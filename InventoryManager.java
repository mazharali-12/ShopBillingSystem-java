import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class InventoryManager {
    private List<Item> items = new ArrayList<>();
    private SupplierManager supplierManager;

    public InventoryManager(SupplierManager supplierManager) {
        this.supplierManager = supplierManager;
        refreshFromDatabase();
    }

    public List<Item> getItems() { return items; }
    public Item findById(int id) { for (Item item : items) if (item.getItemId() == id) return item; return null; }
    public void refreshFromDatabase() { items.clear(); items.addAll(DatabaseManager.loadItems()); }

    public int addItem(String name, double price, int stock, int categoryId, int supplierId) {
        if (name == null || name.trim().isEmpty() || price <= 0) return -1;
        int id = DatabaseManager.insertAndGetId("INSERT INTO item (name, price, stock, category_id, supplier_id) VALUES (?, ?, ?, ?, ?)", name.trim(), price, stock, categoryId, supplierId);
        if (id > 0) {
            Item item = new Item(id, name.trim(), price, stock, categoryId, supplierId);
            Supplier supp = supplierManager.findById(supplierId);
            if (supp != null) item.setSupplierName(supp.getName());
            items.add(item);
        }
        return id;
    }

    public boolean restockItem(int itemId, int quantity) {
        Item item = findById(itemId);
        if (item == null || quantity <= 0) return false;
        boolean logged = DatabaseManager.insertAndGetId("INSERT INTO stock_log (item_id, supplier_id, qty_added, log_date, remarks) VALUES (?, ?, ?, ?, ?)", item.getItemId(), item.getSupplierId(), quantity, java.time.LocalDate.now().toString(), "GUI Restock") > 0;
        if (logged) { refreshFromDatabase(); return true; }
        return false;
    }

    public boolean toggleItemStatus(int itemId) {
        if (DatabaseManager.toggleItemStatus(itemId)) { refreshFromDatabase(); return true; }
        return false;
    }

    public List<Item> getLowStockItems() {
        List<Item> lowStock = new ArrayList<>(); for (Item item : items) if (item.isLowStock()) lowStock.add(item); return lowStock;
    }

    public DefaultTableModel getTableModel() {
        String[] cols = {"ID", "Name", "Price (Rs)", "Stock", "Category", "Supplier"}; // Changed header back
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        for (Item item : items) {
            model.addRow(new Object[]{
                item.getItemId(), item.getName(), String.format("%.2f", item.getPrice()),
                item.getStock() + (item.isLowStock() ? " *" : ""),
                item.getCategoryName() != null ? item.getCategoryName() : "ID:" + item.getCategoryId(), // FIXED: Show Category Name
                item.getSupplierName() != null ? item.getSupplierName() : "ID:" + item.getSupplierId()
            });
        }
        return model;
    }
}