import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BillingManager handles the creation of sales bills.
 * It uses database transactions to ensure data integrity (ACID properties).
 * NOTE: Stock deduction is handled by the MySQL trigger, NOT manually here.
 */
public class BillingManager {
    private InventoryManager inventory;
    private CustomerManager customerManager;

    public BillingManager(InventoryManager inventory, CustomerManager customerManager) {
        this.inventory = inventory;
        this.customerManager = customerManager;
    }

    /**
     * Creates a new bill in the database.
     * @return BillResult containing success status and the generated bill.
     */
    public BillResult createBill(List<BillLine> billLines, double discountPct, double amountPaid, int customerId) {
        if (billLines == null || billLines.isEmpty()) return new BillResult(false, "No items in bill", null);

        double subtotal = billLines.stream().mapToDouble(BillLine::getLineTotal).sum();
        double discountAmount = subtotal * (discountPct / 100.0);
        double netTotal = subtotal - discountAmount;
        double amountDue = Math.max(0, netTotal - amountPaid);
        String today = LocalDate.now().toString();
        String now = LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        Connection con = null;
        int billId = -1;

        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false); // Start transaction

            // 1. Insert Bill Header
            try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO bill (bill_date, bill_time, subtotal, discount_pct, discount_amount, net_total, amount_paid, amount_due, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, today); ps.setString(2, now); ps.setDouble(3, subtotal);
                ps.setDouble(4, discountPct); ps.setDouble(5, discountAmount); ps.setDouble(6, netTotal);
                ps.setDouble(7, amountPaid); ps.setDouble(8, amountDue);
                if (customerId > 0) ps.setInt(9, customerId); else ps.setNull(9, Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) billId = rs.getInt(1); }
            }

            if (billId == -1) throw new SQLException("Failed to generate bill ID");

            // 2. Insert Bill Lines
            for (BillLine bl : billLines) {
                try (PreparedStatement linePs = con.prepareStatement(
                    "INSERT INTO bill_line (bill_id, item_id, item_name, price_at_sale, quantity, line_total) VALUES (?, ?, ?, ?, ?, ?)")) {
                    linePs.setInt(1, billId); linePs.setInt(2, bl.getItemId()); linePs.setString(3, bl.getItemName());
                    linePs.setDouble(4, bl.getPriceAtSale()); linePs.setInt(5, bl.getQuantity()); linePs.setDouble(6, bl.getLineTotal());
                    linePs.executeUpdate();
                }
                // NOTE: Stock deduction is handled automatically by the DB trigger 'trg_deduct_stock_on_sale'
            }

            // 3. Record Payment if amount paid > 0
            if (amountPaid > 0 && customerId > 0) {
                String paymentType = (amountDue == 0) ? "FULL_BILL" : "PARTIAL";
                try (PreparedStatement payPs = con.prepareStatement(
                    "INSERT INTO payment (bill_id, customer_id, amount, payment_date, payment_type, note) VALUES (?, ?, ?, ?, ?, ?)")) {
                    payPs.setInt(1, billId); payPs.setInt(2, customerId); payPs.setDouble(3, amountPaid);
                    payPs.setString(4, today); payPs.setString(5, paymentType);
                    payPs.setString(6, amountDue == 0 ? "Paid at checkout" : "Partial payment");
                    payPs.executeUpdate();
                }
            }

            // 4. Update Customer Dues if amount due > 0
            if (customerId > 0 && amountDue > 0) {
                try (PreparedStatement custPs = con.prepareStatement("UPDATE customer SET total_dues = total_dues + ? WHERE customer_id = ?")) {
                    custPs.setDouble(1, amountDue); custPs.setInt(2, customerId); custPs.executeUpdate();
                }
            }

            con.commit(); // Commit transaction if all steps succeed

            inventory.refreshFromDatabase();
            if (customerId > 0) customerManager.refreshFromDatabase();

            Bill bill = new Bill(billId, today, now, subtotal, discountAmount, netTotal, amountPaid, amountDue, "");
            return new BillResult(true, "Bill created successfully!", bill);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            return new BillResult(false, "Error: " + e.getMessage(), null);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) {}
            }
        }
    }

    public List<Bill> getAllBills() { return DatabaseManager.getAllBills(); }
    public List<BillLine> getBillLines(int billId) { return DatabaseManager.getBillLines(billId); }

    /** Formats the bills list for a JTable */
    public javax.swing.table.DefaultTableModel getBillsTableModel() {
        String[] cols = {"Bill ID", "Date", "Time", "Customer", "Subtotal", "Discount", "Net Total", "Paid", "Due"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Bill b : getAllBills()) {
            model.addRow(new Object[]{ b.getBillId(), b.getDate(), b.getTime(), b.getCustomerName(),
                String.format("%.2f", b.getSubtotal()), String.format("%.2f", b.getDiscountAmount()),
                String.format("%.2f", b.getNetTotal()), String.format("%.2f", b.getAmountPaid()), String.format("%.2f", b.getAmountDue()) });
        }
        return model;
    }
}