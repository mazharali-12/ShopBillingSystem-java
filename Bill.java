/**
 * The Bill class represents a sales transaction (the invoice header).
 * It contains summary information like totals, discounts, and amounts paid/due.
 */
public class Bill {
    private int billId;
    private String date;
    private String time;
    private double subtotal;
    private double discountAmount;
    private double netTotal;
    private double amountPaid;
    private double amountDue;
    private String customerName;

    /** Simplified constructor for list views */
    public Bill(int billId, String date, String customerName, double netTotal, double amountDue) {
        this.billId = billId;
        this.date = date;
        this.customerName = customerName;
        this.netTotal = netTotal;
        this.amountDue = amountDue;
    }

    /** Full constructor for detailed receipt views */
    public Bill(int billId, String date, String time, double subtotal, double discountAmount,
                double netTotal, double amountPaid, double amountDue, String customerName) {
        this.billId = billId;
        this.date = date;
        this.time = time;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.netTotal = netTotal;
        this.amountPaid = amountPaid;
        this.amountDue = amountDue;
        this.customerName = customerName;
    }

    // Getter methods
    public int getBillId() { return billId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public double getSubtotal() { return subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getNetTotal() { return netTotal; }
    public double getAmountPaid() { return amountPaid; }
    public double getAmountDue() { return amountDue; }
    public String getCustomerName() { return customerName; }
}