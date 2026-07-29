/**
 * The BillResult class is a wrapper used to return the outcome of a billing operation.
 * It encapsulates whether the operation was successful, a message, and the resulting Bill object.
 */
public class BillResult {
    private boolean success;
    private String message;
    private Bill bill;

    /**
     * Constructor for BillResult.
     * @param success True if billing succeeded, false otherwise.
     * @param message A descriptive message (e.g., error details or success note).
     * @param bill    The generated Bill object if successful, null otherwise.
     */
    public BillResult(boolean success, String message, Bill bill) {
        this.success = success;
        this.message = message;
        this.bill = bill;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Bill getBill() { return bill; }
}