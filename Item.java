/**
 * The Item class represents a product in the shop's inventory.
 */
public class Item {
    private int itemId;
    private String name;
    private double price;
    private int stock;
    private int categoryId;
    private String categoryName; // ADDED: To display "Beverages" instead of just "1"
    private int supplierId;
    private String supplierName; 

    public Item(int itemId, String name, double price, int stock, int categoryId, int supplierId) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getCategoryId() { return categoryId; }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getCategoryName() { return categoryName; } // ADDED

    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; } // ADDED

    public boolean isLowStock() { return stock < 5; }

    public static Item fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Item(
            rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price"),
            rs.getInt("stock"), rs.getInt("category_id"), rs.getInt("supplier_id")
        );
    }

    @Override
    public String toString() {
        return String.format("%-4d %-28s %10.2f %10d%s", itemId, name, price, stock, (stock < 5 ? "  <-- LOW!" : ""));
    }
}