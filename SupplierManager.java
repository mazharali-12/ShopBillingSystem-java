import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * SupplierManager handles the business logic for suppliers.
 * It acts as an intermediary between the GUI and the DatabaseManager.
 */
public class SupplierManager {
    private List<Supplier> suppliers = new ArrayList<>();

    /** Initializes the manager and loads data from the database immediately */
    public SupplierManager() {
        refreshFromDatabase();
    }

    public List<Supplier> getSuppliers() { return suppliers; }

    /** Finds a specific supplier by its ID in the local list */
    public Supplier findById(int id) {
        for (Supplier s : suppliers) if (s.getSupplierId() == id) return s;
        return null;
    }

    /** Syncs the local Java list with the actual database */
    public void refreshFromDatabase() {
        suppliers.clear();
        suppliers.addAll(DatabaseManager.loadSuppliers());
    }

    /**
     * Adds a new supplier to the database and local list.
     * @return The new supplier ID, or -1 if failed.
     */
    public int addSupplier(String name, String phone, String address) {
        if (name == null || name.trim().isEmpty()) return -1;
        int id = DatabaseManager.insertAndGetId(
            "INSERT INTO supplier (name, phone, address) VALUES (?, ?, ?)", name.trim(), phone, address);
        if (id > 0) {
            suppliers.add(new Supplier(id, name.trim(), phone, address));
        }
        return id;
    }

    /**
     * Formats the supplier list for display in a JTable.
     */
    public DefaultTableModel getTableModel() {
        String[] cols = {"ID", "Name", "Phone", "Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Supplier s : suppliers) {
            model.addRow(new Object[]{ s.getSupplierId(), s.getName(), 
                s.getPhone() != null ? s.getPhone() : "", 
                s.getAddress() != null ? s.getAddress() : "" });
        }
        return model;
    }
}