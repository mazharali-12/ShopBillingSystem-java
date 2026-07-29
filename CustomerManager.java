import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class CustomerManager {
    private List<Customer> customers = new ArrayList<>();

    public CustomerManager() { refreshFromDatabase(); }
    public List<Customer> getCustomers() { return customers; }
    public Customer findById(int id) { for (Customer c : customers) if (c.getCustomerId() == id) return c; return null; }
    public void refreshFromDatabase() { customers.clear(); customers.addAll(DatabaseManager.loadCustomers()); }

    public int addCustomer(String name, String phone) {
        if (name == null || name.trim().isEmpty()) return -1;
        int id = DatabaseManager.insertAndGetId("INSERT INTO customer (name, phone) VALUES (?, ?)", name.trim(), (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null);
        if (id > 0) customers.add(new Customer(id, name.trim(), phone, 0.0));
        return id;
    }

    public boolean toggleCustomerStatus(int customerId) {
        if (DatabaseManager.toggleCustomerStatus(customerId)) { refreshFromDatabase(); return true; }
        return false;
    }

    /**
     * FIXED: Bulletproof settle dues logic.
     * 1. Auto-syncs customer dues with actual bill dues to fix old mismatches.
     * 2. Always deducts from customer table at the end.
     * 3. Always inserts payment records for every bill it touches.
     */
    public double settleDues(int customerId, double paymentAmount) {
        Customer c = DatabaseManager.getCustomerById(customerId);
        if (c == null || c.getTotalDues() <= 0 || paymentAmount <= 0 || paymentAmount > c.getTotalDues()) {
            return -1; 
        }
        
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false); // Start Transaction

            // 1. AUTO-SYNC: Force customer.total_dues to match the exact sum of unpaid bills.
            // This fixes any leftover bugs from previous versions where they didn't match.
            try (PreparedStatement syncPs = con.prepareStatement(
                "UPDATE customer c SET c.total_dues = (SELECT COALESCE(SUM(b.amount_due), 0) FROM bill b WHERE b.customer_id = c.customer_id AND b.amount_due > 0) WHERE c.customer_id = ?")) {
                syncPs.setInt(1, customerId);
                syncPs.executeUpdate();
            }

            // 2. Re-fetch the accurate, synced dues
            double actualDues = 0;
            try (PreparedStatement duesPs = con.prepareStatement("SELECT total_dues FROM customer WHERE customer_id = ?")) {
                duesPs.setInt(1, customerId);
                try (ResultSet rs = duesPs.executeQuery()) {
                    if (rs.next()) actualDues = rs.getDouble("total_dues");
                }
            }

            if (actualDues <= 0) {
                con.commit(); 
                refreshFromDatabase();
                return -1; // Nothing left to pay after sync
            }

            // Cap the payment to what they actually owe
            double payNow = Math.min(paymentAmount, actualDues);
            String today = java.time.LocalDate.now().toString();
            
            // 3. Get unpaid bills ON THIS CONNECTION to prevent isolation issues
            List<Bill> unpaidBills = new ArrayList<>();
            try (PreparedStatement billPs = con.prepareStatement("SELECT bill_id, amount_due FROM bill WHERE customer_id = ? AND amount_due > 0 ORDER BY bill_id")) {
                billPs.setInt(1, customerId);
                try (ResultSet rs = billPs.executeQuery()) {
                    while (rs.next()) unpaidBills.add(new Bill(rs.getInt("bill_id"), null, null, 0, rs.getDouble("amount_due")));
                }
            }

            // 4. Pay off the bills one by one
            double remaining = payNow;
            for (Bill bill : unpaidBills) {
                if (remaining <= 0.001) break; // Stop if we've paid everything (0.001 prevents floating point loops)
                
                double billDue = bill.getAmountDue();
                double payForThisBill = Math.min(remaining, billDue);

                // Update Bill Table
                try (PreparedStatement ps = con.prepareStatement("UPDATE bill SET amount_due = amount_due - ?, amount_paid = amount_paid + ? WHERE bill_id = ?")) {
                    ps.setDouble(1, payForThisBill); ps.setDouble(2, payForThisBill); ps.setInt(3, bill.getBillId());
                    ps.executeUpdate();
                }

                // Insert Payment Record
                String paymentType = (Math.abs(payForThisBill - billDue) < 0.001) ? "DUES_CLEARED" : "PARTIAL";
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO payment (bill_id, customer_id, amount, payment_date, payment_type, note) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, bill.getBillId()); ps.setInt(2, customerId); ps.setDouble(3, payForThisBill);
                    ps.setString(4, today); ps.setString(5, paymentType); ps.setString(6, "Settled dues");
                    ps.executeUpdate();
                }
                remaining -= payForThisBill;
            }

            // 5. CRITICAL FIX: ALWAYS deduct the actually paid amount from customer.total_dues
            // We do this outside the loop so it ALWAYS happens, even if the loop perfectly zeroed out the bills.
            try (PreparedStatement ps = con.prepareStatement("UPDATE customer SET total_dues = total_dues - ? WHERE customer_id = ?")) {
                ps.setDouble(1, payNow); 
                ps.setInt(2, customerId); 
                ps.executeUpdate();
            }

            con.commit(); // COMMIT CHANGES
            refreshFromDatabase();
            
            Customer updated = DatabaseManager.getCustomerById(customerId);
            return updated != null ? updated.getTotalDues() : -1;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {} // ROLLBACK ON ERROR
            System.err.println("Settle Dues Error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (Exception e) {} }
        }
    }

    public DefaultTableModel getTableModel() {
        String[] cols = {"ID", "Name", "Phone", "Total Dues (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        for (Customer c : customers) {
            model.addRow(new Object[]{ c.getCustomerId(), c.getName(), c.getPhone() != null ? c.getPhone() : "", String.format("%.2f", c.getTotalDues()) });
        }
        return model;
    }
}