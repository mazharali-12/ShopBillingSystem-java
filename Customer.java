/**
 * The Customer class represents a shop customer who may buy on credit.
 * It tracks the customer's contact info and their current outstanding dues.
 */
public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private double totalDues;

    /**
     * Constructor to initialize a Customer object.
     */
    public Customer(int customerId, String name, String phone, double totalDues) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.totalDues = totalDues;
    }

    // Getter methods
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public double getTotalDues() { return totalDues; }

    /**
     * Factory method to construct a Customer from a SQL ResultSet.
     */
    public static Customer fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getDouble("total_dues")
        );
    }

    @Override
    public String toString() {
        return String.format("%-4d %-22s %-15s %10.2f", customerId, name, phone, totalDues);
    }
}