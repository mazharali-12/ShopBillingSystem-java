-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: shop_billing_dbjava
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill` (
  `bill_id` int NOT NULL AUTO_INCREMENT,
  `bill_date` date NOT NULL,
  `bill_time` time NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `discount_pct` decimal(5,2) NOT NULL DEFAULT '0.00',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `net_total` decimal(10,2) NOT NULL,
  `amount_paid` decimal(10,2) NOT NULL DEFAULT '0.00',
  `amount_due` decimal(10,2) NOT NULL DEFAULT '0.00',
  `customer_id` int DEFAULT NULL,
  PRIMARY KEY (`bill_id`),
  KEY `idx_bill_date` (`bill_date`),
  KEY `idx_bill_customer` (`customer_id`),
  CONSTRAINT `fk_bill_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `bill_chk_1` CHECK ((`subtotal` >= 0)),
  CONSTRAINT `bill_chk_2` CHECK ((`discount_pct` between 0 and 100)),
  CONSTRAINT `bill_chk_3` CHECK ((`net_total` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill`
--

LOCK TABLES `bill` WRITE;
/*!40000 ALTER TABLE `bill` DISABLE KEYS */;
INSERT INTO `bill` VALUES (1,'2025-06-01','09:15:00',1230.00,5.00,61.50,1168.50,1168.50,0.00,1),(2,'2025-06-01','11:30:00',330.00,0.00,0.00,330.00,330.00,0.00,2),(3,'2025-06-02','10:00:00',2100.00,10.00,210.00,1890.00,1890.00,0.00,3),(4,'2025-06-02','14:45:00',470.00,0.00,0.00,470.00,470.00,0.00,4),(5,'2025-06-03','09:00:00',1030.00,0.00,0.00,1030.00,1030.00,0.00,5),(6,'2025-06-03','13:20:00',180.00,0.00,0.00,180.00,180.00,0.00,6),(7,'2025-06-04','10:10:00',600.00,5.00,30.00,570.00,570.00,0.00,7),(8,'2025-06-04','15:00:00',280.00,0.00,0.00,280.00,280.00,0.00,8),(9,'2025-06-05','09:30:00',1900.00,5.00,95.00,1805.00,1805.00,0.00,9),(10,'2025-06-05','12:00:00',330.00,0.00,0.00,330.00,330.00,0.00,NULL),(11,'2026-04-18','16:56:07',970.00,20.00,194.00,776.00,676.00,100.00,3),(12,'2026-04-19','01:03:41',800.00,0.00,0.00,800.00,800.00,0.00,NULL),(13,'2026-04-19','01:07:18',450.00,0.00,0.00,450.00,350.00,100.00,12),(14,'2026-04-19','01:23:32',340.00,10.00,34.00,306.00,306.00,0.00,10),(15,'2026-04-19','01:47:36',280.00,0.00,0.00,280.00,280.00,0.00,NULL),(16,'2026-04-19','02:04:42',1760.00,35.00,616.00,1144.00,744.00,400.00,11),(17,'2026-04-22','00:11:37',460.00,10.00,46.00,414.00,414.00,0.00,7),(18,'2026-04-28','23:53:34',450.00,10.00,45.00,405.00,205.00,200.00,7),(19,'2026-04-29','21:50:45',360.00,12.00,43.20,316.80,116.00,200.80,NULL),(20,'2026-04-29','22:04:40',1050.00,20.00,210.00,840.00,500.00,340.00,NULL),(21,'2026-04-29','22:18:15',840.00,10.00,84.00,756.00,456.00,300.00,14),(22,'2026-04-29','22:25:51',560.00,0.00,0.00,560.00,400.00,160.00,12),(23,'2026-05-08','23:03:13',1100.00,20.00,220.00,880.00,600.00,280.00,2);
/*!40000 ALTER TABLE `bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_line`
--

DROP TABLE IF EXISTS `bill_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_line` (
  `line_id` int NOT NULL AUTO_INCREMENT,
  `bill_id` int NOT NULL,
  `item_id` int NOT NULL,
  `item_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price_at_sale` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `line_total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`line_id`),
  KEY `fk_line_bill` (`bill_id`),
  KEY `fk_line_item` (`item_id`),
  CONSTRAINT `fk_line_bill` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`bill_id`),
  CONSTRAINT `fk_line_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`),
  CONSTRAINT `bill_line_chk_1` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_line`
--

LOCK TABLES `bill_line` WRITE;
/*!40000 ALTER TABLE `bill_line` DISABLE KEYS */;
INSERT INTO `bill_line` VALUES (1,1,1,'Pepsi 500ml',60.00,5,300.00),(2,1,2,'Lays Chips Classic',30.00,3,90.00),(3,1,6,'Surf Excel 500g',120.00,7,840.00),(4,2,3,'Nestle Milk 1L',180.00,1,180.00),(5,2,7,'Lifebuoy Soap 120g',50.00,3,150.00),(6,3,9,'Basmati Rice 1kg',280.00,5,1400.00),(7,3,4,'Dalda Cooking Oil 1L',350.00,2,700.00),(8,4,5,'Ball Point Pen Blue',15.00,20,300.00),(9,4,6,'Surf Excel 500g',120.00,1,120.00),(10,4,7,'Lifebuoy Soap 120g',50.00,1,50.00),(11,5,8,'Walls Cornetto',90.00,5,450.00),(12,5,1,'Pepsi 500ml',60.00,5,300.00),(13,5,9,'Basmati Rice 1kg',280.00,1,280.00),(14,6,3,'Nestle Milk 1L',180.00,1,180.00),(15,7,10,'7UP 500ml',60.00,5,300.00),(16,7,2,'Lays Chips Classic',30.00,10,300.00),(17,8,9,'Basmati Rice 1kg',280.00,1,280.00),(18,9,4,'Dalda Cooking Oil 1L',350.00,3,1050.00),(19,9,3,'Nestle Milk 1L',180.00,2,360.00),(20,9,7,'Lifebuoy Soap 120g',50.00,5,250.00),(21,9,5,'Ball Point Pen Blue',15.00,16,240.00),(22,10,1,'Pepsi 500ml',60.00,3,180.00),(23,10,2,'Lays Chips Classic',30.00,5,150.00),(24,11,3,'Nestle Milk 1L',180.00,4,720.00),(25,11,7,'Lifebuoy Soap 120g',50.00,5,250.00),(26,12,4,'Dalda Cooking Oil 1L',350.00,2,700.00),(27,12,7,'Lifebuoy Soap 120g',50.00,2,100.00),(28,13,8,'Walls Cornetto',90.00,5,450.00),(29,14,6,'Surf Excel 500g',120.00,2,240.00),(30,14,7,'Lifebuoy Soap 120g',50.00,2,100.00),(31,15,12,'Lays Chips Yougurt',70.00,4,280.00),(32,16,9,'Basmati Rice 1kg',280.00,5,1400.00),(33,16,8,'Walls Cornetto',90.00,4,360.00),(34,17,2,'Lays Chips Classic',30.00,2,60.00),(35,17,12,'Lays Chips Yougurt',70.00,4,280.00),(36,17,1,'Pepsi 500ml',60.00,2,120.00),(37,18,8,'Walls Cornetto',90.00,5,450.00),(38,19,3,'Nestle Milk 1L',180.00,2,360.00),(39,20,4,'Dalda Cooking Oil 1L',350.00,3,1050.00),(40,21,12,'Lays Chips Yougurt',70.00,12,840.00),(41,22,9,'Basmati Rice 1kg',280.00,2,560.00),(42,23,11,'Special biscuit',220.00,5,1100.00);
/*!40000 ALTER TABLE `bill_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Beverages','Cold drinks, juices, water, and energy drinks'),(2,'Snacks','Chips, biscuits, crackers, and packaged snacks'),(3,'Dairy','Milk, cheese, butter, and yogurt products'),(4,'Bakery','Bread, buns, cakes, and pastries'),(5,'Stationery','Pens, notebooks, registers, and office supplies'),(6,'Cleaning','Soaps, detergents, and household cleaners'),(7,'Personal Care','Shampoo, toothpaste, and hygiene products'),(8,'Frozen Foods','Ice cream, frozen meals, and frozen vegetables'),(9,'Dry Goods','Rice, flour, pulses, and cooking essentials'),(10,'Tobacco','Cigarettes and other tobacco products');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_dues` decimal(10,2) NOT NULL DEFAULT '0.00',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `phone` (`phone`),
  CONSTRAINT `customer_chk_1` CHECK ((`total_dues` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Ahmed Ali','0311-1234001',0.00,1),(2,'Sara Khan','0322-2345002',280.00,1),(3,'Muhammad Raza','0333-3456003',100.00,1),(4,'Fatima Bibi','0344-4567004',0.00,1),(5,'Usman Tariq','0355-5678005',0.00,1),(6,'Ayesha Siddiqui','0366-6789006',0.00,1),(7,'Bilal Hussain','0377-7890007',200.00,1),(8,'Zainab Mirza','0388-8901008',0.00,1),(9,'Kamran Sheikh','0399-9012009',0.00,1),(10,'Kamran','03094845566',0.00,1),(11,'Mazhar Ali','03084559966',400.00,1),(12,'Shafique','04566756787',260.00,1),(13,'Ahsan Ali','03144578654',0.00,1),(14,'Waris Ali','03094859966',300.00,1);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `dept_id` int NOT NULL,
  `dept_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (10,'Engineering','Building A'),(20,'Marketing','Building B'),(30,'Sales','Building C'),(40,'HR','Building D'),(50,'Research','Building E');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `emp_id` int NOT NULL,
  `emp_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dept_id` int DEFAULT NULL,
  `salary` decimal(10,2) DEFAULT NULL,
  `manager_id` int DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  PRIMARY KEY (`emp_id`),
  KEY `dept_id` (`dept_id`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `departments` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (101,'Alice',10,95000.00,NULL,'2018-03-15'),(102,'Bob',10,82000.00,101,'2019-06-01'),(103,'Charlie',20,78000.00,101,'2020-01-20'),(104,'Diana',20,72000.00,103,'2020-09-10'),(105,'Eve',30,68000.00,101,'2021-02-14'),(106,'Frank',30,91000.00,101,'2017-07-22'),(107,'Grace',10,88000.00,101,'2019-11-30'),(108,'Hank',40,60000.00,106,'2022-04-01'),(109,'Ivy',30,74000.00,106,'2021-08-15'),(110,'Jack',20,85000.00,101,'2018-12-05');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `category_id` int NOT NULL,
  `supplier_id` int NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`item_id`),
  KEY `fk_item_category` (`category_id`),
  KEY `fk_item_supplier` (`supplier_id`),
  KEY `idx_item_name` (`name`),
  CONSTRAINT `fk_item_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
  CONSTRAINT `fk_item_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  CONSTRAINT `item_chk_1` CHECK ((`price` > 0)),
  CONSTRAINT `item_chk_2` CHECK ((`stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'Pepsi 500ml',60.00,199,1,1,1),(2,'Lays Chips Classic',30.00,180,2,2,1),(3,'Nestle Milk 1L',180.00,70,3,3,1),(4,'Dalda Cooking Oil 1L',350.00,40,9,4,1),(5,'Ball Point Pen Blue',15.00,264,5,5,1),(6,'Surf Excel 500g',120.00,80,6,6,1),(7,'Lifebuoy Soap 120g',50.00,132,7,7,1),(8,'Walls Cornetto',90.00,41,8,8,1),(9,'Basmati Rice 1kg',280.00,56,9,9,1),(10,'7UP 500ml',60.00,100,1,1,1),(11,'Special biscuit',220.00,195,2,11,1),(12,'Lays Chips Yougurt',70.00,9,5,11,1),(13,'Nestle Apple Juice',90.00,230,1,3,1);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL,
  `emp_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `emp_id` (`emp_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1001,101,1,2,'2024-01-10'),(1002,102,2,1,'2024-01-15'),(1003,103,3,4,'2024-02-01'),(1004,101,4,1,'2024-02-20'),(1005,105,6,3,'2024-03-05'),(1006,106,1,1,'2024-03-12'),(1007,107,7,2,'2024-03-18'),(1008,110,5,1,'2024-04-02'),(1009,102,3,2,'2024-04-15'),(1010,109,2,1,'2024-04-22');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `bill_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PARTIAL',
  `note` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `fk_pay_bill` (`bill_id`),
  KEY `fk_pay_customer` (`customer_id`),
  KEY `idx_pay_date` (`payment_date`),
  KEY `idx_pay_type` (`payment_type`),
  CONSTRAINT `fk_pay_bill` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`bill_id`),
  CONSTRAINT `fk_pay_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `payment_chk_1` CHECK ((`amount` > 0)),
  CONSTRAINT `payment_chk_2` CHECK ((`payment_type` in (_utf8mb4'PARTIAL',_utf8mb4'HALF',_utf8mb4'FULL_BILL',_utf8mb4'DUES_CLEARED')))
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,1,668.50,'2025-06-01','PARTIAL','Partial at billing'),(2,1,1,500.00,'2025-06-06','DUES_CLEARED','Remaining dues settled'),(3,2,2,330.00,'2025-06-01','FULL_BILL','Full payment at billing'),(4,3,3,630.00,'2025-06-02','PARTIAL','First installment at billing'),(5,3,3,630.00,'2025-06-08','PARTIAL','Second installment'),(6,3,3,630.00,'2025-06-14','DUES_CLEARED','Final installment, dues cleared'),(7,4,4,470.00,'2025-06-02','FULL_BILL','Full payment at billing'),(8,5,5,515.00,'2025-06-03','HALF','Half paid at billing'),(9,5,5,515.00,'2025-06-10','DUES_CLEARED','Second half, dues cleared'),(10,6,6,180.00,'2025-06-03','FULL_BILL','Full payment at billing'),(11,11,3,500.00,'2026-04-18','PARTIAL','Partial payment at checkout'),(12,11,3,176.00,'2026-04-18','PARTIAL','Settled dues'),(13,13,12,300.00,'2026-04-19','PARTIAL','Partial payment at checkout'),(14,13,12,50.00,'2026-04-19','PARTIAL','Settled dues'),(15,14,10,106.00,'2026-04-19','PARTIAL','Partial payment at checkout'),(16,14,10,200.00,'2026-04-19','DUES_CLEARED','Settled dues'),(17,16,11,544.00,'2026-04-19','PARTIAL','Partial payment at checkout'),(18,16,11,200.00,'2026-04-19','PARTIAL','Settled dues'),(19,9,9,150.00,'2026-04-21','PARTIAL','Settled dues'),(20,7,7,120.00,'2026-04-22','PARTIAL','Settled dues'),(21,17,7,214.00,'2026-04-22','PARTIAL','Partial payment'),(22,7,7,180.00,'2026-04-22','DUES_CLEARED','Settled dues'),(23,17,7,20.00,'2026-04-22','PARTIAL','Settled dues'),(24,9,9,200.00,'2026-04-22','PARTIAL','Settled dues'),(25,9,9,200.00,'2026-04-23','PARTIAL','Settled dues'),(26,9,9,200.00,'2026-04-23','PARTIAL','Settled dues'),(27,9,9,200.00,'2026-04-28','DUES_CLEARED','Settled dues'),(28,17,7,100.00,'2026-04-28','PARTIAL','Settled dues'),(29,18,7,205.00,'2026-04-28','PARTIAL','Partial payment'),(30,17,7,80.00,'2026-04-28','DUES_CLEARED','Settled dues'),(31,21,14,456.00,'2026-04-29','PARTIAL','Partial payment'),(32,22,12,400.00,'2026-04-29','PARTIAL','Partial payment'),(33,23,2,600.00,'2026-05-08','PARTIAL','Partial payment');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL,
  `product_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Laptop','Electronics',1200.00),(2,'Phone','Electronics',800.00),(3,'Desk Chair','Furniture',350.00),(4,'Monitor','Electronics',450.00),(5,'Bookshelf','Furniture',200.00),(6,'Keyboard','Electronics',75.00),(7,'Standing Desk','Furniture',600.00);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_log`
--

DROP TABLE IF EXISTS `stock_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `supplier_id` int NOT NULL,
  `qty_added` int NOT NULL,
  `log_date` date NOT NULL,
  `remarks` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `fk_log_item` (`item_id`),
  KEY `fk_log_supplier` (`supplier_id`),
  KEY `idx_log_date` (`log_date`),
  CONSTRAINT `fk_log_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`),
  CONSTRAINT `fk_log_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  CONSTRAINT `stock_log_chk_1` CHECK ((`qty_added` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_log`
--

LOCK TABLES `stock_log` WRITE;
/*!40000 ALTER TABLE `stock_log` DISABLE KEYS */;
INSERT INTO `stock_log` VALUES (1,1,1,100,'2025-05-25','Monthly restock — Pepsi 500ml'),(2,2,2,50,'2025-05-26','Lays restock for promotion period'),(3,3,3,40,'2025-05-27','Nestle Milk standard monthly order'),(4,4,4,30,'2025-05-28','Dalda Oil pre-Eid restock'),(5,5,5,200,'2025-05-29','Pens bulk order for exam season'),(6,6,6,60,'2025-05-30','Surf Excel monthly supply'),(7,7,7,80,'2025-05-30','Lifebuoy Soap hygiene campaign stock'),(8,8,8,40,'2025-05-31','Walls Cornetto summer restock'),(9,9,9,50,'2025-06-01','Basmati Rice restock for June'),(10,1,1,50,'2025-06-03','Emergency Pepsi restock'),(11,1,1,47,'2026-04-19','Manual Restock'),(12,12,11,1,'2026-04-19','Manual Restock'),(13,12,11,4,'2026-04-21','Manual Restock'),(14,12,11,6,'2026-04-21','Manual Restock'),(15,11,11,20,'2026-04-21','Restock'),(16,11,11,30,'2026-04-22','GUI Restock'),(17,10,1,5,'2026-04-28','GUI Restock'),(18,12,11,5,'2026-04-29','GUI Restock');
/*!40000 ALTER TABLE `stock_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `supplier_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,'Pepsi Distributors Sukkur','0300-1111001','Main Market, Sukkur','pepsi.skr@gmail.com'),(2,'National Foods Ltd','0301-2222002','Industrial Area, Karachi','national@foods.pk'),(3,'Nestle Pakistan','0302-3333003','Blue Area, Islamabad','nestle@pk.nestle.com'),(4,'Dalda Vanaspati','0303-4444004','SITE Area, Karachi','dalda@dalda.com'),(5,'Al-Khair Stationery','0304-5555005','Saddar Bazaar, Sukkur','alkhair.stat@gmail.com'),(6,'Clean & Fresh Co.','0305-6666006','Hyderabad Road, Sukkur','cleanfresh@gmail.com'),(7,'Unilever Pakistan','0306-7777007','Port Qasim, Karachi','supplier@unilever.pk'),(8,'Walls Ice Cream','0307-8888008','Lahore Road, Multan','walls@walls.com.pk'),(9,'Al-Noor Rice Mills','0308-9999009','Rohri, Sukkur','alnoor.rice@gmail.com'),(10,'Lucky Tobacco Co.','0309-1010010','Industrial Zone, Karachi','lucky.tobacco@gmail.com'),(11,'Al-Madina Utility Store','03495644754','Old Sukkur','almadinautl@gmail.com');
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `view_bill_summary`
--

DROP TABLE IF EXISTS `view_bill_summary`;
/*!50001 DROP VIEW IF EXISTS `view_bill_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_bill_summary` AS SELECT 
 1 AS `bill_id`,
 1 AS `bill_date`,
 1 AS `bill_time`,
 1 AS `customer`,
 1 AS `phone`,
 1 AS `subtotal`,
 1 AS `discount_pct`,
 1 AS `discount_amount`,
 1 AS `net_total`,
 1 AS `amount_paid`,
 1 AS `amount_due`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_customer_dues`
--

DROP TABLE IF EXISTS `view_customer_dues`;
/*!50001 DROP VIEW IF EXISTS `view_customer_dues`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_customer_dues` AS SELECT 
 1 AS `customer_id`,
 1 AS `name`,
 1 AS `phone`,
 1 AS `total_dues`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_inventory_detail`
--

DROP TABLE IF EXISTS `view_inventory_detail`;
/*!50001 DROP VIEW IF EXISTS `view_inventory_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_inventory_detail` AS SELECT 
 1 AS `item_id`,
 1 AS `item_name`,
 1 AS `price`,
 1 AS `stock`,
 1 AS `category`,
 1 AS `supplier`,
 1 AS `supplier_phone`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_payment_history`
--

DROP TABLE IF EXISTS `view_payment_history`;
/*!50001 DROP VIEW IF EXISTS `view_payment_history`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_payment_history` AS SELECT 
 1 AS `payment_id`,
 1 AS `payment_date`,
 1 AS `customer`,
 1 AS `bill_id`,
 1 AS `bill_total`,
 1 AS `amount`,
 1 AS `payment_type`,
 1 AS `note`,
 1 AS `bill_remaining_due`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_top_selling_items`
--

DROP TABLE IF EXISTS `view_top_selling_items`;
/*!50001 DROP VIEW IF EXISTS `view_top_selling_items`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_top_selling_items` AS SELECT 
 1 AS `item_id`,
 1 AS `item_name`,
 1 AS `total_units_sold`,
 1 AS `total_revenue`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `view_bill_summary`
--

/*!50001 DROP VIEW IF EXISTS `view_bill_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_bill_summary` AS select `b`.`bill_id` AS `bill_id`,`b`.`bill_date` AS `bill_date`,`b`.`bill_time` AS `bill_time`,coalesce(`c`.`name`,'Walk-in') AS `customer`,`c`.`phone` AS `phone`,`b`.`subtotal` AS `subtotal`,`b`.`discount_pct` AS `discount_pct`,`b`.`discount_amount` AS `discount_amount`,`b`.`net_total` AS `net_total`,`b`.`amount_paid` AS `amount_paid`,`b`.`amount_due` AS `amount_due` from (`bill` `b` left join `customer` `c` on((`b`.`customer_id` = `c`.`customer_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_customer_dues`
--

/*!50001 DROP VIEW IF EXISTS `view_customer_dues`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_customer_dues` AS select `customer`.`customer_id` AS `customer_id`,`customer`.`name` AS `name`,`customer`.`phone` AS `phone`,`customer`.`total_dues` AS `total_dues` from `customer` where (`customer`.`total_dues` > 0) order by `customer`.`total_dues` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_inventory_detail`
--

/*!50001 DROP VIEW IF EXISTS `view_inventory_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_inventory_detail` AS select `i`.`item_id` AS `item_id`,`i`.`name` AS `item_name`,`i`.`price` AS `price`,`i`.`stock` AS `stock`,`c`.`name` AS `category`,`s`.`name` AS `supplier`,`s`.`phone` AS `supplier_phone` from ((`item` `i` join `category` `c` on((`i`.`category_id` = `c`.`category_id`))) join `supplier` `s` on((`i`.`supplier_id` = `s`.`supplier_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_payment_history`
--

/*!50001 DROP VIEW IF EXISTS `view_payment_history`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_payment_history` AS select `p`.`payment_id` AS `payment_id`,`p`.`payment_date` AS `payment_date`,`c`.`name` AS `customer`,`b`.`bill_id` AS `bill_id`,`b`.`net_total` AS `bill_total`,`p`.`amount` AS `amount`,`p`.`payment_type` AS `payment_type`,`p`.`note` AS `note`,`b`.`amount_due` AS `bill_remaining_due` from ((`payment` `p` join `bill` `b` on((`p`.`bill_id` = `b`.`bill_id`))) join `customer` `c` on((`p`.`customer_id` = `c`.`customer_id`))) order by `p`.`payment_date`,`p`.`bill_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_top_selling_items`
--

/*!50001 DROP VIEW IF EXISTS `view_top_selling_items`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_top_selling_items` AS select `bl`.`item_id` AS `item_id`,`bl`.`item_name` AS `item_name`,sum(`bl`.`quantity`) AS `total_units_sold`,sum(`bl`.`line_total`) AS `total_revenue` from `bill_line` `bl` group by `bl`.`item_id`,`bl`.`item_name` order by `total_units_sold` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-28  9:57:42
