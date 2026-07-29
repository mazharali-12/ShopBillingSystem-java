<div align="center">

# 🏪 Shop Retail Billing System

A robust, production-oriented Desktop Retail Billing System built with Java Swing and MySQL. This project demonstrates advanced database management concepts, strict ACID compliance, and clean Object-Oriented Programming (OOP) architecture.

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Badge"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL Badge"/>
<img src="https://img.shields.io/badge/GUI-Swing-5382A1?style=for-the-badge&logo=apache%20netbeans%20ide&logoColor=white" alt="Swing Badge"/>

<br/>
<br/>

</div>

## 🌟 Unique & Advanced Features

Unlike basic CRUD applications, this system implements enterprise-level patterns to ensure data integrity and security:

*   🛡️ <b>Soft-Delete Implementation:</b> Items and Customers are <i>never</i> physically deleted. An <code>is_active</code> toggle is used to hide expired items while perfectly preserving historical financial data and bill references.
*   ⚙️ <b>Database-Level Automation (Triggers):</b> Stock deduction and restocking are handled entirely by MySQL <code>AFTER INSERT</code> triggers. This eliminates the "double-deduction" bug common in Java apps.
*   🔒 <b>ACID-Compliant Transactions:</b> Bill generation and Dues Settlement use explicit JDBC Transactions. If inserting a bill line fails, the entire bill rolls back, preventing corrupted ledgers.
*   💰 <b>Smart Credit Control:</b> The billing GUI actively prevents untraceable revenue loss. If a bill has an amount due, the system <i>forces</i> the user to link an existing Customer or create a new one—blocking "Walk-in" sales for credit.
*   📊 <b>Advanced SQL Architecture:</b> Utilizes SQL <code>VIEWS</code> for dynamic reporting, strategic <code>INDEXES</code> for performance, and strict <code>CHECK</code> constraints to enforce business logic at the DB level.

---

## 🛠️ Tech Stack

<table>
  <tr>
    <th>Component</th>
    <th>Technology</th>
  </tr>
  <tr>
    <td><b>Language</b></td>
    <td>Core Java (Java SE 17+)</td>
  </tr>
  <tr>
    <td><b>GUI Framework</b></td>
    <td>Java Swing (Programmatic, no drag-and-drop)</td>
  </tr>
  <tr>
    <td><b>Database</b></td>
    <td>MySQL 8.0</td>
  </tr>
  <tr>
    <td><b>Connectivity</b></td>
    <td>JDBC (Java Database Connectivity)</td>
  </tr>
</table>

---

## 🗄️ Database Architecture

The database (<code>shop_billing_dbjava</code>) is fully normalized to <b>Third Normal Form (3NF)</b> and consists of 8 interconnected tables: 
<code>Supplier</code> → <code>Item</code> ← <code>Category</code>, <code>Customer</code> → <code>Bill</code> → <code>Bill_Line</code> ← <code>Item</code>, <code>Bill</code> → <code>Payment</code>.

<b>Advanced DB Features Included:</b>
<ul>
  <li><b>5 Custom Views:</b> <code>view_bill_summary</code>, <code>view_inventory_detail</code>, <code>view_customer_dues</code>, <code>view_top_selling_items</code>, <code>view_payment_history</code>.</li>
  <li><b>2 Triggers:</b> For automated stock management without Java interference.</li>
  <li><b>5 Indexes:</b> Optimizing search on <code>item.name</code>, <code>bill.bill_date</code>, etc.</li>
</ul>

---

## 💻 Application Modules (GUI)

The interface is divided into 5 logical tabs:
1.  <b>📦 Inventory:</b> View active items, add products, restock, and toggle activation status.
2.  <b>👥 Customers:</b> Manage customer profiles, view outstanding dues, and process secure payments.
3.  <b>🤝 Suppliers:</b> Manage supplier contacts and addresses.
4.  <b>🧾 Billing:</b> Live calculation engine. Add items, apply discounts, handle split payments (Cash + Credit).
5.  <b>📊 Bills & Payments:</b> View complete invoice history and a dedicated ledger for incoming payments.

---

## 📸 System Screenshots

<div align="center">
  
  <!-- Replace the src links below with your actual screenshot paths after uploading them to GitHub -->
  <!-- Example: src="screenshots/inventory.png" -->
  
  <img src="https://via.placeholder.com/800x450.png?text=Screenshot+1:+Inventory+Management" width="800" alt="Inventory View"/>
  <br/>
  <em>Main Dashboard & Inventory Management</em>
  
  <br/><br/>
  
  <img src="https://via.placeholder.com/800x450.png?text=Screenshot+2:+Live+Billing+Engine" width="800" alt="Billing Tab"/>
  <br/>
  <em>Live Billing Engine & Smart Credit Control</em>

  <br/><br/>

  <img src="https://via.placeholder.com/800x450.png?text=Screenshot+3:+ACID+Dues+Settlement" width="800" alt="Dues Tab"/>
  <br/>
  <em>Transaction-Safe Dues Settlement</em>

</div>

---

## ⚙️ Setup & Installation

### Prerequisites
<ul>
  <li><b>Java JDK</b> (Version 17 or higher recommended)</li>
  <li><b>MySQL Server</b> (Version 8.0+)</li>
  <li><b>MySQL JDBC Driver</b> (<code>mysql-connector-j-x.x.x.jar</code>)</li>
</ul>

### 1. Database Setup
1. Open MySQL Workbench or MySQL CLI.
2. Execute the provided <code>shop_database_Corrected.sql</code> file to create the database, tables, triggers, views, and seed data.

### 2. Configure Database Credentials
Open <code>DatabaseManager.java</code> and update the URL, user, and password:
<pre><code>private static final String DB_URL = "jdbc:mysql://localhost:3306/shop_billing_dbjava";
private static final String DB_USER = "root";
private static final String DB_PASS = "@root"; // Change to your password
</code></pre>

### 3. Compile & Run via CMD
1. Place all <code>.java</code> files and the <code>.jar</code> file in the same directory.
2. Open Command Prompt and navigate to that folder.
3. Compile all files together:
<pre><code>javac -cp .;mysql-connector-j-9.6.0.jar *.java
</code></pre>
4. Run the main class:
<pre><code>java -cp .;mysql-connector-j-9.6.0.jar ShopBillingSystem
</code></pre>

---

## 📁 Project Structure

<pre><code>├── ShopBillingSystem.java      # Main GUI class (JFrame, JTabs)
├── DatabaseManager.java        # JDBC connections, Queries, Triggers calls
├── SupplierManager.java        # Business logic for Suppliers
├── InventoryManager.java       # Business logic for Items & Soft Delete
├── CustomerManager.java        # Business logic for Customers & ACID Dues
├── BillingManager.java         # Transaction-based Bill Generation
├── Model Classes/
│   ├── Item.java
│   ├── Customer.java
│   ├── Supplier.java
│   ├── Bill.java
│   ├── BillLine.java
│   └── BillResult.java
├── mysql-connector-j-9.6.0.jar # JDBC Driver
├── shop_database_Corrected.sql # Full Database Schema
└── README.md                   # You are here!
</code></pre>

---

<div align="center">

## 📜 License
This project is created for educational purposes (DBMS & OOP Course). Feel free to fork and modify for academic use.

</div>
