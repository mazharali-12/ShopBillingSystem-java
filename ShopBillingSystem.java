import java.sql.*;
import java.util.ArrayList;
import java.util.List; 
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ShopBillingSystem extends JFrame {
    private SupplierManager supplierManager;
    private InventoryManager inventoryManager;
    private CustomerManager customerManager;
    private BillingManager billingManager;

    private JTabbedPane tabbedPane;
    private JTable supplierTable, itemTable, customerTable, billTable, paymentTable;
    private DefaultTableModel supplierModel, itemModel, customerModel, billModel, paymentModel;
    private JLabel statusLabel;

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);

    public ShopBillingSystem() {
        supplierManager = new SupplierManager();
        inventoryManager = new InventoryManager(supplierManager);
        customerManager = new CustomerManager();
        billingManager = new BillingManager(inventoryManager, customerManager);

        setTitle("Shop Retail Billing System");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createMenuBar();
        createMainPanel();
        createStatusBar();
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); SwingUtilities.updateComponentTreeUI(this); } catch (Exception e) {}
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File"); JMenuItem exitItem = new JMenuItem("Exit"); exitItem.addActionListener(e -> System.exit(0)); fileMenu.add(exitItem); menuBar.add(fileMenu);
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem lowStockItem = new JMenuItem("Low Stock Items"); lowStockItem.addActionListener(e -> showLowStockReport()); reportsMenu.add(lowStockItem);
        JMenuItem dailySalesItem = new JMenuItem("Today's Sales"); dailySalesItem.addActionListener(e -> showDailySalesReport()); reportsMenu.add(dailySalesItem);
        menuBar.add(reportsMenu);
        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        tabbedPane = new JTabbedPane(); tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.addTab("Inventory", createInventoryPanel());
        tabbedPane.addTab("Customers", createCustomerPanel());
        tabbedPane.addTab("Suppliers", createSupplierPanel());
        tabbedPane.addTab("Billing", createBillingPanel());
        tabbedPane.addTab("Bills History", createBillsHistoryPanel());
        tabbedPane.addTab("Payments", createPaymentsPanel()); // ADDED PAYMENTS TAB
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Inventory Management"), BorderLayout.NORTH);
        itemModel = inventoryManager.getTableModel(); itemTable = new JTable(itemModel); itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); itemTable.setRowHeight(25);
        itemTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 3 && value.toString().contains("*")) { c.setForeground(DANGER_COLOR); setFont(getFont().deriveFont(Font.BOLD)); }
                else if (!isSelected) { c.setForeground(table.getForeground()); setFont(getFont().deriveFont(Font.PLAIN)); }
                return c;
            }
        });
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = createStyledButton("Add Item", SUCCESS_COLOR); addButton.addActionListener(e -> showAddItemDialog());
        JButton restockButton = createStyledButton("Restock", PRIMARY_COLOR); restockButton.addActionListener(e -> showRestockDialog());
        JButton toggleButton = createStyledButton("Deactivate/Activate", WARNING_COLOR); toggleButton.addActionListener(e -> showToggleItemDialog());
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY); refreshButton.addActionListener(e -> refreshInventoryTable());
        buttonPanel.add(addButton); buttonPanel.add(restockButton); buttonPanel.add(toggleButton); buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH); return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Customer Management"), BorderLayout.NORTH);
        customerModel = customerManager.getTableModel(); customerTable = new JTable(customerModel); customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); customerTable.setRowHeight(25);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = createStyledButton("Add Customer", SUCCESS_COLOR); addButton.addActionListener(e -> showAddCustomerDialog());
        JButton settleButton = createStyledButton("Settle Dues", PRIMARY_COLOR); settleButton.addActionListener(e -> showSettleDuesDialog());
        JButton toggleCustButton = createStyledButton("Deactivate/Activate", WARNING_COLOR); toggleCustButton.addActionListener(e -> showToggleCustomerDialog());
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY); refreshButton.addActionListener(e -> refreshCustomerTable());
        buttonPanel.add(addButton); buttonPanel.add(settleButton); buttonPanel.add(toggleCustButton); buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH); return panel;
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Supplier Management"), BorderLayout.NORTH);
        supplierModel = supplierManager.getTableModel(); supplierTable = new JTable(supplierModel); supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); supplierTable.setRowHeight(25);
        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = createStyledButton("Add Supplier", SUCCESS_COLOR); addButton.addActionListener(e -> showAddSupplierDialog());
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY); refreshButton.addActionListener(e -> refreshSupplierTable());
        buttonPanel.add(addButton); buttonPanel.add(refreshButton); panel.add(buttonPanel, BorderLayout.SOUTH); return panel;
    }

    // ADDED: PAYMENTS TAB
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Payment History"), BorderLayout.NORTH);
        paymentModel = getPaymentsModel(); paymentTable = new JTable(paymentModel); paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); paymentTable.setRowHeight(25);
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY); refreshButton.addActionListener(e -> refreshPaymentsTable());
        buttonPanel.add(refreshButton); panel.add(buttonPanel, BorderLayout.SOUTH); return panel;
    }

    private DefaultTableModel getPaymentsModel() {
        String[] cols = {"Payment ID", "Date", "Customer", "Bill ID", "Amount (Rs)", "Type", "Note"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        for (Payment p : DatabaseManager.loadPayments()) {
            model.addRow(new Object[]{ p.getPaymentId(), p.getPaymentDate(), p.getCustomerName(), p.getBillId(), String.format("%.2f", p.getAmount()), p.getPaymentType(), p.getNote() });
        }
        return model;
    }
    private void refreshPaymentsTable() { paymentTable.setModel(getPaymentsModel()); }


    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Create New Bill"), BorderLayout.NORTH);
        JPanel billingArea = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5)); leftPanel.setBorder(BorderFactory.createTitledBorder("Select Items"));
        JPanel selectionPanel = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx=0;gbc.gridy=0;selectionPanel.add(new JLabel("Item ID:"),gbc);gbc.gridx=1;gbc.weightx=1.0;JTextField itemIdField=new JTextField(10);selectionPanel.add(itemIdField,gbc);
        gbc.gridx=0;gbc.gridy=1;gbc.weightx=0;selectionPanel.add(new JLabel("Quantity:"),gbc);gbc.gridx=1;gbc.weightx=1.0;JTextField qtyField=new JTextField(10);selectionPanel.add(qtyField,gbc);
        gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=2;JButton addItemBtn=createStyledButton("Add to Bill",SUCCESS_COLOR);selectionPanel.add(addItemBtn,gbc);
        leftPanel.add(selectionPanel, BorderLayout.NORTH);
        DefaultTableModel billItemsModel = new DefaultTableModel(new String[]{"Item ID", "Name", "Price", "Qty", "Total"}, 0) {@Override public boolean isCellEditable(int row, int column) {return false;}};
        JTable billItemsTable = new JTable(billItemsModel); billItemsTable.setRowHeight(25); leftPanel.add(new JScrollPane(billItemsTable), BorderLayout.CENTER); billingArea.add(leftPanel);
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5)); rightPanel.setBorder(BorderFactory.createTitledBorder("Bill Summary"));
        JPanel summaryPanel = new JPanel(new GridBagLayout()); GridBagConstraints gbc2 = new GridBagConstraints(); gbc2.insets = new Insets(5, 5, 5, 5); gbc2.fill = GridBagConstraints.HORIZONTAL;
        JLabel subtotalLabel = new JLabel("Subtotal: Rs. 0.00"); JLabel discountLabel = new JLabel("Discount: Rs. 0.00"); JLabel netLabel = new JLabel("Net Total: Rs. 0.00");
        JTextField discountField = new JTextField("0", 10); JTextField paidField = new JTextField(10); JTextField customerIdField = new JTextField("0");
        gbc2.gridx=0;gbc2.gridy=0;summaryPanel.add(subtotalLabel,gbc2);gbc2.gridy=1;summaryPanel.add(new JLabel("Discount %:"),gbc2);gbc2.gridx=1;summaryPanel.add(discountField,gbc2);
        gbc2.gridx=0;gbc2.gridy=2;summaryPanel.add(discountLabel,gbc2);gbc2.gridy=3;summaryPanel.add(netLabel,gbc2);gbc2.gridy=4;summaryPanel.add(new JLabel("Amount Paid:"),gbc2);gbc2.gridx=1;summaryPanel.add(paidField,gbc2);
        gbc2.gridx=0;gbc2.gridy=5;summaryPanel.add(new JLabel("Customer ID (0=Walk-in):"),gbc2);gbc2.gridx=1;summaryPanel.add(customerIdField,gbc2);
        JButton generateBillBtn = createStyledButton("Generate Bill", PRIMARY_COLOR); JButton clearBtn = createStyledButton("Clear", DANGER_COLOR);
        gbc2.gridx=0;gbc2.gridy=6;gbc2.gridwidth=2;gbc2.fill=GridBagConstraints.CENTER;summaryPanel.add(generateBillBtn,gbc2);gbc2.gridy=7;summaryPanel.add(clearBtn,gbc2);
        rightPanel.add(summaryPanel, BorderLayout.CENTER); billingArea.add(rightPanel); panel.add(billingArea, BorderLayout.CENTER);
        final JTextField fItemId=itemIdField,fQty=qtyField,fDisc=discountField,fPaid=paidField,fCustId=customerIdField;
        final JLabel fSub=subtotalLabel,fDiscLbl=discountLabel,fNet=netLabel; final DefaultTableModel fModel=billItemsModel;
        List<BillLine> currentBillLines = new ArrayList<>(); double[] subtotal = {0.0};
        addItemBtn.addActionListener(e -> { try { int id=Integer.parseInt(fItemId.getText().trim()),qty=Integer.parseInt(fQty.getText().trim()); if(qty<=0){JOptionPane.showMessageDialog(this,"Quantity must be positive!");return;} Item item=inventoryManager.findById(id); if(item==null){JOptionPane.showMessageDialog(this,"Item not found!");return;} if(item.getStock()<qty){JOptionPane.showMessageDialog(this,"Insufficient stock! Available: "+item.getStock());return;} BillLine line=new BillLine(-1,item.getItemId(),item.getName(),item.getPrice(),qty); currentBillLines.add(line); subtotal[0]+=line.getLineTotal(); fModel.addRow(new Object[]{item.getItemId(),item.getName(),String.format("%.2f",item.getPrice()),qty,String.format("%.2f",line.getLineTotal())}); updateBillSummary(fSub,fDiscLbl,fNet,fDisc,subtotal[0]); fItemId.setText("");fQty.setText("");fItemId.requestFocus(); } catch (NumberFormatException ex) {JOptionPane.showMessageDialog(this,"Enter valid numbers!");} });
        fDisc.getDocument().addDocumentListener(new SimpleDocumentListener(() -> updateBillSummary(fSub,fDiscLbl,fNet,fDisc,subtotal[0])));
        clearBtn.addActionListener(e -> { currentBillLines.clear();subtotal[0]=0;fModel.setRowCount(0);fItemId.setText("");fQty.setText("");fDisc.setText("0");fPaid.setText("");fCustId.setText("0");updateBillSummary(fSub,fDiscLbl,fNet,fDisc,0); });
                        generateBillBtn.addActionListener(e -> { 
            if(currentBillLines.isEmpty()){ JOptionPane.showMessageDialog(this,"Add items first!"); return; } 
            try { 
                double discPct = Double.parseDouble(fDisc.getText().trim());
                double paid = Double.parseDouble(fPaid.getText().trim());
                double custId = Double.parseDouble(fCustId.getText().trim());
                
                // 1. CHECK FOR -1: Intercept request to create a new customer
                if ((int)custId == -1) {
                    int newCustId = promptCreateNewCustomer();
                    if (newCustId == -1) {
                        JOptionPane.showMessageDialog(this, "Bill cancelled. A customer is required for credit.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                        return; // Stop if they cancelled the creation
                    }
                    custId = newCustId; // Swap -1 with the actual new ID
                }
                
                // 2. PREVENT LOSS OF DUES: Calculate expected due
                double tempSubtotal = currentBillLines.stream().mapToDouble(BillLine::getLineTotal).sum();
                double tempDiscount = tempSubtotal * (discPct / 100.0);
                double tempNet = tempSubtotal - tempDiscount;
                double expectedDue = Math.max(0, tempNet - paid);
                
                // 3. VALIDATION: If there is a due amount, Customer ID CANNOT be 0 (Walk-in)
                if (expectedDue > 0 && (int)custId == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Amount due is Rs. " + String.format("%.2f", expectedDue) + 
                        "\nYou MUST enter a valid Customer ID (or -1 to create new)!", 
                        "Credit Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                
                // 4. VALIDATION: Make sure customer actually exists if ID > 0
                if ((int)custId > 0 && customerManager.findById((int)custId) == null) {
                    JOptionPane.showMessageDialog(this, "Customer ID " + (int)custId + " does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                // 5. GENERATE BILL with the verified correct ID
                BillResult res = billingManager.createBill(new ArrayList<>(currentBillLines), discPct, paid, (int)custId); 
                
                if(res.isSuccess()){
                    showBillReceipt(res.getBill(), currentBillLines, discPct, paid);
                    currentBillLines.clear(); subtotal[0] = 0; fModel.setRowCount(0); 
                    fDisc.setText("0"); fPaid.setText(""); fCustId.setText("0");
                    updateBillSummary(fSub, fDiscLbl, fNet, fDisc, 0);
                    refreshInventoryTable(); refreshCustomerTable(); refreshPaymentsTable();
                    setStatus("Bill #" + res.getBill().getBillId() + " created!", SUCCESS_COLOR);
                } else { 
                    JOptionPane.showMessageDialog(this, res.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                } 
            } catch (NumberFormatException ex) { 
                JOptionPane.showMessageDialog(this, "Enter valid numbers!"); 
            } 
        });        return panel;
    }

    private JPanel createBillsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); panel.add(new JLabel("Bills History"), BorderLayout.NORTH);
        billModel = billingManager.getBillsTableModel(); billTable = new JTable(billModel); billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); billTable.setRowHeight(25); panel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY); refreshButton.addActionListener(e -> refreshBillsTable());
        JButton viewDetailsButton = createStyledButton("View Details", PRIMARY_COLOR); viewDetailsButton.addActionListener(e -> { int row=billTable.getSelectedRow(); if(row>=0)showBillDetailsDialog((int)billTable.getValueAt(row,0)); else JOptionPane.showMessageDialog(this,"Select a bill!"); });
        buttonPanel.add(refreshButton); buttonPanel.add(viewDetailsButton); panel.add(buttonPanel, BorderLayout.SOUTH); return panel;
    }

    private void createStatusBar() { statusLabel = new JLabel(" Ready"); statusLabel.setBorder(BorderFactory.createEtchedBorder()); add(statusLabel, BorderLayout.SOUTH); }
    private JButton createStyledButton(String text, Color bgColor) { JButton b = new JButton(text); b.setBackground(bgColor); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 11)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b; }
    private void updateBillSummary(JLabel sub,JLabel disc,JLabel net,JTextField discField,double subtotalVal) { double d=0;try{d=Double.parseDouble(discField.getText().trim());}catch(Exception ignored){} double discAmt=subtotalVal*(d/100.0),netVal=subtotalVal-discAmt; sub.setText(String.format("Subtotal: Rs. %.2f",subtotalVal)); disc.setText(String.format("Discount: Rs. %.2f",discAmt)); net.setText(String.format("Net Total: Rs. %.2f",netVal)); }
    private void setStatus(String msg, Color color) { statusLabel.setText(" " + msg); statusLabel.setForeground(color); }

    // --- DIALOGS ---
    private void showAddItemDialog() {
        JDialog d = new JDialog(this, "Add New Item", true); d.setSize(400, 300); d.setLocationRelativeTo(this); d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10,10,10,10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField nameF = new JTextField(20), priceF = new JTextField(20), stockF = new JTextField(20);
        // FIXED: Combobox now exactly matches your SQL category table names
        JComboBox<String> catC = new JComboBox<>(new String[]{"1-Beverages","2-Snacks","3-Dairy","4-Bakery","5-Stationery","6-Cleaning","7-Personal Care","8-Frozen Foods","9-Dry Goods","10-Tobacco"});
        JComboBox<Supplier> supC = new JComboBox<>(); for(Supplier s : supplierManager.getSuppliers()) supC.addItem(s);
        gbc.gridx=0;gbc.gridy=0;d.add(new JLabel("Name:"),gbc);gbc.gridx=1;d.add(nameF,gbc);
        gbc.gridx=0;gbc.gridy=1;d.add(new JLabel("Price:"),gbc);gbc.gridx=1;d.add(priceF,gbc);
        gbc.gridx=0;gbc.gridy=2;d.add(new JLabel("Stock:"),gbc);gbc.gridx=1;d.add(stockF,gbc);
        gbc.gridx=0;gbc.gridy=3;d.add(new JLabel("Category:"),gbc);gbc.gridx=1;d.add(catC,gbc);
        gbc.gridx=0;gbc.gridy=4;d.add(new JLabel("Supplier:"),gbc);gbc.gridx=1;d.add(supC,gbc);
        gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=2;gbc.fill=GridBagConstraints.CENTER;d.add(createStyledButton("Save", SUCCESS_COLOR),gbc);
        ((JButton)d.getContentPane().getComponent(10)).addActionListener(e -> { try { if(nameF.getText().trim().isEmpty()||Double.parseDouble(priceF.getText().trim())<=0){JOptionPane.showMessageDialog(d,"Invalid input!");return;} int id=inventoryManager.addItem(nameF.getText().trim(),Double.parseDouble(priceF.getText().trim()),Integer.parseInt(stockF.getText().trim()),catC.getSelectedIndex()+1,((Supplier)supC.getSelectedItem()).getSupplierId()); if(id>0){JOptionPane.showMessageDialog(d,"Item Added! ID: "+id);refreshInventoryTable();d.dispose();setStatus("Item added!",SUCCESS_COLOR);} } catch (Exception ex) {JOptionPane.showMessageDialog(d,"Enter valid numbers!");} });
        d.setVisible(true);
    }

    private void showAddCustomerDialog() {
        JDialog d = new JDialog(this, "Add Customer", true); d.setSize(350, 200); d.setLocationRelativeTo(this); d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10,10,10,10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField nameF = new JTextField(20), phoneF = new JTextField(20);
        gbc.gridx=0;gbc.gridy=0;d.add(new JLabel("Name:"),gbc);gbc.gridx=1;d.add(nameF,gbc);
        gbc.gridx=0;gbc.gridy=1;d.add(new JLabel("Phone:"),gbc);gbc.gridx=1;d.add(phoneF,gbc);
        gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=2;gbc.fill=GridBagConstraints.CENTER;d.add(createStyledButton("Save", SUCCESS_COLOR),gbc);
        ((JButton)d.getContentPane().getComponent(4)).addActionListener(e -> { if(nameF.getText().trim().isEmpty()){JOptionPane.showMessageDialog(d,"Name required!");return;} int id=customerManager.addCustomer(nameF.getText().trim(),phoneF.getText().trim()); if(id>0){JOptionPane.showMessageDialog(d,"Customer Added! ID: "+id);refreshCustomerTable();d.dispose();setStatus("Customer added!",SUCCESS_COLOR);} });
        d.setVisible(true);
    }
    
        /** Helper method specifically for billing: Creates customer and returns the ID */
    private int promptCreateNewCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter new Customer Name:");
        if (name == null || name.trim().isEmpty()) {
            return -1; // User clicked cancel or entered blank
        }
        
        String phone = JOptionPane.showInputDialog(this, "Enter phone (optional):");
        
        int newId = customerManager.addCustomer(name.trim(), phone);
        if (newId > 0) {
            JOptionPane.showMessageDialog(this, "Customer '" + name.trim() + "' created with ID: " + newId);
            refreshCustomerTable();
            return newId; // Success! Return the new ID
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create customer!", "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void showAddSupplierDialog() {
        JDialog d = new JDialog(this, "Add Supplier", true); d.setSize(400, 250); d.setLocationRelativeTo(this); d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10,10,10,10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField nameF = new JTextField(20), phoneF = new JTextField(20), addrF = new JTextField(20);
        gbc.gridx=0;gbc.gridy=0;d.add(new JLabel("Name:"),gbc);gbc.gridx=1;d.add(nameF,gbc);
        gbc.gridx=0;gbc.gridy=1;d.add(new JLabel("Phone:"),gbc);gbc.gridx=1;d.add(phoneF,gbc);
        gbc.gridx=0;gbc.gridy=2;d.add(new JLabel("Address:"),gbc);gbc.gridx=1;d.add(addrF,gbc);
        gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=2;gbc.fill=GridBagConstraints.CENTER;d.add(createStyledButton("Save", SUCCESS_COLOR),gbc);
        ((JButton)d.getContentPane().getComponent(6)).addActionListener(e -> { if(nameF.getText().trim().isEmpty()){JOptionPane.showMessageDialog(d,"Name required!");return;} int id=supplierManager.addSupplier(nameF.getText().trim(),phoneF.getText().trim(),addrF.getText().trim()); if(id>0){JOptionPane.showMessageDialog(d,"Supplier Added! ID: "+id);refreshSupplierTable();d.dispose();setStatus("Supplier added!",SUCCESS_COLOR);} });
        d.setVisible(true);
    }

    private void showRestockDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Item ID to restock:"); if(idStr==null) return;
        try { int id=Integer.parseInt(idStr.trim()); Item item=inventoryManager.findById(id); if(item==null){JOptionPane.showMessageDialog(this,"Not found!");return;} String qtyStr=JOptionPane.showInputDialog(this,"Item: "+item.getName()+"\nCurrent Stock: "+item.getStock()+"\n\nQuantity to add:"); if(qtyStr==null) return; int qty=Integer.parseInt(qtyStr.trim()); if(inventoryManager.restockItem(id,qty)){JOptionPane.showMessageDialog(this,"Restocked!");refreshInventoryTable();setStatus("Restocked!",SUCCESS_COLOR);} else JOptionPane.showMessageDialog(this,"Failed!"); } catch (Exception ex) {JOptionPane.showMessageDialog(this,"Invalid input!");}
    }

    private void showSettleDuesDialog() {
        int row = customerTable.getSelectedRow(); int cId;
        if(row>=0) cId = (int) customerTable.getValueAt(row, 0);
        else { String s = JOptionPane.showInputDialog(this, "Enter Customer ID:"); if(s==null) return; try{cId=Integer.parseInt(s.trim());}catch(Exception e){return;} }
        Customer c = DatabaseManager.getCustomerById(cId);
        if(c==null||c.getTotalDues()<=0){JOptionPane.showMessageDialog(this,"No dues or not found!");return;}
        String pStr = JOptionPane.showInputDialog(this, "Customer: "+c.getName()+"\nDues: Rs. "+String.format("%.2f",c.getTotalDues())+"\n\nPayment Amount:");
        if(pStr==null) return;
        try { double p = Double.parseDouble(pStr.trim()); double rem = customerManager.settleDues(cId, p); if(rem>=0){JOptionPane.showMessageDialog(this,"Paid! Remaining: Rs. "+String.format("%.2f",rem));refreshCustomerTable();refreshBillsTable();refreshPaymentsTable();setStatus("Payment recorded!",SUCCESS_COLOR);} else JOptionPane.showMessageDialog(this,"Failed! Check console."); } catch (Exception ex) {JOptionPane.showMessageDialog(this,"Invalid amount!");}
    }

    private void showToggleItemDialog() {
        int id;
        
        int row = itemTable.getSelectedRow();
        if (row >= 0) {
            id = (int) itemTable.getValueAt(row, 0);
            String name = (String) itemTable.getValueAt(row, 1);
            
            int c = JOptionPane.showConfirmDialog(this, "Toggle status for: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
        } else {
            String idStr = JOptionPane.showInputDialog(this, "No item selected.\nEnter Item ID to Activate/Deactivate:");
            if (idStr == null) return;
            
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (inventoryManager.toggleItemStatus(id)) {
            JOptionPane.showMessageDialog(this, "Status toggled successfully! (If deactivated, it is hidden. If activated, it will appear after refresh).");
            refreshInventoryTable();
            setStatus("Item status changed!", WARNING_COLOR);
        } else {
            JOptionPane.showMessageDialog(this, "Failed! Make sure the ID actually exists in the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showToggleCustomerDialog() {
        int id;
        
        // CHECK: Did they select a row in the table?
        int row = customerTable.getSelectedRow();
        if (row >= 0) {
            // Yes, get ID from the selected row
            id = (int) customerTable.getValueAt(row, 0);
            String name = (String) customerTable.getValueAt(row, 1);
            
            int c = JOptionPane.showConfirmDialog(this, "Toggle status for: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return; // They clicked No
        } else {
            // No row selected. Ask them for an ID (Used for REACTIVATING hidden customers)
            String idStr = JOptionPane.showInputDialog(this, "No customer selected.\nEnter Customer ID to Activate/Deactivate:");
            if (idStr == null) return; // They clicked Cancel
            
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Perform the toggle in the database
        if (customerManager.toggleCustomerStatus(id)) {
            JOptionPane.showMessageDialog(this, "Status toggled successfully! (If deactivated, it is hidden. If activated, it will appear after refresh).");
            refreshCustomerTable();
            setStatus("Customer status changed!", WARNING_COLOR);
        } else {
            JOptionPane.showMessageDialog(this, "Failed! Make sure the ID actually exists in the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBillReceipt(Bill bill, List<BillLine> lines, double discPct, double paid) {
        StringBuilder r = new StringBuilder(); r.append("========== SHOP RECEIPT ==========\n"); r.append(String.format("Bill #: %d | Date: %s\n",bill.getBillId(),bill.getDate())); r.append("----------------------------------\n");
        for(BillLine bl : lines) r.append(String.format("%-20s x%-3d %7.2f\n",bl.getItemName(),bl.getQuantity(),bl.getLineTotal()));
        r.append("----------------------------------\n"); r.append(String.format("Subtotal:       %10.2f\n",bill.getSubtotal())); r.append(String.format("Discount:       -%9.2f\n",bill.getDiscountAmount()));
        r.append(String.format("NET TOTAL:      %10.2f\n",bill.getNetTotal())); r.append(String.format("Paid:           %10.2f\n",bill.getAmountPaid())); r.append(String.format("Due:            %10.2f\n",bill.getAmountDue())); r.append("==================================\n");
        JTextArea ta = new JTextArea(r.toString()); ta.setEditable(false); ta.setFont(new Font("Monospaced", Font.PLAIN, 12)); JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Receipt #" + bill.getBillId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBillDetailsDialog(int billId) {
        List<BillLine> lines = billingManager.getBillLines(billId); if(lines.isEmpty()){JOptionPane.showMessageDialog(this,"No items!");return;}
        DefaultTableModel m = new DefaultTableModel(new String[]{"Item ID","Name","Price","Qty","Total"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
        for(BillLine bl : lines) m.addRow(new Object[]{bl.getItemId(),bl.getItemName(),String.format("%.2f",bl.getPriceAtSale()),bl.getQuantity(),String.format("%.2f",bl.getLineTotal())});
        JTable t = new JTable(m); t.setRowHeight(25); JOptionPane.showMessageDialog(this, new JScrollPane(t), "Bill #" + billId + " Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLowStockReport() {
        List<Item> low = inventoryManager.getLowStockItems(); if(low.isEmpty()){JOptionPane.showMessageDialog(this,"No low stock!");return;}
        StringBuilder r = new StringBuilder("LOW STOCK ITEMS (< 5)\n\n"); for(Item i : low) r.append(String.format("ID: %-4d | %-25s | Stock: %d\n",i.getItemId(),i.getName(),i.getStock()));
        JTextArea ta = new JTextArea(r.toString()); ta.setEditable(false); ta.setFont(new Font("Monospaced", Font.PLAIN, 12)); JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Low Stock", JOptionPane.WARNING_MESSAGE);
    }

    private void showDailySalesReport() {
        String today = java.time.LocalDate.now().toString();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as cnt, COALESCE(SUM(net_total),0) as sales, COALESCE(SUM(amount_paid),0) as paid FROM bill WHERE bill_date = ?")) {
            ps.setString(1, today); ResultSet rs = ps.executeQuery();
            if(rs.next()) JOptionPane.showMessageDialog(this, String.format("Today's Sales (%s)\n\nBills: %d\nTotal Sales: Rs. %.2f\nTotal Paid: Rs. %.2f",today,rs.getInt("cnt"),rs.getDouble("sales"),rs.getDouble("paid")), "Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "DB Error!"); }
    }

    private void refreshInventoryTable() { inventoryManager.refreshFromDatabase(); itemTable.setModel(inventoryManager.getTableModel()); }
    private void refreshCustomerTable() { customerManager.refreshFromDatabase(); customerTable.setModel(customerManager.getTableModel()); }
    private void refreshSupplierTable() { supplierManager.refreshFromDatabase(); supplierTable.setModel(supplierManager.getTableModel()); }
    private void refreshBillsTable() { billTable.setModel(billingManager.getBillsTableModel()); }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener { private final Runnable callback; public SimpleDocumentListener(Runnable callback) { this.callback = callback; } @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { callback.run(); } @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { callback.run(); } @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { callback.run(); } }

    public static void main(String[] args) {
        if (!DatabaseManager.isDriverAvailable()) { JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Fatal Error", JOptionPane.ERROR_MESSAGE); System.exit(1); }
        if (!DatabaseManager.testConnection()) { JOptionPane.showMessageDialog(null, "Failed to connect to database!", "Connection Error", JOptionPane.ERROR_MESSAGE); System.exit(1); }
        SwingUtilities.invokeLater(() -> { try { ShopBillingSystem app = new ShopBillingSystem(); app.setVisible(true); app.setStatus("Connected successfully!", SUCCESS_COLOR); } catch (Exception e) { JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE); e.printStackTrace(); } });
    }
}