/**
 * The Supplier class represents a product supplier in the database.
 * It acts as a data transfer object (DTO) for supplier information.
 */
public class Supplier {
    private int supplierId;
    private String name;
    private String phone;
    private String address;

    /**
     * Constructor to initialize a Supplier object.
     * @param supplierId The unique ID of the supplier in the database.
     * @param name       The name of the supplier.
     * @param phone      The contact number of the supplier.
     * @param address    The physical address of the supplier.
     */
    public Supplier(int supplierId, String name, String phone, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Getter methods to access private fields
    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    /**
     * Factory method to construct a Supplier directly from a SQL ResultSet.
     * Reduces code duplication when reading from the database.
     * @param rs The ResultSet pointing to a supplier row.
     * @return A populated Supplier object.
     */
    public static Supplier fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Supplier(
            rs.getInt("supplier_id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("address")
        );
    }

    @Override
    public String toString() {
        return String.format("%-4d %-25s %-15s", supplierId, name, phone);
    }
}