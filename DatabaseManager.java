import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shop_billing_dbjava";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "@root"; 
    private static boolean driverLoaded = false;

    static { loadDriver(); }

    private static void loadDriver() {
        if (!driverLoaded) {
            try { Class.forName("com.mysql.cj.jdbc.Driver"); driverLoaded = true; } 
            catch (ClassNotFoundException e) { System.err.println("FATAL: MySQL JDBC Driver not found!"); }
        }
    }

    public static boolean isDriverAvailable() { return driverLoaded; }
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) throw new SQLException("Driver not loaded.");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    public static boolean testConnection() { try (Connection c = getConnection()) { return c.isValid(5); } catch (Exception e) { return false; } }

    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) { if (params[i] == null) ps.setNull(i + 1, Types.NULL); else ps.setObject(i + 1, params[i]); }
    }

    public static int insertAndGetId(String sql, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params); ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { System.err.println("DB Insert Error: " + e.getMessage()); }
        return -1;
    }

    public static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params); ps.executeUpdate(); return true;
        } catch (SQLException e) { System.err.println("DB Update Error: " + e.getMessage()); return false; }
    }

    // --- LOAD METHODS ---

    public static List<Supplier> loadSuppliers() {
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM supplier ORDER BY supplier_id")) {
            while (rs.next()) list.add(Supplier.fromResultSet(rs));
        } catch (SQLException e) { System.err.println("Error loading suppliers: " + e.getMessage()); }
        return list;
    }

    public static List<Item> loadItems() {
        List<Item> list = new ArrayList<>();
        // FIXED: Added LEFT JOIN category to fetch category_name
        String sql = "SELECT i.*, s.name as supplier_name, c.name as category_name FROM item i " +
                     "LEFT JOIN supplier s ON i.supplier_id = s.supplier_id " +
                     "LEFT JOIN category c ON i.category_id = c.category_id " +
                     "WHERE i.is_active = 1 ORDER BY i.item_id";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Item item = Item.fromResultSet(rs);
                try { item.setSupplierName(rs.getString("supplier_name")); } catch (Exception ignored) {}
                try { item.setCategoryName(rs.getString("category_name")); } catch (Exception ignored) {} // ADDED
                list.add(item);
            }
        } catch (SQLException e) { System.err.println("Error loading items: " + e.getMessage()); }
        return list;
    }

    public static List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM customer WHERE is_active = 1 ORDER BY customer_id")) {
            while (rs.next()) list.add(Customer.fromResultSet(rs));
        } catch (SQLException e) { System.err.println("Error loading customers: " + e.getMessage()); }
        return list;
    }

    public static Customer getCustomerById(int customerId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM customer WHERE customer_id = ? AND is_active = 1")) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Customer.fromResultSet(rs); }
        } catch (SQLException e) { System.err.println("Error getting customer: " + e.getMessage()); }
        return null;
    }

    public static Item getItemById(int itemId) {
        // FIXED: Added category join here too
        String sql = "SELECT i.*, s.name as supplier_name, c.name as category_name FROM item i " +
                     "LEFT JOIN supplier s ON i.supplier_id = s.supplier_id " +
                     "LEFT JOIN category c ON i.category_id = c.category_id " +
                     "WHERE i.item_id = ? AND i.is_active = 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Item item = Item.fromResultSet(rs);
                    try { item.setSupplierName(rs.getString("supplier_name")); } catch (Exception ignored) {}
                    try { item.setCategoryName(rs.getString("category_name")); } catch (Exception ignored) {}
                    return item;
                }
            }
        } catch (SQLException e) { System.err.println("Error getting item: " + e.getMessage()); }
        return null;
    }

    public static List<Bill> getUnpaidBills(int customerId) {
        List<Bill> bills = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT bill_id, bill_date, net_total, amount_due FROM bill WHERE customer_id = ? AND amount_due > 0")) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bills.add(new Bill(rs.getInt("bill_id"), rs.getString("bill_date"), null, rs.getDouble("net_total"), rs.getDouble("amount_due")));
            }
        } catch (SQLException e) { System.err.println("Error getting unpaid bills: " + e.getMessage()); }
        return bills;
    }

    public static List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.bill_id, b.bill_date, b.bill_time, b.subtotal, b.discount_amount, b.net_total, b.amount_paid, b.amount_due, COALESCE(c.name, 'Walk-in') as cust_name " +
                     "FROM bill b LEFT JOIN customer c ON b.customer_id = c.customer_id ORDER BY b.bill_id DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) bills.add(new Bill(rs.getInt("bill_id"), rs.getString("bill_date"), rs.getString("bill_time"), rs.getDouble("subtotal"), rs.getDouble("discount_amount"), rs.getDouble("net_total"), rs.getDouble("amount_paid"), rs.getDouble("amount_due"), rs.getString("cust_name")));
        } catch (SQLException e) { System.err.println("Error loading bills: " + e.getMessage()); }
        return bills;
    }

    public static List<BillLine> getBillLines(int billId) {
        List<BillLine> lines = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM bill_line WHERE bill_id = ?")) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lines.add(new BillLine(rs.getInt("bill_id"), rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("price_at_sale"), rs.getInt("quantity"))); }
        } catch (SQLException e) { System.err.println("Error loading bill lines: " + e.getMessage()); }
        return lines;
    }

    // ADDED: Load Payments
    public static List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as cust_name FROM payment p JOIN customer c ON p.customer_id = c.customer_id ORDER BY p.payment_date DESC, p.payment_id DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(Payment.fromResultSet(rs));
        } catch (SQLException e) { System.err.println("Error loading payments: " + e.getMessage()); }
        return list;
    }

    // --- SOFT DELETE METHODS ---
    public static boolean toggleItemStatus(int itemId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE item SET is_active = IF(is_active = 1, 0, 1) WHERE item_id = ?")) {
            ps.setInt(1, itemId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean toggleCustomerStatus(int customerId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE customer SET is_active = IF(is_active = 1, 0, 1) WHERE customer_id = ?")) {
            ps.setInt(1, customerId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}