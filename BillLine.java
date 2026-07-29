/**
 * The BillLine class represents an individual item added to a Bill.
 * It acts as the junction between a Bill and an Item, capturing the price at the time of sale.
 */
public class BillLine {
    private int billId;
    private int itemId;
    private int quantity;
    private String itemName;
    private double priceAtSale;
    private double lineTotal;

    /**
     * Constructor to initialize a BillLine object.
     */
    public BillLine(int billId, int itemId, String itemName, double priceAtSale, int quantity) {
        this.billId = billId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.priceAtSale = priceAtSale;
        this.quantity = quantity;
        this.lineTotal = priceAtSale * quantity; // Auto-calculate total
    }

    // Getter methods
    public int getBillId() { return billId; }
    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public String getItemName() { return itemName; }
    public double getPriceAtSale() { return priceAtSale; }
    public double getLineTotal() { return lineTotal; }
}