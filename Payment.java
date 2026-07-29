import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Payment class represents a financial transaction against a bill.
 * It tracks how much was paid, when, and what type of payment it was.
 */
public class Payment {
    private int paymentId;
    private int billId;
    private int customerId;
    private String customerName;
    private double amount;
    private String paymentDate;
    private String paymentType;
    private String note;

    public Payment(int paymentId, int billId, int customerId, String customerName, 
                   double amount, String paymentDate, String paymentType, String note) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.note = note;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getBillId() { return billId; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentType() { return paymentType; }
    public String getNote() { return note; }

    /**
     * Factory method to construct a Payment from a SQL ResultSet.
     * Assumes the query JOINs the customer table to get the customer name.
     */
    public static Payment fromResultSet(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("payment_id"),
            rs.getInt("bill_id"),
            rs.getInt("customer_id"),
            rs.getString("cust_name"), // Joined column
            rs.getDouble("amount"),
            rs.getString("payment_date"),
            rs.getString("payment_type"),
            rs.getString("note")
        );
    }
}