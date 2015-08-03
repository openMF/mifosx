-- MySQL dump 10.13  Distrib 5.6.23, for osx10.10 (x86_64)
--
-- Host: localhost    Database: mifostenant-19
-- ------------------------------------------------------
-- Server version	5.6.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acc_accounting_rule`
--

DROP TABLE IF EXISTS `acc_accounting_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_accounting_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `debit_account_id` bigint(20) DEFAULT NULL,
  `allow_multiple_debits` tinyint(1) NOT NULL DEFAULT '0',
  `credit_account_id` bigint(20) DEFAULT NULL,
  `allow_multiple_credits` tinyint(1) NOT NULL DEFAULT '0',
  `description` varchar(500) DEFAULT NULL,
  `system_defined` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `accounting_rule_name_unique` (`name`),
  KEY `FK_acc_accounting_rule_acc_gl_account_debit` (`debit_account_id`),
  KEY `FK_acc_accounting_rule_acc_gl_account_credit` (`credit_account_id`),
  KEY `FK_acc_accounting_rule_m_office` (`office_id`),
  CONSTRAINT `FK_acc_accounting_rule_acc_gl_account_credit` FOREIGN KEY (`credit_account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK_acc_accounting_rule_acc_gl_account_debit` FOREIGN KEY (`debit_account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK_acc_accounting_rule_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_accounting_rule`
--

LOCK TABLES `acc_accounting_rule` WRITE;
/*!40000 ALTER TABLE `acc_accounting_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `acc_accounting_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_gl_account`
--

DROP TABLE IF EXISTS `acc_gl_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_gl_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `hierarchy` varchar(50) DEFAULT NULL,
  `gl_code` varchar(45) NOT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  `manual_journal_entries_allowed` tinyint(1) NOT NULL DEFAULT '1',
  `account_usage` tinyint(1) NOT NULL DEFAULT '2',
  `classification_enum` smallint(5) NOT NULL,
  `tag_id` int(11) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `acc_gl_code` (`gl_code`),
  KEY `FK_ACC_0000000001` (`parent_id`),
  KEY `FKGLACC000000002` (`tag_id`),
  CONSTRAINT `FKGLACC000000002` FOREIGN KEY (`tag_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_ACC_0000000001` FOREIGN KEY (`parent_id`) REFERENCES `acc_gl_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_gl_account`
--

LOCK TABLES `acc_gl_account` WRITE;
/*!40000 ALTER TABLE `acc_gl_account` DISABLE KEYS */;
INSERT INTO `acc_gl_account` VALUES (1,'Deposit Accounts Portfolio',NULL,'.','10100',0,1,2,2,NULL,NULL),(2,'Overpayment Liability',NULL,'.','10200',0,1,1,2,NULL,NULL),(3,'Interest Payable',NULL,'.','10300',0,1,2,2,NULL,NULL),(4,'Pass-Through Collections',NULL,'.','10400',0,1,2,2,NULL,NULL),(5,'External Credits and Borrowings',NULL,'.','10500',0,1,2,2,NULL,NULL),(6,'Accrued Expenses and Provisions',NULL,'.','10600',0,1,2,2,NULL,NULL),(7,'Other Liabilities',NULL,'.','10700',0,1,2,2,NULL,NULL),(8,'Fixed Assets',NULL,'.','20100',0,1,2,1,NULL,NULL),(9,'Investments',NULL,'.','20200',0,1,2,1,NULL,NULL),(10,'Cash',NULL,'.','20300',0,1,2,1,NULL,NULL),(11,'Bank Accounts',NULL,'.','20400',0,1,2,1,NULL,NULL),(12,'Lending Portfolio',NULL,'.','20500',0,1,2,1,NULL,NULL),(13,'Interest and Fees Receivable',NULL,'.','20600',0,1,2,1,NULL,NULL),(14,'Advance Payments',NULL,'.','20700',0,1,2,1,NULL,NULL),(15,'Reserves for Possible Losses',NULL,'.','20800',0,1,2,1,NULL,NULL),(16,'Accrued Income',NULL,'.','20900',0,1,2,1,NULL,NULL),(17,'Suspense Account',NULL,'.','9999',0,1,1,1,NULL,NULL),(18,'Portfolio Earnings',NULL,'.','30100',0,1,2,4,NULL,NULL),(19,'Non Portfolio Income',NULL,'.','30200',0,1,2,4,NULL,NULL),(20,'Portfolio Expenses',NULL,'.','40100',0,1,2,5,NULL,NULL),(21,'General And Administrative Expenses',NULL,'.','40200',0,1,2,5,NULL,NULL),(22,'Sundry Expenses',NULL,'.','40800',0,1,2,5,NULL,NULL),(23,'Repairs and Maintenance',NULL,'.','40900',0,1,2,5,NULL,NULL),(24,'Depreciation and Amortization',NULL,'.','41000',0,1,2,5,NULL,NULL),(25,'Equity and Share Capital',NULL,'.','50100',0,1,2,3,NULL,NULL),(26,'Opening Balances Contra Account',NULL,'.','9099',0,1,2,3,NULL,NULL),(27,'Voluntary Savings',1,'.1.','10101',0,1,2,2,NULL,NULL),(28,'Mandatory Savings',1,'.1.','10102',0,1,2,2,NULL,NULL),(29,'Term Deposits',1,'.1.','10103',0,1,2,2,NULL,NULL),(30,'Recurring Deposits',1,'.1.','10104',0,1,1,2,NULL,NULL),(31,'Furniture and Fixtures',8,'.8.','20101',0,1,1,1,NULL,NULL),(32,'Cash In Hand',10,'.10.','20301',0,1,1,1,NULL,NULL),(33,'Petty Cash',10,'.10.','20302',0,1,1,1,NULL,NULL),(34,'Loans to Customers',12,'.12.','20501',0,1,1,1,NULL,NULL),(35,'Current Account Overdrafts',12,'.12.','20502',0,1,1,1,NULL,NULL),(36,'Fees and Charges',18,'.18.','30101',0,1,1,4,NULL,NULL),(37,'Penalties',18,'.18.','30102',0,1,1,4,NULL,NULL),(38,'Interest Received from Borrowers',18,'.18.','30103',0,1,1,4,NULL,NULL),(39,'Insurance Charges',18,'.18.','30104',0,1,1,4,NULL,NULL),(40,'Other Operating Income',18,'.18.','30105',0,1,1,4,NULL,NULL),(41,'Losses Written Off',20,'.20.','40101',0,1,1,5,NULL,NULL),(42,'Interest Paid To Depositors',20,'.20.','40102',0,1,1,5,NULL,NULL),(43,'Loan Collection Expenses',20,'.20.','40103',0,1,1,5,NULL,NULL),(44,'Salaries and Wages',21,'.21.','40300',0,1,1,5,NULL,NULL),(45,'Professional Expenses',21,'.21.','40400',0,1,1,5,NULL,NULL),(46,'Travel and Conveyance Expenses',21,'.21.','40500',0,1,1,5,NULL,NULL),(47,'Training Expenses',21,'.21.','40600',0,1,1,5,NULL,NULL),(48,'Office Expenses',21,'.21.','40700',0,1,2,5,NULL,NULL),(49,'Printing and Stationery',48,'.21.48.','40701',0,1,1,5,NULL,NULL),(50,'Telephone Charges',48,'.21.48.','40702',0,1,1,5,NULL,NULL),(51,'Electricity Charges',48,'.21.48.','40703',0,1,1,5,NULL,NULL),(52,'Rent Paid',48,'.21.48.','40704',0,1,1,5,NULL,NULL),(53,'Internet Charges',48,'.21.48.','40705',0,1,1,5,NULL,NULL),(54,'Loan Recovery (Temp)',NULL,'.','220002-Temp',0,1,1,4,NULL,'Temporary account to track income from Loan recovery'),(55,'Liability Transfer (Temp)',NULL,'.','220004-Temp',0,1,1,2,NULL,'Temporary Liability account to track Account Transfers');
/*!40000 ALTER TABLE `acc_gl_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_gl_closure`
--

DROP TABLE IF EXISTS `acc_gl_closure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_gl_closure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `closing_date` date NOT NULL,
  `is_deleted` int(20) NOT NULL DEFAULT '0',
  `createdby_id` bigint(20) DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `comments` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `office_id_closing_date` (`office_id`,`closing_date`),
  KEY `FK_acc_gl_closure_m_office` (`office_id`),
  KEY `FK_acc_gl_closure_m_appuser` (`createdby_id`),
  KEY `FK_acc_gl_closure_m_appuser_2` (`lastmodifiedby_id`),
  CONSTRAINT `FK_acc_gl_closure_m_appuser` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_acc_gl_closure_m_appuser_2` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_acc_gl_closure_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_gl_closure`
--

LOCK TABLES `acc_gl_closure` WRITE;
/*!40000 ALTER TABLE `acc_gl_closure` DISABLE KEYS */;
/*!40000 ALTER TABLE `acc_gl_closure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_gl_financial_activity_account`
--

DROP TABLE IF EXISTS `acc_gl_financial_activity_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_gl_financial_activity_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gl_account_id` bigint(20) NOT NULL DEFAULT '0',
  `financial_activity_type` smallint(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `financial_activity_type` (`financial_activity_type`),
  KEY `FK_office_mapping_acc_gl_account` (`gl_account_id`),
  CONSTRAINT `FK_office_mapping_acc_gl_account` FOREIGN KEY (`gl_account_id`) REFERENCES `acc_gl_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_gl_financial_activity_account`
--

LOCK TABLES `acc_gl_financial_activity_account` WRITE;
/*!40000 ALTER TABLE `acc_gl_financial_activity_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `acc_gl_financial_activity_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_gl_journal_entry`
--

DROP TABLE IF EXISTS `acc_gl_journal_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_gl_journal_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `office_id` bigint(20) NOT NULL,
  `reversal_id` bigint(20) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `transaction_id` varchar(50) NOT NULL,
  `loan_transaction_id` bigint(20) DEFAULT NULL,
  `savings_transaction_id` bigint(20) DEFAULT NULL,
  `reversed` tinyint(1) NOT NULL DEFAULT '0',
  `ref_num` varchar(100) DEFAULT NULL,
  `manual_entry` tinyint(1) NOT NULL DEFAULT '0',
  `entry_date` date NOT NULL,
  `type_enum` smallint(5) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `entity_type_enum` smallint(5) DEFAULT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `createdby_id` bigint(20) NOT NULL,
  `lastmodifiedby_id` bigint(20) NOT NULL,
  `created_date` datetime NOT NULL,
  `lastmodified_date` datetime NOT NULL,
  `is_running_balance_calculated` tinyint(4) NOT NULL DEFAULT '0',
  `office_running_balance` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `organization_running_balance` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `payment_details_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_acc_gl_journal_entry_m_office` (`office_id`),
  KEY `FK_acc_gl_journal_entry_m_appuser` (`createdby_id`),
  KEY `FK_acc_gl_journal_entry_m_appuser_2` (`lastmodifiedby_id`),
  KEY `FK_acc_gl_journal_entry_acc_gl_journal_entry` (`reversal_id`),
  KEY `FK_acc_gl_journal_entry_acc_gl_account` (`account_id`),
  KEY `FK_acc_gl_journal_entry_m_loan_transaction` (`loan_transaction_id`),
  KEY `FK_acc_gl_journal_entry_m_savings_account_transaction` (`savings_transaction_id`),
  KEY `FK_acc_gl_journal_entry_m_payment_detail` (`payment_details_id`),
  CONSTRAINT `FK_acc_gl_journal_entry_acc_gl_account` FOREIGN KEY (`account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_acc_gl_journal_entry` FOREIGN KEY (`reversal_id`) REFERENCES `acc_gl_journal_entry` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_appuser` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_appuser_2` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_loan_transaction` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_payment_detail` FOREIGN KEY (`payment_details_id`) REFERENCES `m_payment_detail` (`id`),
  CONSTRAINT `FK_acc_gl_journal_entry_m_savings_account_transaction` FOREIGN KEY (`savings_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_gl_journal_entry`
--

LOCK TABLES `acc_gl_journal_entry` WRITE;
/*!40000 ALTER TABLE `acc_gl_journal_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `acc_gl_journal_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_product_mapping`
--

DROP TABLE IF EXISTS `acc_product_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_product_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gl_account_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `product_type` smallint(5) DEFAULT NULL,
  `payment_type` int(11) DEFAULT NULL,
  `charge_id` bigint(20) DEFAULT NULL,
  `financial_account_type` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_acc_product_mapping_m_charge` (`charge_id`),
  KEY `FK_acc_product_mapping_m_payment_type` (`payment_type`),
  CONSTRAINT `FK_acc_product_mapping_m_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  CONSTRAINT `FK_acc_product_mapping_m_payment_type` FOREIGN KEY (`payment_type`) REFERENCES `m_payment_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_product_mapping`
--

LOCK TABLES `acc_product_mapping` WRITE;
/*!40000 ALTER TABLE `acc_product_mapping` DISABLE KEYS */;
INSERT INTO `acc_product_mapping` VALUES (1,32,1,1,NULL,NULL,1),(2,34,1,1,NULL,NULL,2),(3,17,1,1,NULL,NULL,10),(4,38,1,1,NULL,NULL,3),(5,36,1,1,NULL,NULL,4),(6,37,1,1,NULL,NULL,5),(7,41,1,1,NULL,NULL,6),(8,2,1,1,NULL,NULL,11),(9,31,1,2,NULL,NULL,1),(10,32,1,2,NULL,NULL,11),(11,36,1,2,NULL,NULL,4),(12,37,1,2,NULL,NULL,5),(13,38,1,2,NULL,NULL,12),(14,41,1,2,NULL,NULL,3),(15,42,1,2,NULL,NULL,13),(16,30,1,2,NULL,NULL,2),(17,30,1,2,NULL,NULL,10),(18,54,1,1,NULL,NULL,12);
/*!40000 ALTER TABLE `acc_product_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acc_rule_tags`
--

DROP TABLE IF EXISTS `acc_rule_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_rule_tags` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acc_rule_id` bigint(20) NOT NULL,
  `tag_id` int(11) NOT NULL,
  `acc_type_enum` smallint(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_ACCOUNT_RULE_TAGS` (`acc_rule_id`,`tag_id`,`acc_type_enum`),
  KEY `FK_acc_accounting_rule_id` (`acc_rule_id`),
  KEY `FK_m_code_value_id` (`tag_id`),
  CONSTRAINT `FK_acc_accounting_rule_id` FOREIGN KEY (`acc_rule_id`) REFERENCES `acc_accounting_rule` (`id`),
  CONSTRAINT `FK_m_code_value_id` FOREIGN KEY (`tag_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_rule_tags`
--

LOCK TABLES `acc_rule_tags` WRITE;
/*!40000 ALTER TABLE `acc_rule_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `acc_rule_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_account_number_format`
--

DROP TABLE IF EXISTS `c_account_number_format`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `c_account_number_format` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_type_enum` smallint(1) NOT NULL,
  `prefix_type_enum` smallint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_type_enum` (`account_type_enum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_account_number_format`
--

LOCK TABLES `c_account_number_format` WRITE;
/*!40000 ALTER TABLE `c_account_number_format` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_account_number_format` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_cache`
--

DROP TABLE IF EXISTS `c_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `c_cache` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cache_type_enum` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_cache`
--

LOCK TABLES `c_cache` WRITE;
/*!40000 ALTER TABLE `c_cache` DISABLE KEYS */;
INSERT INTO `c_cache` VALUES (1,1);
/*!40000 ALTER TABLE `c_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_configuration`
--

DROP TABLE IF EXISTS `c_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `c_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `value` int(11) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `description` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_configuration`
--

LOCK TABLES `c_configuration` WRITE;
/*!40000 ALTER TABLE `c_configuration` DISABLE KEYS */;
INSERT INTO `c_configuration` VALUES (1,'maker-checker',NULL,0,NULL),(4,'amazon-S3',NULL,0,NULL),(5,'reschedule-future-repayments',NULL,1,NULL),(6,'reschedule-repayments-on-holidays',NULL,0,NULL),(7,'allow-transactions-on-holiday',NULL,0,NULL),(8,'allow-transactions-on-non_workingday',NULL,0,NULL),(9,'constraint_approach_for_datatables',NULL,0,NULL),(10,'penalty-wait-period',2,1,NULL),(11,'force-password-reset-days',0,0,NULL),(12,'grace-on-penalty-posting',0,1,NULL),(15,'savings-interest-posting-current-period-end',NULL,0,'Recommended to be changed only once during start of production. When set as false(default), interest will be posted on the first date of next period. If set as true, interest will be posted on last date of current period. There is no difference in the interest amount posted.'),(16,'financial-year-beginning-month',1,1,'Recommended to be changed only once during start of production. Allowed values 1 - 12 (January - December). Interest posting periods are evaluated based on this configuration.'),(17,'min-clients-in-group',5,0,'Minimum number of Clients that a Group should have'),(18,'max-clients-in-group',5,0,'Maximum number of Clients that a Group can have'),(19,'meetings-mandatory-for-jlg-loans',NULL,0,'Enforces all JLG loans to follow a meeting schedule belonging to parent group or Center'),(20,'office-specific-products-enabled',0,0,'Whether products and fees should be office specific or not? This property should NOT be changed once Mifos is Live.'),(21,'restrict-products-to-user-office',0,0,'This should be enabled only if, products & fees are office specific (i.e. office-specific-products-enabled is enabled). This property specifies if the products should be auto-restricted to office of the user who created the proudct? Note: This property should NOT be changed once Mifos is Live.'),(22,'office-opening-balances-contra-account',0,1,NULL);
/*!40000 ALTER TABLE `c_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_external_service`
--

DROP TABLE IF EXISTS `c_external_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `c_external_service` (
  `name` varchar(150) NOT NULL,
  `value` varchar(250) DEFAULT NULL,
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_external_service`
--

LOCK TABLES `c_external_service` WRITE;
/*!40000 ALTER TABLE `c_external_service` DISABLE KEYS */;
INSERT INTO `c_external_service` VALUES ('s3_access_key',NULL),('s3_bucket_name',NULL),('s3_secret_key',NULL);
/*!40000 ALTER TABLE `c_external_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `display_name` varchar(50) NOT NULL,
  `cron_expression` varchar(20) CHARACTER SET latin1 NOT NULL,
  `create_time` datetime NOT NULL,
  `task_priority` smallint(6) NOT NULL DEFAULT '5',
  `group_name` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `previous_run_start_time` datetime DEFAULT NULL,
  `next_run_time` datetime DEFAULT NULL,
  `job_key` varchar(500) DEFAULT NULL,
  `initializing_errorlog` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `currently_running` tinyint(1) NOT NULL DEFAULT '0',
  `updates_allowed` tinyint(1) NOT NULL DEFAULT '1',
  `scheduler_group` smallint(2) NOT NULL DEFAULT '0',
  `is_misfired` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job`
--

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;
INSERT INTO `job` VALUES (1,'Update loan Summary','Update loan Summary','0 0 22 1/1 * ? *','2015-06-03 02:56:57',5,NULL,NULL,'2015-06-03 09:30:00','Update loan SummaryJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(2,'Update Loan Arrears Ageing','Update Loan Arrears Ageing','0 1 0 1/1 * ? *','2015-06-03 02:56:57',5,NULL,NULL,'2015-06-03 11:31:00','Update Loan Arrears AgeingJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(3,'Update Loan Paid In Advance','Update Loan Paid In Advance','0 5 0 1/1 * ? *','2015-06-03 02:56:57',5,NULL,NULL,'2015-06-03 11:35:00','Update Loan Paid In AdvanceJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(4,'Apply Annual Fee For Savings','Apply Annual Fee For Savings','0 20 22 1/1 * ? *','2015-06-03 02:56:57',5,NULL,NULL,'2015-06-03 09:50:00','Apply Annual Fee For SavingsJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(5,'Apply Holidays To Loans','Apply Holidays To Loans','0 0 12 * * ?','2015-06-03 02:56:57',5,NULL,NULL,'2015-06-03 23:30:00','Apply Holidays To LoansJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(6,'Post Interest For Savings','Post Interest For Savings','0 0 0 1/1 * ? *','2015-06-03 02:56:58',5,NULL,NULL,'2015-06-03 11:30:00','Post Interest For SavingsJobDetail3 _ DEFAULT',NULL,1,0,1,1,0),(7,'Transfer Fee For Loans From Savings','Transfer Fee For Loans From Savings','0 1 0 1/1 * ? *','2015-06-03 02:57:00',5,NULL,NULL,'2015-06-03 11:31:00','Transfer Fee For Loans From SavingsJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(8,'Pay Due Savings Charges','Pay Due Savings Charges','0 0 12 * * ?','2013-09-23 00:00:00',5,NULL,NULL,'2015-06-03 23:30:00','Pay Due Savings ChargesJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(9,'Update Accounting Running Balances','Update Accounting Running Balances','0 1 0 1/1 * ? *','2015-06-03 02:57:00',5,NULL,NULL,'2015-06-03 11:31:00','Update Accounting Running BalancesJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(10,'Execute Standing Instruction','Execute Standing Instruction','0 0 0 1/1 * ? *','2015-06-03 02:57:04',5,NULL,NULL,'2015-06-03 11:30:00','Execute Standing InstructionJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(11,'Add Accrual Transactions','Add Accrual Transactions','0 1 0 1/1 * ? *','2015-06-03 02:57:04',3,NULL,NULL,'2015-06-03 11:31:00','Add Accrual TransactionsJobDetail3 _ DEFAULT',NULL,1,0,1,3,0),(12,'Apply penalty to overdue loans','Apply penalty to overdue loans','0 0 0 1/1 * ? *','2015-06-03 02:57:04',5,NULL,NULL,'2015-06-03 11:30:00','Apply penalty to overdue loansJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(13,'Update Non Performing Assets','Update Non Performing Assets','0 0 0 1/1 * ? *','2015-06-03 02:57:04',5,NULL,NULL,'2015-06-03 11:30:00','Update Non Performing AssetsJobDetail3 _ DEFAULT',NULL,1,0,1,3,0),(14,'Transfer Interest To Savings','Transfer Interest To Savings','0 2 0 1/1 * ? *','2015-06-03 02:57:05',4,NULL,NULL,'2015-06-03 11:32:00','Transfer Interest To SavingsJobDetail3 _ DEFAULT',NULL,1,0,1,1,0),(15,'Update Deposit Accounts Maturity details','Update Deposit Accounts Maturity details','0 0 0 1/1 * ? *','2015-06-03 02:57:05',5,NULL,NULL,'2015-06-03 11:30:00','Update Deposit Accounts Maturity detailsJobDetail3 _ DEFAULT',NULL,1,0,1,0,0),(16,'Add Periodic Accrual Transactions','Add Periodic Accrual Transactions','0 2 0 1/1 * ? *','2015-06-03 02:57:06',2,NULL,NULL,'2015-06-03 11:32:00','Add Periodic Accrual TransactionsJobDetail3 _ DEFAULT',NULL,1,0,1,3,0),(17,'Recalculate Interest For Loans','Recalculate Interest For Loans','0 1 0 1/1 * ? *','2015-06-03 02:57:07',4,NULL,NULL,'2015-06-03 11:31:00','Recalculate Interest For LoansJobDetail3 _ DEFAULT',NULL,1,0,1,3,0),(18,'Generate Mandatory Savings Schedule','Generate Mandatory Savings Schedule','0 5 0 1/1 * ? *','2015-06-03 02:57:12',5,NULL,NULL,'2015-06-03 11:35:00','Generate Mandatory Savings ScheduleJobDetail3 _ DEFAULT',NULL,1,0,1,0,0);
/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_run_history`
--

DROP TABLE IF EXISTS `job_run_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_run_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(10) CHARACTER SET latin1 NOT NULL,
  `error_message` text,
  `trigger_type` varchar(25) NOT NULL,
  `error_log` text,
  PRIMARY KEY (`id`),
  KEY `scheduledjobsFK` (`job_id`),
  CONSTRAINT `scheduledjobsFK` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_run_history`
--

LOCK TABLES `job_run_history` WRITE;
/*!40000 ALTER TABLE `job_run_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `job_run_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_account_transfer_details`
--

DROP TABLE IF EXISTS `m_account_transfer_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_account_transfer_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_office_id` bigint(20) NOT NULL,
  `to_office_id` bigint(20) NOT NULL,
  `from_client_id` bigint(20) DEFAULT NULL,
  `to_client_id` bigint(20) DEFAULT NULL,
  `from_savings_account_id` bigint(20) DEFAULT NULL,
  `to_savings_account_id` bigint(20) DEFAULT NULL,
  `from_loan_account_id` bigint(20) DEFAULT NULL,
  `to_loan_account_id` bigint(20) DEFAULT NULL,
  `transfer_type` smallint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_account_transfer_details_from_office` (`from_office_id`),
  KEY `FK_m_account_transfer_details_to_office` (`to_office_id`),
  KEY `FK_m_account_transfer_details_from_client` (`from_client_id`),
  KEY `FK_m_account_transfer_details_to_client` (`to_client_id`),
  KEY `FK_m_account_transfer_details_from_savings_account` (`from_savings_account_id`),
  KEY `FK_m_account_transfer_details_to_savings_account` (`to_savings_account_id`),
  KEY `FK_m_account_transfer_details_from_loan_account` (`from_loan_account_id`),
  KEY `FK_m_account_transfer_details_to_loan_account` (`to_loan_account_id`),
  CONSTRAINT `FK_m_account_transfer_details_from_client` FOREIGN KEY (`from_client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_from_loan_account` FOREIGN KEY (`from_loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_from_office` FOREIGN KEY (`from_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_from_savings_account` FOREIGN KEY (`from_savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_to_client` FOREIGN KEY (`to_client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_to_loan_account` FOREIGN KEY (`to_loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_to_office` FOREIGN KEY (`to_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_account_transfer_details_to_savings_account` FOREIGN KEY (`to_savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_account_transfer_details`
--

LOCK TABLES `m_account_transfer_details` WRITE;
/*!40000 ALTER TABLE `m_account_transfer_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_account_transfer_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_account_transfer_standing_instructions`
--

DROP TABLE IF EXISTS `m_account_transfer_standing_instructions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_account_transfer_standing_instructions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `account_transfer_details_id` bigint(20) NOT NULL,
  `priority` tinyint(2) NOT NULL,
  `status` tinyint(2) NOT NULL,
  `instruction_type` tinyint(2) NOT NULL,
  `amount` decimal(19,6) DEFAULT NULL,
  `valid_from` date NOT NULL,
  `valid_till` date DEFAULT NULL,
  `recurrence_type` tinyint(1) NOT NULL,
  `recurrence_frequency` smallint(5) DEFAULT NULL,
  `recurrence_interval` smallint(5) DEFAULT NULL,
  `recurrence_on_day` smallint(2) DEFAULT NULL,
  `recurrence_on_month` smallint(2) DEFAULT NULL,
  `last_run_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FK_m_standing_instructions_account_transfer_details` (`account_transfer_details_id`),
  CONSTRAINT `FK_m_standing_instructions_account_transfer_details` FOREIGN KEY (`account_transfer_details_id`) REFERENCES `m_account_transfer_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_account_transfer_standing_instructions`
--

LOCK TABLES `m_account_transfer_standing_instructions` WRITE;
/*!40000 ALTER TABLE `m_account_transfer_standing_instructions` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_account_transfer_standing_instructions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_account_transfer_standing_instructions_history`
--

DROP TABLE IF EXISTS `m_account_transfer_standing_instructions_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_account_transfer_standing_instructions_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `standing_instruction_id` bigint(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `execution_time` datetime NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `error_log` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_account_transfer_standing_instructions_history` (`standing_instruction_id`),
  CONSTRAINT `FK_m_account_transfer_standing_instructions_m_history` FOREIGN KEY (`standing_instruction_id`) REFERENCES `m_account_transfer_standing_instructions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_account_transfer_standing_instructions_history`
--

LOCK TABLES `m_account_transfer_standing_instructions_history` WRITE;
/*!40000 ALTER TABLE `m_account_transfer_standing_instructions_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_account_transfer_standing_instructions_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_account_transfer_transaction`
--

DROP TABLE IF EXISTS `m_account_transfer_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_account_transfer_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_transfer_details_id` bigint(20) NOT NULL,
  `from_savings_transaction_id` bigint(20) DEFAULT NULL,
  `from_loan_transaction_id` bigint(20) DEFAULT NULL,
  `to_savings_transaction_id` bigint(20) DEFAULT NULL,
  `to_loan_transaction_id` bigint(20) DEFAULT NULL,
  `is_reversed` tinyint(1) NOT NULL,
  `transaction_date` date NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_account_transfer_transaction_from_m_savings_transaction` (`from_savings_transaction_id`),
  KEY `FK_m_account_transfer_transaction_to_m_savings_transaction` (`to_savings_transaction_id`),
  KEY `FK_m_account_transfer_transaction_to_m_loan_transaction` (`to_loan_transaction_id`),
  KEY `FK_m_account_transfer_transaction_from_m_loan_transaction` (`from_loan_transaction_id`),
  KEY `FK_m_account_transfer_transaction_account_detail` (`account_transfer_details_id`),
  CONSTRAINT `FK_m_account_transfer_transaction_account_detail` FOREIGN KEY (`account_transfer_details_id`) REFERENCES `m_account_transfer_details` (`id`),
  CONSTRAINT `FK_m_account_transfer_transaction_from_m_loan_transaction` FOREIGN KEY (`from_loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `FK_m_account_transfer_transaction_from_m_savings_transaction` FOREIGN KEY (`from_savings_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`),
  CONSTRAINT `FK_m_account_transfer_transaction_to_m_loan_transaction` FOREIGN KEY (`to_loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `FK_m_account_transfer_transaction_to_m_savings_transaction` FOREIGN KEY (`to_savings_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_account_transfer_transaction`
--

LOCK TABLES `m_account_transfer_transaction` WRITE;
/*!40000 ALTER TABLE `m_account_transfer_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_account_transfer_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_appuser`
--

DROP TABLE IF EXISTS `m_appuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_appuser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `office_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `username` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `firsttime_login_remaining` bit(1) NOT NULL,
  `nonexpired` bit(1) NOT NULL,
  `nonlocked` bit(1) NOT NULL,
  `nonexpired_credentials` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `last_time_password_updated` date NOT NULL,
  `password_never_expires` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'define if the password, should be check for validity period or not',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_org` (`username`),
  KEY `FKB3D587CE0DD567A` (`office_id`),
  KEY `fk_m_appuser_002x` (`staff_id`),
  KEY `last_time_password_updated` (`last_time_password_updated`),
  CONSTRAINT `FKB3D587CE0DD567A` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `fk_m_appuser_002` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_appuser`
--

LOCK TABLES `m_appuser` WRITE;
/*!40000 ALTER TABLE `m_appuser` DISABLE KEYS */;
INSERT INTO `m_appuser` VALUES (1, 0, 1, NULL, 'mifos', 'App', 'Administrator', '729dd9a1c041b56c1619e6dd4719773d7d1a7e2bf5c68f3e9b748c49cf02e180', 'demomfi@mifos.org', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(2, 0, 1, 3, '000101', 'Ashutosh', 'Desai', '6f4c2a98040370edd3ecd05d6bd10e88755925c2995f1262e9b09fc8e5cf19d2', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(3, 0, 1, 4, '000112', 'Hindol', 'Roy', 'ea3002bc1524570892cb8641c921abb647741178626f6e0cc9b2f3d4ce23eaa7', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(4, 0, 1, 6, '000236', 'Dhiren', 'Chedda', '83068ddbdca81c0ac56e159028c927c587bad840789ce8da2bd81a6f65b12c03', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(5, 0, 1, 58, '000246', 'Krishna', 'Mohan', '1fcec930cdcd94026adab8d94427da4562d6e986298c512c1a1bea3e9fc2e885', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(6, 0, 1, 5, '000230', 'Ganesan', 'SS', 'c78e1e1036a2ebaaa28c943d823d5e87ada31d603e976e2484c3a3fa06766f7d', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(7, 0, 1, 52, '000194', 'Rahul', 'Thorat', 'f2547e8e2ad3637d692474978471c70798d7a3cf7fb2cfdf036db3eecfe45ec5', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(8, 0, 1, 53, 'cntrt3', 'Dawood', 'Mulla', '96ab1c4926e6800dafdae3f994b4843670f237451a010045c03e60a5d128ea09', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(9, 0, 1, NULL, 'cntrt1', 'Sabina', 'Simon', 'baa661f50e71f8510308cd992eee2f02e5cf4834bad97d883fbb1a95ceffe8c2', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(10, 0, 1, 55, 'cntrt2', 'Pooja', 'Warikoo', 'e96aaed4bbfbad511a5a4675fd2a67efeeef84357b6fbe66da63d32484879897', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(11, 0, 1, 56, 'cntrt31', 'Ruksar', 'Shaikh', 'ea88e554b8565d7c466ad9ed1476fffe45cf43b97518aa536a8e207f25f23c32', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(12, 0, 1, 57, 'cntrt32', 'Aniket', 'Gaikwad', '7d8ba892bcc30225dbfba7d0bcee4b69cb5bd45137375a0065d09d09530ed17d', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(13, 0, 1, 7, '000205', 'Miteswar', 'Mahajan', '39352566198615ce463a0d239c804d748d68700bd36accec7b5c1e952ad005f5', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(14, 0, 1, 49, '000199', 'Anand', 'Mittal', '62bb9eda667a2e7f1b3a379708f86f628fab7c1eef123e35f6e7d7359a765b3a', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(15, 0, 1, 50, '000237', 'Pankaj', 'Sharma', 'd46ae202451013a7927320abf3f7b345f1f4c046f749196aba2f7ca36fc5f3be', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(16, 0, 1, 51, '000253', 'Shalini', 'Pandey', '05d79ff205085ed6203fc42926ce82d01cb7dd696d0b0db043f132edb22f47c0', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(17, 0, 4, 1, '000104', 'Tribhuvan', 'Petshali', 'cebe91b2475ee25097b577557aa2cc8dc85e63360b69891b03b13be992a5890b', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(18, 0, 3, NULL, '000178', 'Manoj', 'Passi', 'ec9bb909663b0e4c9a438be4fcf2c3a5f8e3c8a326488881ec87d370ffbba838', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(19, 0, 3, NULL, 'sheba', 'Sheba', 'Khanam', '7e248565b1236c40cd470d77745b0e6b805a40b254d7e9247bb88bafe94eb83f', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(20, 0, 2, NULL, '000220', 'Ravi', 'Verma', 'c1eaba378d300d2e08b958ed0b1418d0fd2be2abba8b87c5ce8ce4c0f036d6e4', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(21, 0, 2, NULL, '000245', 'Manoj', 'Kumar', '893c634138ce9ed4ea64ea668a74ad2dcec98821f4205cf4ac1d13ca30460958', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(22, 0, 5, 15, '000124', 'Jitendra', 'Taneja', 'c169d3c7e297eb8d9077242e4287c21722a2d204ee7542ec90f4682b6b56a107', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(23, 0, 11, 16, '000210', 'Mahesh', 'Gaur', '817558d6dd6ebd509c51d8cd73ad873fd1931c02fc0ea01d679a3eabfeb57551', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(24, 0, 11, 17, '000186', 'Vimal', 'Lakhani', '7f08c1de242b5186fe919f48c6daff9ea38405856331ad789864b2c1c951185e', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(25, 0, 9, 18, '000208', 'Vinod', 'Maheshwari', '22cf44e49d5615c0aa572fb4c5d352ed06e0e81dedf5e4c1cb8c8b5c026896e2', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(26, 0, 9, 23, '000204', 'Neeraj', 'Gupta', 'fcb6210e27ece54ed4bbe8e985ab7347119f678d05598602ad9ab9606339b65c', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(27, 0, 8, 19, '000185', 'Dilip', 'Sahni', '2f31d9bb684b113afb9255d58c2baeb2a24cbd0f49abd4ec2db6a050d1e09a51', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(28, 0, 10, 20, '000138', 'Naveen', 'Joshi', '11a39eb3d275bd39b950e2023f99042576458334a8cd965f99052d0c4ea136aa', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(29, 0, 10, NULL, '000217', 'Amit', 'Nayak', 'a189f0cf27ab904ec861257dfe06c5f7a71e78858cabb29a953daa10c2f4cfe1', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(30, 0, 8, 22, '000258', 'Preeti', 'Sharma', 'dcdd2d56241d77973aeb865e9cba9d378f33e9ba94c33d24b24343aff7a42119', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(31, 0, 7, 24, '000119', 'Manivannan', 'P', '1b5813204cc727856417205a580f32c6a7048e4db56cfa9e3af0d1deace23def', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(32, 0, 14, NULL, '000121', 'Sugumar', 'Moorthy', '2b4b99ff637828fbe3dfb2dea2b4f722fac9deee1ae17babdfb8e61e9f2994bd', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(33, 0, 14, 26, '000187', 'Elangovan', 'S', 'c0152f24acff1c1fd3943e69f6908154a2535b985beae1cd3e294dc642867130', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(34, 0, 14, NULL, '000233', 'D Suresh', 'Babu', '66ee6d7d6cad3e2500240ac8713a174a453e09a59dbb1f8d6bddc32ab881a694', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(35, 0, 19, 28, '000223', 'P', 'Kamaraju', 'bd424ca0684231c708102230bc3fee099480c36737f7061b4808590ddd5c931e', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(36, 0, 19, 29, '000213', 'R', 'Latchathipathy', '1374d88cd5e9e012edd465bdd326c6bb3af0d63c95e827677f5388a0ffe91bc4', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(37, 0, 21, 30, '000215', 'S', 'Kanagaraj', '88f18cfc3dcbacd2cf511431b752d12b77be425a8cff07c088946806b919b5a3', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(38, 0, 21, 31, '000238', 'Mani', 'N', '52cc5edf0419c1c78e5c082e6572f21cefc3c3b1c769e82f45a8604e8cd395de', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(39, 0, 15, 32, '000207', 'G', 'Selvakumar', '665f06efc82af24b240185f5d2bcdc92bb11b6632cf4a5e636fa0190852f9bef', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(40, 0, 15, 33, '000250', 'Shalini', 'R', '4c1f3f8dfed079a874252eaec002f64af911ee35e2d16805abd3bb366922fe7e', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(41, 0, 6, 34, '000109', 'Sudhir', 'Bhanushali', '40fc4dc0ab7c7a46489b5d5ffdb4d3e6bbeaef5acd888fa33e5daa69172390e4', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(42, 0, 20, 35, '000130', 'Rajiv', 'Ranjan', 'ac19820c8738c5815e9abfe8778249496b4f4b9c0700aacaa4923dc7b3deacc8', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(43, 0, 20, 36, '000251', 'Pooja', 'Upadhyay', '46ff9ff15ccd157bc20e5e8845b0f7abb6dce7f2fd19373974e0fd53892f7f80', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(44, 0, 16, 37, '000126', 'Vijay', 'Singh', 'b748d6d05ba5b4d44081ceb906a32e00b47c1e5045bcbbee515f117fe14c6c07', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(45, 0, 16, 38, '000214', 'Ganesh', 'Chorghe', '433b01395387d6de6fe5a3c890619734a380ecce5cee3380053317e9496f294d', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(46, 0, 16, 39, '000231', 'Mugdha', 'Gandhi', '278bdb1d180eccbf54f4675956338fa1824fefcb12de32452f0343e5d42c1456', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(47, 0, 12, 40, '000108', 'Rajesh', 'Dalvi', 'eb2cb239d0648f90a3d06225abf0fdcd3eb0965578084ecb64b8c187804ef987', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(48, 0, 12, 42, '000203', 'Anil', 'Shinde', '06e2eea389bdfa809de7e4d7179fe1c11f2166e4e57bf81a3a12763a581e27aa', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(49, 0, 12, 43, '000240', 'Vaibhav', 'More', '6844d20cc138f193a3016cfcf4a412208eaa78373ede68ccde7e7dd7668aacc8', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(50, 0, 13, 44, '000196', 'Anand', 'Korgaonkar', 'e0f870599742e2a816c9c23801d683b751139e594039d8dbf36c28ab1ee49dab', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(51, 0, 13, 45, '000254', 'Vijay', 'Potdar', '0ad72cb04f597c35fbd3dae6f167f638511c7b7292061190dace291489decdd1', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(52, 0, 13, NULL, 'TXN0426', 'Fauziya', 'Sayyed', '6234d60d246e0af38eb395a3267ea43e02c3e4284f12803fa88f59f2eebd7560', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(53, 0, 22, NULL, '000201', 'Mangesh', 'Nakure', '9861f770c22ceb1f1dd932c50bcb6f714f03197e80eb7b66cdafe4d6673cbb6f', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0),
(54, 0, 22, 48, '000255', 'Vaibhav', 'Nagtilak', '8c5b87c5953fa037d5cf9e92832dd258c88b2581fb7d478ba6b5bbbec073f0f3', '', b'0', b'1', b'1', b'1', b'1', '2015-07-21', 0);
/*!40000 ALTER TABLE `m_appuser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_appuser_previous_password`
--

DROP TABLE IF EXISTS `m_appuser_previous_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_appuser_previous_password` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `removal_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `m_appuser_previous_password_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `m_appuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_appuser_previous_password`
--

LOCK TABLES `m_appuser_previous_password` WRITE;
/*!40000 ALTER TABLE `m_appuser_previous_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_appuser_previous_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_appuser_role`
--

DROP TABLE IF EXISTS `m_appuser_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_appuser_role` (
  `appuser_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`appuser_id`,`role_id`),
  KEY `FK7662CE59B4100309` (`appuser_id`),
  KEY `FK7662CE5915CEC7AB` (`role_id`),
  CONSTRAINT `FK7662CE5915CEC7AB` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`),
  CONSTRAINT `FK7662CE59B4100309` FOREIGN KEY (`appuser_id`) REFERENCES `m_appuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_appuser_role`
--

LOCK TABLES `m_appuser_role` WRITE;
/*!40000 ALTER TABLE `m_appuser_role` DISABLE KEYS */;
INSERT INTO `m_appuser_role` VALUES (1, 1),
(2, 16),
(3, 16),
(4, 12),
(5, 12),
(6, 15),
(7, 15),
(8, 15),
(9, 15),
(10, 15),
(11, 15),
(12, 15),
(13, 13),
(14, 13),
(15, 13),
(16, 13),
(17, 10),
(18, 10),
(19, 8),
(20, 10),
(21, 8),
(22, 10),
(23, 10),
(24, 8),
(25, 10),
(26, 8),
(27, 10),
(28, 10),
(29, 8),
(30, 8),
(31, 10),
(32, 10),
(33, 10),
(34, 8),
(35, 10),
(36, 8),
(37, 10),
(38, 8),
(39, 10),
(40, 8),
(41, 10),
(42, 10),
(43, 8),
(44, 10),
(45, 8),
(46, 8),
(47, 10),
(48, 8),
(49, 8),
(50, 8),
(51, 10),
(52, 8),
(53, 10),
(54, 8);

/*!40000 ALTER TABLE `m_appuser_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_calendar`
--

DROP TABLE IF EXISTS `m_calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_calendar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `location` varchar(50) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `duration` smallint(6) DEFAULT NULL,
  `calendar_type_enum` smallint(5) NOT NULL,
  `repeating` tinyint(1) NOT NULL DEFAULT '0',
  `recurrence` varchar(100) DEFAULT NULL,
  `remind_by_enum` smallint(5) DEFAULT NULL,
  `first_reminder` smallint(11) DEFAULT NULL,
  `second_reminder` smallint(11) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_calendar`
--

LOCK TABLES `m_calendar` WRITE;
/*!40000 ALTER TABLE `m_calendar` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_calendar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_calendar_history`
--

DROP TABLE IF EXISTS `m_calendar_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_calendar_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `calendar_id` bigint(20) NOT NULL,
  `title` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `location` varchar(50) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `duration` smallint(6) DEFAULT NULL,
  `calendar_type_enum` smallint(5) NOT NULL,
  `repeating` tinyint(1) NOT NULL DEFAULT '0',
  `recurrence` varchar(100) DEFAULT NULL,
  `remind_by_enum` smallint(5) DEFAULT NULL,
  `first_reminder` smallint(11) DEFAULT NULL,
  `second_reminder` smallint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_calendar_m_calendar_history` (`calendar_id`),
  CONSTRAINT `FK_m_calendar_m_calendar_history` FOREIGN KEY (`calendar_id`) REFERENCES `m_calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_calendar_history`
--

LOCK TABLES `m_calendar_history` WRITE;
/*!40000 ALTER TABLE `m_calendar_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_calendar_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_calendar_instance`
--

DROP TABLE IF EXISTS `m_calendar_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_calendar_instance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `calendar_id` bigint(20) NOT NULL,
  `entity_id` bigint(20) NOT NULL,
  `entity_type_enum` smallint(5) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_calendar_m_calendar_instance` (`calendar_id`),
  CONSTRAINT `FK_m_calendar_m_calendar_instance` FOREIGN KEY (`calendar_id`) REFERENCES `m_calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_calendar_instance`
--

LOCK TABLES `m_calendar_instance` WRITE;
/*!40000 ALTER TABLE `m_calendar_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_calendar_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_cashier_transactions`
--

DROP TABLE IF EXISTS `m_cashier_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_cashier_transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cashier_id` bigint(20) NOT NULL,
  `txn_type` smallint(5) NOT NULL,
  `txn_amount` decimal(19,6) NOT NULL,
  `txn_date` date NOT NULL,
  `created_date` datetime NOT NULL,
  `entity_type` varchar(50) DEFAULT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `txn_note` varchar(200) DEFAULT NULL,
  `currency_code` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IK_m_teller_transactions_m_cashier` (`cashier_id`),
  CONSTRAINT `FK_m_teller_transactions_m_cashiers` FOREIGN KEY (`cashier_id`) REFERENCES `m_cashiers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_cashier_transactions`
--

LOCK TABLES `m_cashier_transactions` WRITE;
/*!40000 ALTER TABLE `m_cashier_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_cashier_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_cashiers`
--

DROP TABLE IF EXISTS `m_cashiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_cashiers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `staff_id` bigint(20) DEFAULT NULL,
  `teller_id` bigint(20) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `start_time` varchar(10) DEFAULT NULL,
  `end_time` varchar(10) DEFAULT NULL,
  `full_day` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IK_m_cashiers_m_staff` (`staff_id`),
  KEY `IK_m_cashiers_m_teller` (`teller_id`),
  CONSTRAINT `FK_m_cashiers_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `FK_m_cashiers_m_teller` FOREIGN KEY (`teller_id`) REFERENCES `m_tellers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_cashiers`
--

LOCK TABLES `m_cashiers` WRITE;
/*!40000 ALTER TABLE `m_cashiers` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_cashiers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_charge`
--

DROP TABLE IF EXISTS `m_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `charge_applies_to_enum` smallint(5) NOT NULL,
  `charge_time_enum` smallint(5) NOT NULL,
  `charge_calculation_enum` smallint(5) NOT NULL,
  `charge_payment_mode_enum` smallint(5) DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `fee_on_day` smallint(5) DEFAULT NULL,
  `fee_interval` smallint(5) DEFAULT NULL,
  `fee_on_month` smallint(5) DEFAULT NULL,
  `is_penalty` tinyint(1) NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `min_cap` decimal(19,6) DEFAULT NULL,
  `max_cap` decimal(19,6) DEFAULT NULL,
  `fee_frequency` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_charge`
--

LOCK TABLES `m_charge` WRITE;
/*!40000 ALTER TABLE `m_charge` DISABLE KEYS */;
(1, 'TW EMI DPC', 'INR', 1, 9, 1, 0, 500.000000, NULL, NULL, NULL, 1, 1, 0, NULL, NULL, NULL),
(2, 'Document Charge', 'INR', 1, 1, 1, 0, 250.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(3, 'ECS charge', 'INR', 1, 1, 1, 0, 100.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(4, 'Insurance Charge', 'INR', 1, 1, 1, 0, 500.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(5, 'Processing Fee', 'INR', 1, 1, 1, 0, 1000.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(6, 'Stamping Charge', 'INR', 1, 1, 2, 0, 0.150000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(7, 'Subvention Charge', 'INR', 1, 1, 1, 0, 1000.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL),
(8, 'TW Precautionary Charge', 'INR', 1, 1, 1, 0, 100.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL);

/*!40000 ALTER TABLE `m_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_client`
--

DROP TABLE IF EXISTS `m_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(20) NOT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '300',
  `sub_status` int(11) DEFAULT NULL,
  `activation_date` date DEFAULT NULL,
  `office_joining_date` date DEFAULT NULL,
  `office_id` bigint(20) NOT NULL,
  `transfer_to_office_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `middlename` varchar(50) DEFAULT NULL,
  `lastname` varchar(50) DEFAULT NULL,
  `fullname` varchar(100) DEFAULT NULL,
  `display_name` varchar(100) NOT NULL,
  `mobile_no` varchar(50) DEFAULT NULL,
  `gender_cv_id` int(11) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `image_id` bigint(20) DEFAULT NULL,
  `closure_reason_cv_id` int(11) DEFAULT NULL,
  `closedon_date` date DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_on` date DEFAULT NULL,
  `submittedon_date` date DEFAULT NULL,
  `submittedon_userid` bigint(20) DEFAULT NULL,
  `activatedon_userid` bigint(20) DEFAULT NULL,
  `closedon_userid` bigint(20) DEFAULT NULL,
  `default_savings_product` bigint(20) DEFAULT NULL,
  `default_savings_account` bigint(20) DEFAULT NULL,
  `client_type_cv_id` int(11) DEFAULT NULL,
  `client_classification_cv_id` int(11) DEFAULT NULL,
  `reject_reason_cv_id` int(11) DEFAULT NULL,
  `rejectedon_date` date DEFAULT NULL,
  `rejectedon_userid` bigint(20) DEFAULT NULL,
  `withdraw_reason_cv_id` int(11) DEFAULT NULL,
  `withdrawn_on_date` date DEFAULT NULL,
  `withdraw_on_userid` bigint(20) DEFAULT NULL,
  `reactivated_on_date` date DEFAULT NULL,
  `reactivated_on_userid` bigint(20) DEFAULT NULL,
  `fathername` varchar(100) DEFAULT NULL,
  `emailaddress` varchar(50) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `password` varchar(15) DEFAULT NULL,
  `marital_cv_id` int(11) DEFAULT NULL,
  `religion_cv_id` int(11) DEFAULT NULL,
  `dependent_cv_id` int(11) DEFAULT NULL,
  `education_cv_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_no_UNIQUE` (`account_no`),
  UNIQUE KEY `external_id` (`external_id`),
  UNIQUE KEY `mobile_no_UNIQUE` (`mobile_no`),
  KEY `FKCE00CAB3E0DD567A` (`office_id`),
  KEY `FK_m_client_m_image` (`image_id`),
  KEY `client_staff_id` (`staff_id`),
  KEY `FK_m_client_m_code` (`closure_reason_cv_id`),
  KEY `FK_m_client_m_office` (`transfer_to_office_id`),
  KEY `FK_m_client_m_savings_product` (`default_savings_product`),
  KEY `FK_m_client_m_savings_account` (`default_savings_account`),
  KEY `FK_m_client_type_m_code_value` (`client_type_cv_id`),
  KEY `FK_m_client_classification_m_code_value` (`client_classification_cv_id`),
  KEY `FK1_m_client_gender_m_code_value` (`gender_cv_id`),
  KEY `FK_m_client_substatus_m_code_value` (`sub_status`),
  KEY `FK_m_client_type_mcode_value_reject` (`reject_reason_cv_id`),
  KEY `FK_m_client_type_m_code_value_withdraw` (`withdraw_reason_cv_id`),
  CONSTRAINT `FK1_m_client_gender_m_code_value` FOREIGN KEY (`gender_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FKCE00CAB3E0DD567A` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_client_classification_m_code_value` FOREIGN KEY (`client_classification_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_client_m_code` FOREIGN KEY (`closure_reason_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_client_m_image` FOREIGN KEY (`image_id`) REFERENCES `m_image` (`id`),
  CONSTRAINT `FK_m_client_m_office` FOREIGN KEY (`transfer_to_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_client_m_savings_account` FOREIGN KEY (`default_savings_account`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `FK_m_client_m_savings_product` FOREIGN KEY (`default_savings_product`) REFERENCES `m_savings_product` (`id`),
  CONSTRAINT `FK_m_client_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `FK_m_client_substatus_m_code_value` FOREIGN KEY (`sub_status`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_client_type_m_code_value` FOREIGN KEY (`client_type_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_client_type_m_code_value_withdraw` FOREIGN KEY (`withdraw_reason_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_client_type_mcode_value_reject` FOREIGN KEY (`reject_reason_cv_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_client`
--

LOCK TABLES `m_client` WRITE;
/*!40000 ALTER TABLE `m_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_client_address`
--

CREATE TABLE IF NOT EXISTS `m_client_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `address_type_id` bigint(20) NOT NULL,
  `address_line` varchar(100) NOT NULL DEFAULT '',
  `address_line_two` varchar(100) NOT NULL DEFAULT '',
  `landmark` varchar(20) NOT NULL DEFAULT '',
  `city` varchar(20) NOT NULL DEFAULT '',
  `pincode` varchar(6) DEFAULT NULL,
  `isBoth_perma_same` tinyint(1) DEFAULT NULL,
  `state_type_id` bigint(20) NOT NULL,
  `createdby_id` bigint(11) DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_address_type` (`client_id`,`address_type_id`),
  UNIQUE KEY `unique_address_line` (`address_line`,`address_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
/*!40000 ALTER TABLE `m_client_address` DISABLE KEYS */;

--
-- Dumping data for table `m_client_address`
--

LOCK TABLES `m_client_address` WRITE;
/*!40000 ALTER TABLE `m_client_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_client_address` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `m_client_attendance`
--

DROP TABLE IF EXISTS `m_client_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_client_attendance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL DEFAULT '0',
  `meeting_id` bigint(20) NOT NULL,
  `attendance_type_enum` smallint(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_client_meeting_attendance` (`client_id`,`meeting_id`),
  KEY `FK_m_meeting_m_client_attendance` (`meeting_id`),
  CONSTRAINT `FK_m_client_m_client_attendance` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK_m_meeting_m_client_attendance` FOREIGN KEY (`meeting_id`) REFERENCES `m_meeting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_client_attendance`
--

LOCK TABLES `m_client_attendance` WRITE;
/*!40000 ALTER TABLE `m_client_attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_client_attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_client_identifier`
--

DROP TABLE IF EXISTS `m_client_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_client_identifier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `document_type_id` int(11) NOT NULL,
  `proof_type_id` int(11) NOT NULL,
  `document_key` varchar(50) NOT NULL,
  `validity` date DEFAULT NULL,
  `is_life_time` tinyint(1) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_identifier_key` (`document_type_id`,`document_key`),
  UNIQUE KEY `unique_client_identifier` (`client_id`,`document_type_id`),
  KEY `FK_m_client_document_m_client` (`client_id`),
  KEY `FK_m_client_document_m_code_value` (`document_type_id`),
  CONSTRAINT `FK_m_client_document_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK_m_client_document_m_code_value` FOREIGN KEY (`document_type_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_client_identifier`
--

LOCK TABLES `m_client_identifier` WRITE;
/*!40000 ALTER TABLE `m_client_identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_client_identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_code`
--

DROP TABLE IF EXISTS `m_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_name` varchar(100) DEFAULT NULL,
  `is_system_defined` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_name` (`code_name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_code`
--

LOCK TABLES `m_code` WRITE;
/*!40000 ALTER TABLE `m_code` DISABLE KEYS */;
INSERT INTO `m_code` VALUES (1, 'Customer Identifier', 1),
(2, 'LoanCollateral', 1),
(3, 'LoanPurpose', 1),
(4, 'Gender', 1),
(5, 'YesNo', 1),
(6, 'GuarantorRelationship', 1),
(7, 'AssetAccountTags', 1),
(8, 'LiabilityAccountTags', 1),
(9, 'EquityAccountTags', 1),
(10, 'IncomeAccountTags', 1),
(11, 'ExpenseAccountTags', 1),
(13, 'GROUPROLE', 1),
(14, 'ClientClosureReason', 1),
(15, 'GroupClosureReason', 1),
(16, 'ClientType', 1),
(17, 'ClientClassification', 1),
(18, 'ClientSubStatus', 1),
(19, 'ClientRejectReason', 1),
(20, 'ClientWithdrawReason', 1),
(21, 'Entity to Entity Access Types', 1),
(22, 'CenterClosureReason', 1),
(23, 'MaritalStatus', 1),
(24, 'State UT', 0),
(25, 'residential status', 0),
(26, 'Year since', 0),
(27, 'Proof Type', 1),
(28, 'Religion', 1),
(29, 'Number', 0),
(30, 'GoldFineness', 1),
(31, 'Jewellery', 1),
(32, 'Dependents', 1),
(33, 'Gross Income', 0),
(34, 'Relationship', 0),
(35, 'Other Loans', 0),
(36, 'Asset Owned', 0),
(37, 'Official Relationship', 0),
(38, 'Education', 1),
(39, 'MakeType', 1),
(40, 'GNCB', 1),
(41, 'Address Type', 0),
(42, 'State', 0),
(43, 'Packet Location', 0),
(44, 'Insurance Company List', 0);

/*!40000 ALTER TABLE `m_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_code_value`
--

DROP TABLE IF EXISTS `m_code_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_code_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_id` int(11) NOT NULL,
  `code_value` varchar(100) DEFAULT NULL,
  `code_description` varchar(500) DEFAULT NULL,
  `order_position` int(11) NOT NULL DEFAULT '0',
  `code_score` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_value` (`code_id`,`code_value`),
  KEY `FKCFCEA42640BE071Z` (`code_id`),
  CONSTRAINT `FKCFCEA42640BE071Z` FOREIGN KEY (`code_id`) REFERENCES `m_code` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_code_value`
--

LOCK TABLES `m_code_value` WRITE;
/*!40000 ALTER TABLE `m_code_value` DISABLE KEYS */;
INSERT INTO `m_code_value` VALUES (1, 1, 'Passport', 'passport', 1, NULL),
(2, 1, 'Insurance Card', 'insurance card', 2, NULL),
(3, 1, 'Driver License', 'driver license', 3, NULL),
(4, 1, 'Other', 'other', 8, NULL),
(5, 6, 'Spouse', NULL, 0, NULL),
(6, 6, 'Parent', NULL, 0, NULL),
(7, 6, 'Sibling', NULL, 0, NULL),
(8, 6, 'Business Associate', NULL, 0, NULL),
(9, 6, 'Other', NULL, 0, NULL),
(10, 21, 'Office Access to Loan Products', NULL, 0, NULL),
(11, 21, 'Office Access to Savings Products', NULL, 0, NULL),
(12, 21, 'Office Access to Fees/Charges', NULL, 0, NULL),
(13, 4, 'Male ', 'Male ', 0, NULL),
(14, 4, 'Female', 'Female', 0, NULL),
(17, 24, 'Andaman and Nicobar Islands', 'Andaman and Nicobar Islands', 1, NULL),
(18, 24, 'Andhra Pradesh', 'Andhra Pradesh', 2, NULL),
(19, 24, 'Arunachal Pradesh', 'Arunachal Pradesh', 3, NULL),
(20, 24, 'Assam', 'Assam', 4, NULL),
(21, 24, 'Bihar', 'Bihar', 5, NULL),
(22, 24, 'Chandigarh', 'Chandigarh', 6, NULL),
(23, 24, 'Chhattisgarh', 'Chhattisgarh', 7, NULL),
(24, 24, 'Dadra and Nagar Haveli', 'Dadra and Nagar Haveli', 8, NULL),
(25, 24, 'Daman and Diu', 'Daman and Diu', 9, NULL),
(26, 24, 'Delhi', 'Delhi', 10, NULL),
(27, 24, 'Goa', 'Goa', 11, NULL),
(28, 24, 'Gujarat', 'Gujarat', 12, NULL),
(29, 24, 'Haryana', 'Haryana', 13, NULL),
(30, 24, 'Himachal Pradesh', 'Himachal Pradesh', 14, NULL),
(31, 24, 'Jammu and Kashmir', 'Jammu and Kashmir', 15, NULL),
(32, 24, 'Jharkhand', 'Jharkhand', 16, NULL),
(33, 24, 'Karnataka', 'Karnataka', 17, NULL),
(34, 24, 'Kerala', 'Kerala', 18, NULL),
(35, 24, 'Lakshadweep', 'Lakshadweep', 19, NULL),
(36, 24, 'Madhya Pradesh', 'Madhya Pradesh', 20, NULL),
(37, 24, 'Maharashtra', 'Maharashtra', 21, NULL),
(38, 24, 'Manipur', 'Manipur', 22, NULL),
(39, 24, 'Meghalaya', 'Meghalaya', 23, NULL),
(40, 24, 'Mizoram', 'Mizoram', 24, NULL),
(41, 24, 'Nagaland', 'Nagaland', 25, NULL),
(42, 24, 'Odisha', 'Odisha', 26, NULL),
(43, 24, 'Puducherry', 'Puducherry', 27, NULL),
(44, 24, 'Punjab', 'Punjab', 28, NULL),
(45, 24, 'Rajasthan', 'Rajasthan', 29, NULL),
(46, 24, 'Sikkim', 'Sikkim', 30, NULL),
(47, 24, 'Tamil Nadu', 'Tamil Nadu', 31, NULL),
(48, 24, 'Telangana', 'Telangana', 32, NULL),
(49, 24, 'Tripura', 'Tripura', 33, NULL),
(50, 24, 'Uttar Pradesh', 'Uttar Pradesh', 34, NULL),
(51, 24, 'Uttarakhand', 'Uttarakhand', 35, NULL),
(52, 24, 'West Bengal', 'West Bengal', 36, NULL),
(53, 25, 'Owned', 'owned', 1, NULL),
(54, 25, 'Rented', 'rented', 2, NULL),
(55, 25, 'Parental', 'Parental', 3, NULL),
(56, 25, 'Company', 'company', 4, NULL),
(57, 25, 'Other', 'other', 5, NULL),
(58, 26, '1', '1', 1, NULL),
(59, 26, '2', '2', 2, NULL),
(60, 26, '3', '3', 3, NULL),
(61, 26, '4', '4', 4, NULL),
(62, 26, '5', '5', 5, NULL),
(63, 26, '6', '6', 6, NULL),
(64, 26, '7', '7', 7, NULL),
(65, 26, '8', '8', 8, NULL),
(66, 26, '9', '9', 9, NULL),
(67, 26, '10', '10', 10, NULL),
(68, 26, 'Above 10', 'above 10', 11, NULL),
(69, 2, 'Gold ', 'gold', 0, NULL),
(70, 27, 'ID Only', 'ID only', 1, NULL),
(71, 27, 'ID and Address', 'both ID and address', 3, NULL),
(72, 14, 'Death', 'death', 1, NULL),
(73, 2, 'Two Wheeler', 'two wheeler', 1, NULL),
(74, 23, 'Single', 'Single', 1, NULL),
(75, 23, 'Married', 'married', 2, NULL),
(76, 23, 'Widow', 'widow', 3, NULL),
(77, 23, 'Separated', 'separated', 4, NULL),
(78, 23, 'Divorced', 'divorced', 5, NULL),
(79, 28, 'Hindu', 'hindu', 1, NULL),
(80, 29, '1', '1', 1, NULL),
(81, 30, '22 Carats', '22 carats', 1, NULL),
(82, 31, 'Necklace - Gale ka Haar / Mala', 'Necklace - Gale ka Haar / Mala		Ring', 1, NULL),
(83, 30, '18 Carats', '18 carat', 3, NULL),
(84, 30, '20 Carats', '20 carat', 2, NULL),
(85, 31, 'Necklace - Long - Aram', 'Necklace - Long - Aram', 2, NULL),
(86, 28, 'Muslim', 'muslim', 2, NULL),
(87, 28, 'Sikh', 'Sikh', 3, NULL),
(88, 28, 'Christian', 'christian', 4, NULL),
(89, 28, 'Jain', 'Jain', 5, NULL),
(90, 28, 'Buddhism', 'buddhism', 6, NULL),
(91, 17, 'Managers', 'Chief executives, senior officials, legislators,Administrative, commercial,Production,specialized services,Hospitality, retail and other services managers', 1, NULL),
(92, 17, 'Doctors', 'medical doctors', 2, NULL),
(93, 17, 'Teacher', 'teaching professional', 3, NULL),
(94, 17, 'Professor/Lecturer', 'professor, higher educationist', 4, NULL),
(95, 17, 'Information Technology', 'IT and database engineers', 5, NULL),
(96, 17, 'Researcher', 'Science,engineering, commerce, arts', 6, NULL),
(97, 17, 'Lawyer', 'legal professionals', 7, NULL),
(98, 17, 'Social and religious professional', 'social, religious professionals', 8, NULL),
(99, 17, 'Pharmacist', 'pharmacy and medicine', 9, NULL),
(100, 17, 'Business', 'business executive', 10, NULL),
(101, 17, 'Persona service workers', 'cook, travel attendant, waiters, barber,driver, personal care', 11, NULL),
(102, 17, 'Skilled agricultural', 'skilled agriculture', 12, NULL),
(103, 17, 'Craft and related trade worker', 'Craft and related trade worker', 13, NULL),
(104, 16, 'Salaried', 'salaried', 1, NULL),
(105, 16, 'SENP', 'self employed non professional', 2, NULL),
(106, 16, 'SEP', 'self employed professional', 3, NULL),
(107, 16, 'Housewife', 'housewife', 4, NULL),
(108, 16, 'Others', 'other', 5, NULL),
(109, 17, 'Others', 'others', 14, NULL),
(110, 32, 'One', 'one', 2, NULL),
(111, 19, 'Inappropriate', 'inappropriate', 1, NULL),
(112, 19, 'Invalid data entry', 'invalid data entry', 2, NULL),
(113, 32, 'Two', 'two', 3, NULL),
(114, 32, 'Three', 'three', 4, NULL),
(115, 32, '>three', 'more than 3', 5, NULL),
(116, 33, '<1lakh', 'below 1 lakh', 1, NULL),
(117, 33, '1-3 lakh', '1-3 lakh', 2, NULL),
(118, 33, '3-6 lakh', '3-6 lakh', 3, NULL),
(119, 33, '6-10 lakh', '6-10 lakh', 4, NULL),
(120, 33, '>10 lakh', 'greater than 10 lakh', 5, NULL),
(121, 1, 'Aadhar Card', 'aadhar card', 4, NULL),
(122, 1, 'Ration Card', 'ration card', 5, NULL),
(123, 1, 'Voter ID', 'voter id', 6, NULL),
(124, 1, 'Utility Bill', 'utility Bill', 7, NULL),
(125, 27, 'Address Only', 'address only', 2, NULL),
(126, 34, 'Son', 'son', 1, NULL),
(127, 34, 'Daughter', 'daughter', 2, NULL),
(128, 34, 'Parent', 'Parent', 3, NULL),
(129, 34, 'Relative', 'related', 4, NULL),
(130, 34, 'Friend', 'friend', 5, NULL),
(131, 34, 'Neighbour', 'Neighbour', 6, NULL),
(132, 34, 'Other', 'other', 9, NULL),
(133, 35, 'Auto loan', 'Auto loan', 1, NULL),
(134, 35, 'Home loan', 'home loan', 2, NULL),
(135, 35, 'Personal loan', 'personal loan', 3, NULL),
(136, 35, 'Business Loan', 'business loan', 4, NULL),
(137, 36, 'Car', 'car', 1, NULL),
(138, 36, 'Bike', 'bike', 2, NULL),
(139, 36, 'Home', 'home', 3, NULL),
(140, 37, 'Boss', 'boss', 1, NULL),
(141, 37, 'Supervisor', 'supervisor', 2, NULL),
(142, 37, 'Applicant himself', 'applicant himself', 3, NULL),
(144, 37, 'Other', 'other', 4, NULL),
(145, 38, 'Graduate', 'graduate', 3, NULL),
(146, 38, 'Post Graduate', 'post graduate', 4, NULL),
(147, 38, 'School', 'school', 1, NULL),
(148, 38, 'Undergraduate', 'undergraduate', 2, NULL),
(149, 38, 'Others', 'others', 5, NULL),
(150, 32, 'None', 'none', 1, NULL),
(152, 3, 'Education', 'education', 1, NULL),
(153, 3, 'Travel', 'travel', 2, NULL),
(154, 3, 'Medical Emergency', 'Medical Emergency', 3, NULL),
(155, 3, 'Household Purchases', 'Household Purchases', 4, NULL),
(156, 3, 'Business', 'business', 5, NULL),
(157, 3, 'Personal', 'personal', 6, NULL),
(158, 3, 'Others', 'others', 7, NULL),
(159, 14, 'Defaulting', 'defaulting', 2, NULL),
(160, 14, 'Legal', 'legal as in phishing case', 3, NULL),
(161, 14, 'Others', 'other', 4, NULL),
(162, 19, 'Others', 'others', 3, NULL),
(163, 20, 'Invalid data entry', 'invalid data entry', 1, NULL),
(164, 20, 'Others', 'others', 2, NULL),
(165, 31, 'Necklace - Fancy - Manimal', 'Necklace - Fancy - Manimal', 3, NULL),
(166, 31, 'Necklace with Coins - Kasumalai', 'Necklace with Coins - Kasumalai', 4, NULL),
(167, 31, 'Necklace with Gold Beads - Bormal', 'Necklace with Gold Beads - Bormal', 5, NULL),
(168, 31, 'Chain - Neck -  Light', 'Chain - Neck -  Light', 6, NULL),
(169, 31, 'Chain - Neck -  Heavy', 'Chain - Neck -  Heavy', 7, NULL),
(170, 31, 'Chain - Two Row', 'Chain - Two Row', 8, NULL),
(171, 31, 'Chain - Hand / Wrist', 'Chain - Hand / Wrist', 9, NULL),
(172, 31, 'Chain - Waist /Hip', 'Chain - Waist /Hip', 10, NULL),
(173, 31, 'Chain - Forehead', 'Chain - Forehead', 11, NULL),
(174, 31, 'Chain - Ear ,  Head - Mattal / Matti', 'Chain - Ear ,  Head - Mattal / Matti', 12, NULL),
(175, 31, 'Chain from Mangalasutra - Saradu', 'Chain from Mangalasutra - Saradu', 13, NULL),
(176, 31, 'Locket - Neck', 'Locket - Neck', 14, NULL),
(177, 31, 'Locket - Forehead - Tikka', 'Locket - Forehead - Tikka', 15, NULL),
(178, 31, 'Locket - Others', 'Locket - Others', 16, NULL),
(179, 31, 'Finger Ring - Angoothi', 'Finger Ring - Angoothi', 17, NULL),
(180, 31, 'Ear Ring Small / Drops- Kaan Ki Baali', 'Ear Ring Small / Drops- Kaan Ki Baali', 18, NULL),
(181, 31, 'Ear Ring Long - Jhumka / Jimmki', 'Ear Ring Long - Jhumka / Jimmki', 19, NULL),
(182, 31, 'Ear Ring - Studs with Chain', 'Ear Ring - Studs with Chain', 20, NULL),
(183, 31, 'Nose Ring - Nath / Nathani', 'Nose Ring - Nath / Nathani', 21, NULL),
(184, 31, 'Wedding Necklace - Mangalasutra', 'Wedding Necklace - Mangalasutra', 22, NULL),
(185, 31, 'Bangles -Plain - Kangan', 'Bangles -Plain - Kangan', 23, NULL),
(186, 31, 'Bangles -Step - Kangan', 'Bangles -Step - Kangan', 24, NULL),
(187, 31, 'Bangles -Stone - Kangan', 'Bangles -Stone - Kangan', 25, NULL),
(188, 31, 'Bracelet - Hand Chain - Flexible', 'Bracelet - Hand Chain - Flexible', 26, NULL),
(189, 31, 'Bracelet - Thick - Kappu', 'Bracelet - Thick - Kappu', 27, NULL),
(190, 31, 'Bracelet - Hard - Kadha', 'Bracelet - Hard - Kadha', 28, NULL),
(191, 31, 'Waist Band - Kamar Bandh', 'Waist Band - Kamar Bandh', 29, NULL),
(192, 31, 'Hip Chain', 'Hip Chain', 30, NULL),
(193, 31, 'Anklet -Leg Chain -  Paayal - Kolusu', 'Anklet -Leg Chain -  Paayal - Kolusu', 31, NULL),
(194, 31, 'Toe Ring -', 'Toe Ring -', 32, NULL),
(195, 31, 'Locket -Tikli - Nethisutti', 'Locket -Tikli - Nethisutti', 33, NULL),
(196, 31, 'Round Locket - Dollar', 'Round Locket - Dollar', 34, NULL),
(197, 31, 'Coin - Gold', 'Coin - Gold', 35, NULL),
(198, 31, 'Biscuit - Gold', 'Biscuit - Gold', 36, NULL),
(199, 31, 'Bar - Gold', 'Bar - Gold', 37, NULL),
(200, 31, 'Piece - Gold', 'Piece - Gold', 38, NULL),
(201, 31, 'Beads - Gold - Nanal', 'Beads - Gold - Nanal', 39, NULL),
(202, 31, 'Arm Band - Haath Bandh', 'Arm Band - Haath Bandh', 40, NULL),
(203, 31, 'Traditional Ladies Bracelet - Haath Phool', 'Traditional Ladies Bracelet - Haath Phool', 41, NULL),
(204, 31, 'Studs', 'Studs', 42, NULL),
(205, 2, 'MSME', 'MSME', 2, NULL),
(206, 34, 'Spouse', 'spouse', 7, NULL),
(207, 29, '2', '2', 2, NULL),
(208, 29, '3', '3', 3, NULL),
(209, 29, '4', '4', 4, NULL),
(213, 29, '>4', '>4', 5, NULL),
(214, 1, 'Pan Card', 'Pan Card', 0, NULL),
(215, 40, 'Guarantor', 'guarantor', 1, NULL),
(216, 40, 'Nominee', 'nominee', 2, NULL),
(217, 40, 'Co borrower', 'co borrower', 3, NULL),
(218, 41, 'Present Address', NULL, 0, NULL),
(219, 41, 'Permanent Address', NULL, 0, NULL),
(220, 42, 'Delhi', NULL, 0, NULL),
(221, 41, 'Official', 'Official', 0, NULL),
(222, 39, 'Honda Activa', 'activa 3 G, I , 125 STD , 125 DLX', 1, NULL),
(223, 39, 'Honda Dio', 'New Dio', 2, NULL),
(224, 39, 'Honda Aviator', 'DR, DC', 3, NULL),
(225, 39, 'Honda Unicorn', '150, 160 STD, 160 DLX', 4, NULL),
(226, 39, 'Honda Shine', 'Drum, Disc , CBS', 5, NULL),
(227, 39, 'Honda Twister', 'Drum , Disc', 6, NULL),
(228, 39, 'Honda CBR', '250 R STD, 250R ABS , 250R REP, 150R STD , 150R DLX', 7, NULL),
(229, 39, 'Honda Dream Yuga', 'New Dream Yuga , Dream Yuga Self', 8, NULL),
(230, 39, 'Honda TRIGGER', 'STD, DLX, CBS', 9, NULL),
(231, 39, 'Honda NEO', 'Kick-Alloy , Kick-Self', 10, NULL),
(232, 39, 'Honda CD 110 NEO', 'Honda CD 110 NEO', 11, NULL),
(233, 39, 'TVS XL Super HD', 'TVS XL Super HD', 12, NULL),
(234, 39, 'TVS Scooty Pep Ref', 'TVS Scooty Pep Ref', 13, NULL),
(235, 39, 'TVS Scooty Zest', 'TVS Scooty Zest', 14, NULL),
(236, 39, 'TVS Wego', 'TVS Wego', 15, NULL),
(237, 39, 'TVS Jupiter', 'TVS Jupiter', 16, NULL),
(238, 39, 'TVS Sports ES', 'TVS Sports ES', 17, NULL),
(239, 39, 'TVS Star 110 Mag ES', 'TVS Star 110 Mag ES', 18, NULL),
(240, 39, 'TVS Phoenix', 'Drum ,Disc', 19, NULL),
(241, 39, 'TVS Apache', '160 RTR, 160 RTRRD New , 180 CC. 180 ABS', 20, NULL),
(242, 39, 'Hero Dawn KS', 'Hero Dawn KS', 21, NULL),
(243, 39, 'Hero HF DLX', 'KS Spoke , KS Cast , Spoke SS , Cast SS', 22, NULL),
(244, 39, 'Hero Splendor Plus', 'Splendor Plus Spoke , Splendor Plus Cast', 23, NULL),
(245, 39, 'Hero Splendor Pro', 'Splendor Pro SS Cast , Splendor Pro SS Spoke, Splendor Pro SS Cast , Splendor Pro J1 SS Cast, Splendor Pro Classic', 24, NULL),
(246, 39, 'Hero I Smart', 'I Smart CSS , I Smart SSPK', 25, NULL),
(247, 39, 'Hero Passion', 'Xpro Dr, Xpro Disc, Pro SS Cast, Pro Disc, Pro TR', 26, NULL),
(248, 39, 'Hero Igniter', 'igniter Drum , Igniter Disc', 27, NULL),
(249, 39, 'Hero Hunk', 'Hunk FR-DI , Hunk- DDSS', 28, NULL),
(250, 39, 'Hero Xtreme', 'Xtreme -FD, Xtreme-DD , Xtreme Sport FD , Xtreme Sport DD', 29, NULL),
(251, 39, 'Hero Achiever', 'Hero Achiever', 30, NULL),
(252, 39, 'Hero Super splendor', 'Hero Super splendor', 31, NULL),
(253, 39, 'Hero Glamour', 'Glamour DR , Glamour Disc, Glamour FI', 32, NULL),
(254, 39, 'Hero Karizma R', 'Hero Karizma R', 33, NULL),
(255, 39, 'Hero ZMR', 'Hero ZMR', 34, NULL),
(256, 39, 'Hero Pleasure', 'Pleasure Sheet Metal, Pleasure cast Metal', 35, NULL),
(257, 39, 'Hero Maestro', 'Hero Maestro', 36, NULL),
(258, 39, 'Bajaj Avenger 220 DTS-i', 'Avenger 220 DTS-i', 37, NULL),
(259, 39, 'Bajaj Pulsar', 'Pulsar RS 200 Pulsar AS 200/150 Pulsar 200 NS Pulsar 220 Pulsar 180 Pulsar 150 Pulsar 135 LS', 38, NULL),
(260, 39, 'Bajaj Discover', 'New Discover 150 , New Discover 125, Discover 125 M Discover 100 M Discover 100', 39, NULL),
(261, 39, 'Bajaj Platina', 'Platina 100 Platina 100 ES Platina 100', 40, NULL),
(262, 39, 'Bajaj CT 100', 'Bajaj CT 100', 41, NULL),
(263, 39, 'Yamaha SZ', 'Yamaha SZ S, SZ RR', 42, NULL),
(264, 39, 'Yamaha Ray Z', 'yamaha Ray Z', 43, NULL),
(265, 39, 'Yamaha Saluto', 'yamaha saluto', 44, NULL),
(266, 39, 'Yamaha Fascino', 'Yamaha Fascino', 45, NULL),
(267, 39, 'Yamaha Crux', 'Yamaha Crux', 46, NULL),
(268, 39, 'Yamaha FZ', 'Yamaha FZ S, FZ 16, FZ', 47, NULL),
(269, 39, 'Yamaha YBR', 'YBR 110, YBR 125', 48, NULL),
(270, 39, 'Yamaha Alpha', 'Yamaha Alpha', 49, NULL),
(271, 39, 'Yamaha Ray', 'Yamaha Ray', 50, NULL),
(272, 39, 'Royal Enfield Bullet', 'Bullet 350 Twin Spark,Electra TwinSpark,Bullet 500', 51, NULL),
(273, 39, 'Royal Enfield Classic', 'classic 350,classic 500,classic desert storm,classic chrome', 52, NULL),
(274, 39, 'Royal Enfield Thunderbird', 'thunderbird 350, 500', 53, NULL),
(275, 39, 'Vespa', 'vespa LX,VX,S', 54, NULL),
(276, 43, 'Branch', 'branch', 1, NULL),
(277, 43, 'CMS', 'cms', 2, NULL),
(278, 43, 'others', 'others', 3, NULL),
(279, 44, 'Bajaj Allianz', 'Bajaj Allianz', 1, NULL),
(280, 44, 'Bharati Axa General Insurance', 'Bharati Axa General Insurance', 2, NULL),
(281, 44, 'Future Generali', 'Future Generali', 3, NULL),
(282, 44, 'ICICI Lombard General Insurance Company', 'ICICI Lombard General Insurance Company', 4, NULL),
(283, 44, 'HDFC ERGO General Insurance Company', 'HDFC ERGO General Insurance Company', 5, NULL),
(284, 44, 'L&T General Insurance Company', 'L&T General Insurance Company', 6, NULL),
(285, 44, 'National Insurance Company', 'National Insurance Company', 7, NULL),
(286, 44, 'Oriental Insurance Company', 'Oriental Insurance Company', 8, NULL),
(287, 44, 'Reliance General Insurance Company', 'Reliance General Insurance Company', 9, NULL),
(288, 44, 'SBI General Insurance Company', 'SBI General Insurance Company', 10, NULL),
(289, 44, 'Shriram General Insurance Company', 'Shriram General Insurance Company', 11, NULL),
(290, 44, 'Star Health and Allied Insurance Company', 'Star Health and Allied Insurance Company', 12, NULL),
(291, 44, 'TATA AIG General Insurance Company', 'TATA AIG General Insurance Company', 13, NULL),
(292, 44, 'The New India Assurance Company', 'The New India Assurance Company', 14, NULL),
(293, 44, 'United India Insurance Company', 'United India Insurance Company', 15, NULL),
(294, 44, 'Universal Sompo General Insurance Company', 'Universal Sompo General Insurance Company', 16, NULL),
(295, 44, 'Liberty Videocon General Insurance Company', 'Liberty Videocon General Insurance Company', 17, NULL),
(296, 44, 'IFFCO Tokio General Insurance Company', 'IFFCO Tokio General Insurance Company', 18, NULL),
(297, 44, 'Magma HDI General Insurance Company', 'Magma HDI General Insurance Company', 19, NULL);

/*!40000 ALTER TABLE `m_code_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_currency`
--

DROP TABLE IF EXISTS `m_currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(3) NOT NULL,
  `decimal_places` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `display_symbol` varchar(10) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `internationalized_name_code` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_currency`
--

LOCK TABLES `m_currency` WRITE;
/*!40000 ALTER TABLE `m_currency` DISABLE KEYS */;
INSERT INTO `m_currency` VALUES (1,'AED',2,NULL,NULL,'UAE Dirham','currency.AED'),(2,'AFN',2,NULL,NULL,'Afghanistan Afghani','currency.AFN'),(3,'ALL',2,NULL,NULL,'Albanian Lek','currency.ALL'),(4,'AMD',2,NULL,NULL,'Armenian Dram','currency.AMD'),(5,'ANG',2,NULL,NULL,'Netherlands Antillian Guilder','currency.ANG'),(6,'AOA',2,NULL,NULL,'Angolan Kwanza','currency.AOA'),(7,'ARS',2,NULL,'$','Argentine Peso','currency.ARS'),(8,'AUD',2,NULL,'A$','Australian Dollar','currency.AUD'),(9,'AWG',2,NULL,NULL,'Aruban Guilder','currency.AWG'),(10,'AZM',2,NULL,NULL,'Azerbaijanian Manat','currency.AZM'),(11,'BAM',2,NULL,NULL,'Bosnia and Herzegovina Convertible Marks','currency.BAM'),(12,'BBD',2,NULL,NULL,'Barbados Dollar','currency.BBD'),(13,'BDT',2,NULL,NULL,'Bangladesh Taka','currency.BDT'),(14,'BGN',2,NULL,NULL,'Bulgarian Lev','currency.BGN'),(15,'BHD',3,NULL,NULL,'Bahraini Dinar','currency.BHD'),(16,'BIF',0,NULL,NULL,'Burundi Franc','currency.BIF'),(17,'BMD',2,NULL,NULL,'Bermudian Dollar','currency.BMD'),(18,'BND',2,NULL,'B$','Brunei Dollar','currency.BND'),(19,'BOB',2,NULL,'Bs.','Bolivian Boliviano','currency.BOB'),(20,'BRL',2,NULL,'R$','Brazilian Real','currency.BRL'),(21,'BSD',2,NULL,NULL,'Bahamian Dollar','currency.BSD'),(22,'BTN',2,NULL,NULL,'Bhutan Ngultrum','currency.BTN'),(23,'BWP',2,NULL,NULL,'Botswana Pula','currency.BWP'),(24,'BYR',0,NULL,NULL,'Belarussian Ruble','currency.BYR'),(25,'BZD',2,NULL,'BZ$','Belize Dollar','currency.BZD'),(26,'CAD',2,NULL,NULL,'Canadian Dollar','currency.CAD'),(27,'CDF',2,NULL,NULL,'Franc Congolais','currency.CDF'),(28,'CHF',2,NULL,NULL,'Swiss Franc','currency.CHF'),(29,'CLP',0,NULL,'$','Chilean Peso','currency.CLP'),(30,'CNY',2,NULL,NULL,'Chinese Yuan Renminbi','currency.CNY'),(31,'COP',2,NULL,'$','Colombian Peso','currency.COP'),(32,'CRC',2,NULL,'₡','Costa Rican Colon','currency.CRC'),(33,'CSD',2,NULL,NULL,'Serbian Dinar','currency.CSD'),(34,'CUP',2,NULL,'$MN','Cuban Peso','currency.CUP'),(35,'CVE',2,NULL,NULL,'Cape Verde Escudo','currency.CVE'),(36,'CYP',2,NULL,NULL,'Cyprus Pound','currency.CYP'),(37,'CZK',2,NULL,NULL,'Czech Koruna','currency.CZK'),(38,'DJF',0,NULL,NULL,'Djibouti Franc','currency.DJF'),(39,'DKK',2,NULL,NULL,'Danish Krone','currency.DKK'),(40,'DOP',2,NULL,'RD$','Dominican Peso','currency.DOP'),(41,'DZD',2,NULL,NULL,'Algerian Dinar','currency.DZD'),(42,'EEK',2,NULL,NULL,'Estonian Kroon','currency.EEK'),(43,'EGP',2,NULL,NULL,'Egyptian Pound','currency.EGP'),(44,'ERN',2,NULL,NULL,'Eritrea Nafka','currency.ERN'),(45,'ETB',2,NULL,NULL,'Ethiopian Birr','currency.ETB'),(46,'EUR',2,NULL,'€','Euro','currency.EUR'),(47,'FJD',2,NULL,NULL,'Fiji Dollar','currency.FJD'),(48,'FKP',2,NULL,NULL,'Falkland Islands Pound','currency.FKP'),(49,'GBP',2,NULL,NULL,'Pound Sterling','currency.GBP'),(50,'GEL',2,NULL,NULL,'Georgian Lari','currency.GEL'),(51,'GHC',2,NULL,'GHc','Ghana Cedi','currency.GHC'),(52,'GIP',2,NULL,NULL,'Gibraltar Pound','currency.GIP'),(53,'GMD',2,NULL,NULL,'Gambian Dalasi','currency.GMD'),(54,'GNF',0,NULL,NULL,'Guinea Franc','currency.GNF'),(55,'GTQ',2,NULL,'Q','Guatemala Quetzal','currency.GTQ'),(56,'GYD',2,NULL,NULL,'Guyana Dollar','currency.GYD'),(57,'HKD',2,NULL,NULL,'Hong Kong Dollar','currency.HKD'),(58,'HNL',2,NULL,'L','Honduras Lempira','currency.HNL'),(59,'HRK',2,NULL,NULL,'Croatian Kuna','currency.HRK'),(60,'HTG',2,NULL,'G','Haiti Gourde','currency.HTG'),(61,'HUF',2,NULL,NULL,'Hungarian Forint','currency.HUF'),(62,'IDR',2,NULL,NULL,'Indonesian Rupiah','currency.IDR'),(63,'ILS',2,NULL,NULL,'New Israeli Shekel','currency.ILS'),(64,'INR',2,NULL,'₹','Indian Rupee','currency.INR'),(65,'IQD',3,NULL,NULL,'Iraqi Dinar','currency.IQD'),(66,'IRR',2,NULL,NULL,'Iranian Rial','currency.IRR'),(67,'ISK',0,NULL,NULL,'Iceland Krona','currency.ISK'),(68,'JMD',2,NULL,NULL,'Jamaican Dollar','currency.JMD'),(69,'JOD',3,NULL,NULL,'Jordanian Dinar','currency.JOD'),(70,'JPY',0,NULL,NULL,'Japanese Yen','currency.JPY'),(71,'KES',2,NULL,'KSh','Kenyan Shilling','currency.KES'),(72,'KGS',2,NULL,NULL,'Kyrgyzstan Som','currency.KGS'),(73,'KHR',2,NULL,NULL,'Cambodia Riel','currency.KHR'),(74,'KMF',0,NULL,NULL,'Comoro Franc','currency.KMF'),(75,'KPW',2,NULL,NULL,'North Korean Won','currency.KPW'),(76,'KRW',0,NULL,NULL,'Korean Won','currency.KRW'),(77,'KWD',3,NULL,NULL,'Kuwaiti Dinar','currency.KWD'),(78,'KYD',2,NULL,NULL,'Cayman Islands Dollar','currency.KYD'),(79,'KZT',2,NULL,NULL,'Kazakhstan Tenge','currency.KZT'),(80,'LAK',2,NULL,NULL,'Lao Kip','currency.LAK'),(81,'LBP',2,NULL,'L£','Lebanese Pound','currency.LBP'),(82,'LKR',2,NULL,NULL,'Sri Lanka Rupee','currency.LKR'),(83,'LRD',2,NULL,NULL,'Liberian Dollar','currency.LRD'),(84,'LSL',2,NULL,NULL,'Lesotho Loti','currency.LSL'),(85,'LTL',2,NULL,NULL,'Lithuanian Litas','currency.LTL'),(86,'LVL',2,NULL,NULL,'Latvian Lats','currency.LVL'),(87,'LYD',3,NULL,NULL,'Libyan Dinar','currency.LYD'),(88,'MAD',2,NULL,NULL,'Moroccan Dirham','currency.MAD'),(89,'MDL',2,NULL,NULL,'Moldovan Leu','currency.MDL'),(90,'MGA',2,NULL,NULL,'Malagasy Ariary','currency.MGA'),(91,'MKD',2,NULL,NULL,'Macedonian Denar','currency.MKD'),(92,'MMK',2,NULL,'K','Myanmar Kyat','currency.MMK'),(93,'MNT',2,NULL,NULL,'Mongolian Tugrik','currency.MNT'),(94,'MOP',2,NULL,NULL,'Macau Pataca','currency.MOP'),(95,'MRO',2,NULL,NULL,'Mauritania Ouguiya','currency.MRO'),(96,'MTL',2,NULL,NULL,'Maltese Lira','currency.MTL'),(97,'MUR',2,NULL,NULL,'Mauritius Rupee','currency.MUR'),(98,'MVR',2,NULL,NULL,'Maldives Rufiyaa','currency.MVR'),(99,'MWK',2,NULL,NULL,'Malawi Kwacha','currency.MWK'),(100,'MXN',2,NULL,'$','Mexican Peso','currency.MXN'),(101,'MYR',2,NULL,NULL,'Malaysian Ringgit','currency.MYR'),(102,'MZM',2,NULL,NULL,'Mozambique Metical','currency.MZM'),(103,'NAD',2,NULL,NULL,'Namibia Dollar','currency.NAD'),(104,'NGN',2,NULL,NULL,'Nigerian Naira','currency.NGN'),(105,'NIO',2,NULL,'C$','Nicaragua Cordoba Oro','currency.NIO'),(106,'NOK',2,NULL,NULL,'Norwegian Krone','currency.NOK'),(107,'NPR',2,NULL,NULL,'Nepalese Rupee','currency.NPR'),(108,'NZD',2,NULL,NULL,'New Zealand Dollar','currency.NZD'),(109,'OMR',3,NULL,NULL,'Rial Omani','currency.OMR'),(110,'PAB',2,NULL,'B/.','Panama Balboa','currency.PAB'),(111,'PEN',2,NULL,'S/.','Peruvian Nuevo Sol','currency.PEN'),(112,'PGK',2,NULL,NULL,'Papua New Guinea Kina','currency.PGK'),(113,'PHP',2,NULL,NULL,'Philippine Peso','currency.PHP'),(114,'PKR',2,NULL,NULL,'Pakistan Rupee','currency.PKR'),(115,'PLN',2,NULL,NULL,'Polish Zloty','currency.PLN'),(116,'PYG',0,NULL,'₲','Paraguayan Guarani','currency.PYG'),(117,'QAR',2,NULL,NULL,'Qatari Rial','currency.QAR'),(118,'RON',2,NULL,NULL,'Romanian Leu','currency.RON'),(119,'RUB',2,NULL,NULL,'Russian Ruble','currency.RUB'),(120,'RWF',0,NULL,NULL,'Rwanda Franc','currency.RWF'),(121,'SAR',2,NULL,NULL,'Saudi Riyal','currency.SAR'),(122,'SBD',2,NULL,NULL,'Solomon Islands Dollar','currency.SBD'),(123,'SCR',2,NULL,NULL,'Seychelles Rupee','currency.SCR'),(124,'SDD',2,NULL,NULL,'Sudanese Dinar','currency.SDD'),(125,'SEK',2,NULL,NULL,'Swedish Krona','currency.SEK'),(126,'SGD',2,NULL,NULL,'Singapore Dollar','currency.SGD'),(127,'SHP',2,NULL,NULL,'St Helena Pound','currency.SHP'),(128,'SIT',2,NULL,NULL,'Slovenian Tolar','currency.SIT'),(129,'SKK',2,NULL,NULL,'Slovak Koruna','currency.SKK'),(130,'SLL',2,NULL,NULL,'Sierra Leone Leone','currency.SLL'),(131,'SOS',2,NULL,NULL,'Somali Shilling','currency.SOS'),(132,'SRD',2,NULL,NULL,'Surinam Dollar','currency.SRD'),(133,'STD',2,NULL,NULL,'Sao Tome and Principe Dobra','currency.STD'),(134,'SVC',2,NULL,NULL,'El Salvador Colon','currency.SVC'),(135,'SYP',2,NULL,NULL,'Syrian Pound','currency.SYP'),(136,'SZL',2,NULL,NULL,'Swaziland Lilangeni','currency.SZL'),(137,'THB',2,NULL,NULL,'Thai Baht','currency.THB'),(138,'TJS',2,NULL,NULL,'Tajik Somoni','currency.TJS'),(139,'TMM',2,NULL,NULL,'Turkmenistan Manat','currency.TMM'),(140,'TND',3,NULL,'DT','Tunisian Dinar','currency.TND'),(141,'TOP',2,NULL,NULL,'Tonga Pa\'anga','currency.TOP'),(142,'TRY',2,NULL,NULL,'Turkish Lira','currency.TRY'),(143,'TTD',2,NULL,NULL,'Trinidad and Tobago Dollar','currency.TTD'),(144,'TWD',2,NULL,NULL,'New Taiwan Dollar','currency.TWD'),(145,'TZS',2,NULL,NULL,'Tanzanian Shilling','currency.TZS'),(146,'UAH',2,NULL,NULL,'Ukraine Hryvnia','currency.UAH'),(147,'UGX',2,NULL,'USh','Uganda Shilling','currency.UGX'),(148,'USD',2,NULL,'$','US Dollar','currency.USD'),(149,'UYU',2,NULL,'$U','Peso Uruguayo','currency.UYU'),(150,'UZS',2,NULL,NULL,'Uzbekistan Sum','currency.UZS'),(151,'VEB',2,NULL,'Bs.F.','Venezuelan Bolivar','currency.VEB'),(152,'VND',2,NULL,NULL,'Vietnamese Dong','currency.VND'),(153,'VUV',0,NULL,NULL,'Vanuatu Vatu','currency.VUV'),(154,'WST',2,NULL,NULL,'Samoa Tala','currency.WST'),(155,'XAF',0,NULL,NULL,'CFA Franc BEAC','currency.XAF'),(156,'XCD',2,NULL,NULL,'East Caribbean Dollar','currency.XCD'),(157,'XDR',5,NULL,NULL,'SDR (Special Drawing Rights)','currency.XDR'),(158,'XOF',0,NULL,'CFA','CFA Franc BCEAO','currency.XOF'),(159,'XPF',0,NULL,NULL,'CFP Franc','currency.XPF'),(160,'YER',2,NULL,NULL,'Yemeni Rial','currency.YER'),(161,'ZAR',2,NULL,'R','South African Rand','currency.ZAR'),(162,'ZMK',2,NULL,NULL,'Zambian Kwacha','currency.ZMK'),(163,'ZWD',2,NULL,NULL,'Zimbabwe Dollar','currency.ZWD');
/*!40000 ALTER TABLE `m_currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_account_on_hold_transaction`
--

DROP TABLE IF EXISTS `m_deposit_account_on_hold_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_account_on_hold_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `transaction_type_enum` smallint(1) NOT NULL,
  `transaction_date` date NOT NULL,
  `is_reversed` tinyint(1) NOT NULL DEFAULT '0',
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_deposit_on_hold_transaction_m_savings_account` (`savings_account_id`),
  CONSTRAINT `FK_deposit_on_hold_transaction_m_savings_account` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_account_on_hold_transaction`
--

LOCK TABLES `m_deposit_account_on_hold_transaction` WRITE;
/*!40000 ALTER TABLE `m_deposit_account_on_hold_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_account_on_hold_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_account_recurring_detail`
--

DROP TABLE IF EXISTS `m_deposit_account_recurring_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_account_recurring_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL DEFAULT '0',
  `mandatory_recommended_deposit_amount` decimal(19,6) DEFAULT NULL,
  `is_mandatory` tinyint(4) NOT NULL DEFAULT '0',
  `allow_withdrawal` tinyint(4) NOT NULL DEFAULT '0',
  `adjust_advance_towards_future_payments` tinyint(4) NOT NULL DEFAULT '1',
  `is_calendar_inherited` tinyint(4) NOT NULL DEFAULT '0',
  `total_overdue_amount` decimal(19,6) DEFAULT NULL,
  `no_of_overdue_installments` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKDARD00000000000001` (`savings_account_id`),
  CONSTRAINT `FKDARD00000000000001` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_account_recurring_detail`
--

LOCK TABLES `m_deposit_account_recurring_detail` WRITE;
/*!40000 ALTER TABLE `m_deposit_account_recurring_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_account_recurring_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_account_term_and_preclosure`
--

DROP TABLE IF EXISTS `m_deposit_account_term_and_preclosure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_account_term_and_preclosure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL DEFAULT '0',
  `min_deposit_term` int(11) DEFAULT NULL,
  `max_deposit_term` int(11) DEFAULT NULL,
  `min_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `max_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `in_multiples_of_deposit_term` int(11) DEFAULT NULL,
  `in_multiples_of_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `pre_closure_penal_applicable` smallint(5) DEFAULT NULL,
  `pre_closure_penal_interest` decimal(19,6) DEFAULT NULL,
  `pre_closure_penal_interest_on_enum` smallint(5) DEFAULT NULL,
  `deposit_period` int(11) DEFAULT NULL,
  `deposit_period_frequency_enum` smallint(5) DEFAULT NULL,
  `deposit_amount` decimal(19,6) DEFAULT NULL,
  `maturity_amount` decimal(19,6) DEFAULT NULL,
  `maturity_date` date DEFAULT NULL,
  `on_account_closure_enum` smallint(5) DEFAULT NULL,
  `expected_firstdepositon_date` date DEFAULT NULL,
  `transfer_interest_to_linked_account` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FKDATP00000000000001` (`savings_account_id`),
  CONSTRAINT `FKDATP00000000000001` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_account_term_and_preclosure`
--

LOCK TABLES `m_deposit_account_term_and_preclosure` WRITE;
/*!40000 ALTER TABLE `m_deposit_account_term_and_preclosure` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_account_term_and_preclosure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_product_interest_rate_chart`
--

DROP TABLE IF EXISTS `m_deposit_product_interest_rate_chart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_product_interest_rate_chart` (
  `deposit_product_id` bigint(20) NOT NULL,
  `interest_rate_chart_id` bigint(20) NOT NULL,
  UNIQUE KEY `deposit_product_id_interest_rate_chart_id` (`deposit_product_id`,`interest_rate_chart_id`),
  KEY `FKDPIRC00000000000002` (`interest_rate_chart_id`),
  CONSTRAINT `FKDPIRC00000000000001` FOREIGN KEY (`deposit_product_id`) REFERENCES `m_savings_product` (`id`),
  CONSTRAINT `FKDPIRC00000000000002` FOREIGN KEY (`interest_rate_chart_id`) REFERENCES `m_interest_rate_chart` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_product_interest_rate_chart`
--

LOCK TABLES `m_deposit_product_interest_rate_chart` WRITE;
/*!40000 ALTER TABLE `m_deposit_product_interest_rate_chart` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_product_interest_rate_chart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_product_recurring_detail`
--

DROP TABLE IF EXISTS `m_deposit_product_recurring_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_product_recurring_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_product_id` bigint(20) NOT NULL DEFAULT '0',
  `is_mandatory` tinyint(1) NOT NULL DEFAULT '1',
  `allow_withdrawal` tinyint(1) NOT NULL DEFAULT '0',
  `adjust_advance_towards_future_payments` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FKDPRD00000000000001` (`savings_product_id`),
  CONSTRAINT `FKDPRD00000000000001` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_product_recurring_detail`
--

LOCK TABLES `m_deposit_product_recurring_detail` WRITE;
/*!40000 ALTER TABLE `m_deposit_product_recurring_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_product_recurring_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_deposit_product_term_and_preclosure`
--

DROP TABLE IF EXISTS `m_deposit_product_term_and_preclosure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_deposit_product_term_and_preclosure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_product_id` bigint(20) NOT NULL DEFAULT '0',
  `min_deposit_term` int(11) DEFAULT NULL,
  `max_deposit_term` int(11) DEFAULT NULL,
  `min_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `max_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `in_multiples_of_deposit_term` int(11) DEFAULT NULL,
  `in_multiples_of_deposit_term_type_enum` smallint(5) DEFAULT NULL,
  `pre_closure_penal_applicable` smallint(5) DEFAULT NULL,
  `pre_closure_penal_interest` decimal(19,6) DEFAULT NULL,
  `pre_closure_penal_interest_on_enum` smallint(5) DEFAULT NULL,
  `min_deposit_amount` decimal(19,6) DEFAULT NULL,
  `max_deposit_amount` decimal(19,6) DEFAULT NULL,
  `deposit_amount` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKDPTP00000000000001` (`savings_product_id`),
  CONSTRAINT `FKDPTP00000000000001` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_deposit_product_term_and_preclosure`
--

LOCK TABLES `m_deposit_product_term_and_preclosure` WRITE;
/*!40000 ALTER TABLE `m_deposit_product_term_and_preclosure` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_deposit_product_term_and_preclosure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_document`
--

DROP TABLE IF EXISTS `m_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_document` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `parent_entity_type` varchar(50) NOT NULL,
  `parent_entity_id` int(20) NOT NULL DEFAULT '0',
  `name` varchar(250) NOT NULL,
  `file_name` varchar(250) NOT NULL,
  `size` int(20) DEFAULT '0',
  `type` varchar(500) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `location` varchar(500) NOT NULL DEFAULT '0',
  `storage_type_enum` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_document`
--

LOCK TABLES `m_document` WRITE;
/*!40000 ALTER TABLE `m_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_entity_relation`
--

DROP TABLE IF EXISTS `m_entity_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_entity_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_entity_type` int(10) NOT NULL,
  `to_entity_type` int(10) NOT NULL,
  `code_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `from_entity_type_to_entity_type_code_name` (`from_entity_type`,`to_entity_type`,`code_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_entity_relation`
--

LOCK TABLES `m_entity_relation` WRITE;
/*!40000 ALTER TABLE `m_entity_relation` DISABLE KEYS */;
INSERT INTO `m_entity_relation` VALUES (1,1,2,'office_access_to_loan_products'),(2,1,3,'office_access_to_savings_products'),(3,1,4,'office_access_to_fees/charges'),(4,5,2,'role_access_to_loan_products'),(5,5,3,'role_access_to_savings_products');
/*!40000 ALTER TABLE `m_entity_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_entity_to_entity_access`
--

DROP TABLE IF EXISTS `m_entity_to_entity_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_entity_to_entity_access` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_type` varchar(50) NOT NULL,
  `entity_id` bigint(20) NOT NULL,
  `access_type_code_value_id` int(11) NOT NULL,
  `second_entity_type` varchar(50) NOT NULL,
  `second_entity_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_uniq_m_entity_to_entity_access` (`entity_type`,`entity_id`,`access_type_code_value_id`,`second_entity_type`,`second_entity_id`),
  KEY `IDX_OFFICE` (`entity_type`,`entity_id`),
  KEY `FK_access_type_code_m_code_value` (`access_type_code_value_id`),
  CONSTRAINT `FK_access_type_code_m_code_value` FOREIGN KEY (`access_type_code_value_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_entity_to_entity_access`
--

LOCK TABLES `m_entity_to_entity_access` WRITE;
/*!40000 ALTER TABLE `m_entity_to_entity_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_entity_to_entity_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_entity_to_entity_mapping`
--

DROP TABLE IF EXISTS `m_entity_to_entity_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_entity_to_entity_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rel_id` bigint(20) NOT NULL DEFAULT '0',
  `from_id` bigint(20) NOT NULL DEFAULT '0',
  `to_id` bigint(20) unsigned NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rel_id_from_id_to_id` (`rel_id`,`from_id`,`to_id`),
  CONSTRAINT `FK__rel_id_m_entity_relation_id` FOREIGN KEY (`rel_id`) REFERENCES `m_entity_relation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_entity_to_entity_mapping`
--

LOCK TABLES `m_entity_to_entity_mapping` WRITE;
/*!40000 ALTER TABLE `m_entity_to_entity_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_entity_to_entity_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_fund`
--

DROP TABLE IF EXISTS `m_fund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_fund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fund_name_org` (`name`),
  UNIQUE KEY `fund_externalid_org` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_fund`
--

LOCK TABLES `m_fund` WRITE;
/*!40000 ALTER TABLE `m_fund` DISABLE KEYS */;
INSERT INTO `m_fund` VALUES (1, 'TW', NULL),
(2, 'Gold', NULL),
(3, 'MSME', NULL);
/*!40000 ALTER TABLE `m_fund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_group`
--

DROP TABLE IF EXISTS `m_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_id` varchar(100) DEFAULT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '300',
  `activation_date` date DEFAULT NULL,
  `office_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `level_id` int(11) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `hierarchy` varchar(100) DEFAULT NULL,
  `closure_reason_cv_id` int(11) DEFAULT NULL,
  `closedon_date` date DEFAULT NULL,
  `activatedon_userid` bigint(20) DEFAULT NULL,
  `submittedon_date` date DEFAULT NULL,
  `submittedon_userid` bigint(20) DEFAULT NULL,
  `closedon_userid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`display_name`,`level_id`),
  UNIQUE KEY `external_id` (`external_id`,`level_id`),
  UNIQUE KEY `external_id_UNIQUE` (`external_id`),
  KEY `office_id` (`office_id`),
  KEY `staff_id` (`staff_id`),
  KEY `Parent_Id_reference` (`parent_id`),
  KEY `FK_m_group_level` (`level_id`),
  KEY `FK_m_group_m_code` (`closure_reason_cv_id`),
  CONSTRAINT `FK_m_group_level` FOREIGN KEY (`level_id`) REFERENCES `m_group_level` (`id`),
  CONSTRAINT `FK_m_group_m_code` FOREIGN KEY (`closure_reason_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_group_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `Parent_Id_reference` FOREIGN KEY (`parent_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `m_group_ibfk_1` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_group`
--

LOCK TABLES `m_group` WRITE;
/*!40000 ALTER TABLE `m_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_group_client`
--

DROP TABLE IF EXISTS `m_group_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_group_client` (
  `group_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  PRIMARY KEY (`group_id`,`client_id`),
  KEY `client_id` (`client_id`),
  CONSTRAINT `m_group_client_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `m_group_client_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_group_client`
--

LOCK TABLES `m_group_client` WRITE;
/*!40000 ALTER TABLE `m_group_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_group_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_group_level`
--

DROP TABLE IF EXISTS `m_group_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_group_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `super_parent` tinyint(1) NOT NULL,
  `level_name` varchar(100) NOT NULL,
  `recursable` tinyint(1) NOT NULL,
  `can_have_clients` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Parent_levelId_reference` (`parent_id`),
  CONSTRAINT `Parent_levelId_reference` FOREIGN KEY (`parent_id`) REFERENCES `m_group_level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_group_level`
--

LOCK TABLES `m_group_level` WRITE;
/*!40000 ALTER TABLE `m_group_level` DISABLE KEYS */;
INSERT INTO `m_group_level` VALUES (1,NULL,1,'Center',1,0),(2,1,0,'Group',0,1);
/*!40000 ALTER TABLE `m_group_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_group_roles`
--

DROP TABLE IF EXISTS `m_group_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_group_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `role_cv_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_GROUP_ROLES` (`client_id`,`group_id`,`role_cv_id`),
  KEY `FKGroupRoleClientId` (`client_id`),
  KEY `FKGroupRoleGroupId` (`group_id`),
  KEY `FK_grouprole_m_codevalue` (`role_cv_id`),
  CONSTRAINT `FKGroupRoleClientId` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKGroupRoleGroupId` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `FK_grouprole_m_codevalue` FOREIGN KEY (`role_cv_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_group_roles`
--

LOCK TABLES `m_group_roles` WRITE;
/*!40000 ALTER TABLE `m_group_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_group_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_guarantor`
--

DROP TABLE IF EXISTS `m_guarantor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_guarantor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `client_reln_cv_id` int(11) DEFAULT NULL,
  `type_enum` smallint(5) NOT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `lastname` varchar(50) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `address_line_1` varchar(500) DEFAULT NULL,
  `address_line_2` varchar(500) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `zip` varchar(20) DEFAULT NULL,
  `house_phone_number` varchar(20) DEFAULT NULL,
  `mobile_number` varchar(20) DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `gnbc_type_cv_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_guarantor_m_loan` (`loan_id`),
  KEY `FK_m_guarantor_m_code_value` (`client_reln_cv_id`),
  CONSTRAINT `FK_m_guarantor_m_code_value` FOREIGN KEY (`client_reln_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_m_guarantor_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_guarantor`
--

LOCK TABLES `m_guarantor` WRITE;
/*!40000 ALTER TABLE `m_guarantor` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_guarantor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_guarantor_funding_details`
--

DROP TABLE IF EXISTS `m_guarantor_funding_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_guarantor_funding_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guarantor_id` bigint(20) NOT NULL,
  `account_associations_id` bigint(20) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `amount_released_derived` decimal(19,6) DEFAULT NULL,
  `amount_remaining_derived` decimal(19,6) DEFAULT NULL,
  `amount_transfered_derived` decimal(19,6) DEFAULT NULL,
  `status_enum` smallint(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_guarantor_fund_details_m_guarantor` (`guarantor_id`),
  KEY `FK_m_guarantor_fund_details_account_associations_id` (`account_associations_id`),
  CONSTRAINT `FK_m_guarantor_fund_details_account_associations_id` FOREIGN KEY (`account_associations_id`) REFERENCES `m_portfolio_account_associations` (`id`),
  CONSTRAINT `FK_m_guarantor_fund_details_m_guarantor` FOREIGN KEY (`guarantor_id`) REFERENCES `m_guarantor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_guarantor_funding_details`
--

LOCK TABLES `m_guarantor_funding_details` WRITE;
/*!40000 ALTER TABLE `m_guarantor_funding_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_guarantor_funding_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_guarantor_transaction`
--

DROP TABLE IF EXISTS `m_guarantor_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_guarantor_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guarantor_fund_detail_id` bigint(20) NOT NULL,
  `loan_transaction_id` bigint(20) DEFAULT NULL,
  `deposit_on_hold_transaction_id` bigint(20) NOT NULL,
  `is_reversed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_guarantor_transaction_m_deposit_account_on_hold_transaction` (`deposit_on_hold_transaction_id`),
  KEY `FK_guarantor_transaction_guarantor_fund_detail` (`guarantor_fund_detail_id`),
  KEY `FK_guarantor_transaction_m_loan_transaction` (`loan_transaction_id`),
  CONSTRAINT `FK_guarantor_transaction_guarantor_fund_detail` FOREIGN KEY (`guarantor_fund_detail_id`) REFERENCES `m_guarantor_funding_details` (`id`),
  CONSTRAINT `FK_guarantor_transaction_m_deposit_account_on_hold_transaction` FOREIGN KEY (`deposit_on_hold_transaction_id`) REFERENCES `m_deposit_account_on_hold_transaction` (`id`),
  CONSTRAINT `FK_guarantor_transaction_m_loan_transaction` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_guarantor_transaction`
--

LOCK TABLES `m_guarantor_transaction` WRITE;
/*!40000 ALTER TABLE `m_guarantor_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_guarantor_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_holiday`
--

DROP TABLE IF EXISTS `m_holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_holiday` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `from_date` datetime NOT NULL,
  `to_date` datetime NOT NULL,
  `repayments_rescheduled_to` datetime NOT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '100',
  `processed` tinyint(1) NOT NULL DEFAULT '0',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `holiday_name` (`name`,`from_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_holiday`
--

LOCK TABLES `m_holiday` WRITE;
/*!40000 ALTER TABLE `m_holiday` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_holiday` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_holiday_office`
--

DROP TABLE IF EXISTS `m_holiday_office`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_holiday_office` (
  `holiday_id` bigint(20) NOT NULL,
  `office_id` bigint(20) NOT NULL,
  PRIMARY KEY (`holiday_id`,`office_id`),
  KEY `m_holiday_id_ibfk_1` (`holiday_id`),
  KEY `m_office_id_ibfk_2` (`office_id`),
  CONSTRAINT `m_holiday_id_ibfk_1` FOREIGN KEY (`holiday_id`) REFERENCES `m_holiday` (`id`),
  CONSTRAINT `m_office_id_ibfk_2` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_holiday_office`
--

LOCK TABLES `m_holiday_office` WRITE;
/*!40000 ALTER TABLE `m_holiday_office` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_holiday_office` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_hook`
--

DROP TABLE IF EXISTS `m_hook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_hook` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` smallint(6) NOT NULL,
  `is_active` smallint(3) NOT NULL DEFAULT '1',
  `name` varchar(45) NOT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `ugd_template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_template_id_idx` (`template_id`),
  KEY `fk_ugd_template_id` (`ugd_template_id`),
  CONSTRAINT `fk_template_id` FOREIGN KEY (`template_id`) REFERENCES `m_hook_templates` (`id`),
  CONSTRAINT `fk_ugd_template_id` FOREIGN KEY (`ugd_template_id`) REFERENCES `m_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_hook`
--

LOCK TABLES `m_hook` WRITE;
/*!40000 ALTER TABLE `m_hook` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_hook` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_hook_configuration`
--

DROP TABLE IF EXISTS `m_hook_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_hook_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hook_id` bigint(20) DEFAULT NULL,
  `field_type` varchar(45) NOT NULL,
  `field_name` varchar(100) NOT NULL,
  `field_value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_hook_id_idx` (`hook_id`),
  CONSTRAINT `fk_hook_id_cfg` FOREIGN KEY (`hook_id`) REFERENCES `m_hook` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_hook_configuration`
--

LOCK TABLES `m_hook_configuration` WRITE;
/*!40000 ALTER TABLE `m_hook_configuration` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_hook_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_hook_registered_events`
--

DROP TABLE IF EXISTS `m_hook_registered_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_hook_registered_events` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hook_id` bigint(20) NOT NULL,
  `entity_name` varchar(45) NOT NULL,
  `action_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_hook_id_idx` (`hook_id`),
  CONSTRAINT `fk_hook_idc` FOREIGN KEY (`hook_id`) REFERENCES `m_hook` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_hook_registered_events`
--

LOCK TABLES `m_hook_registered_events` WRITE;
/*!40000 ALTER TABLE `m_hook_registered_events` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_hook_registered_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_hook_schema`
--

DROP TABLE IF EXISTS `m_hook_schema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_hook_schema` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `hook_template_id` smallint(6) NOT NULL,
  `field_type` varchar(45) NOT NULL,
  `field_name` varchar(100) NOT NULL,
  `placeholder` varchar(100) DEFAULT NULL,
  `optional` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_hook_template_id_idx` (`hook_template_id`),
  CONSTRAINT `fk_hook_template_id` FOREIGN KEY (`hook_template_id`) REFERENCES `m_hook_templates` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_hook_schema`
--

LOCK TABLES `m_hook_schema` WRITE;
/*!40000 ALTER TABLE `m_hook_schema` DISABLE KEYS */;
INSERT INTO `m_hook_schema` VALUES (1,1,'string','Payload URL',NULL,0),(2,1,'string','Content Type','json / form',0),(3,2,'string','Payload URL',NULL,0),(4,2,'string','SMS Provider',NULL,0),(5,2,'string','Phone Number',NULL,0),(6,2,'string','SMS Provider Token',NULL,0),(7,2,'string','SMS Provider Account Id',NULL,0);
/*!40000 ALTER TABLE `m_hook_schema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_hook_templates`
--

DROP TABLE IF EXISTS `m_hook_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_hook_templates` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_hook_templates`
--

LOCK TABLES `m_hook_templates` WRITE;
/*!40000 ALTER TABLE `m_hook_templates` DISABLE KEYS */;
INSERT INTO `m_hook_templates` VALUES (1,'Web'),(2,'SMS Bridge');
/*!40000 ALTER TABLE `m_hook_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_image`
--

DROP TABLE IF EXISTS `m_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location` varchar(500) DEFAULT NULL,
  `storage_type_enum` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_image`
--

LOCK TABLES `m_image` WRITE;
/*!40000 ALTER TABLE `m_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_interest_incentives`
--

DROP TABLE IF EXISTS `m_interest_incentives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_interest_incentives` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `interest_rate_slab_id` bigint(20) NOT NULL,
  `entiry_type` smallint(2) NOT NULL,
  `attribute_name` smallint(2) NOT NULL,
  `condition_type` smallint(2) NOT NULL,
  `attribute_value` varchar(50) NOT NULL,
  `incentive_type` smallint(2) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_interest_incentives_m_interest_rate_slab` (`interest_rate_slab_id`),
  CONSTRAINT `FK_m_interest_incentives_m_interest_rate_slab` FOREIGN KEY (`interest_rate_slab_id`) REFERENCES `m_interest_rate_slab` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_interest_incentives`
--

LOCK TABLES `m_interest_incentives` WRITE;
/*!40000 ALTER TABLE `m_interest_incentives` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_interest_incentives` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_interest_rate_chart`
--

DROP TABLE IF EXISTS `m_interest_rate_chart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_interest_rate_chart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `from_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_interest_rate_chart`
--

LOCK TABLES `m_interest_rate_chart` WRITE;
/*!40000 ALTER TABLE `m_interest_rate_chart` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_interest_rate_chart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_interest_rate_slab`
--

DROP TABLE IF EXISTS `m_interest_rate_slab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_interest_rate_slab` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `interest_rate_chart_id` bigint(20) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `period_type_enum` smallint(5) NOT NULL DEFAULT '1',
  `from_period` int(11) NOT NULL DEFAULT '0',
  `to_period` int(11) DEFAULT NULL,
  `amount_range_from` decimal(19,6) DEFAULT NULL,
  `amount_range_to` decimal(19,6) DEFAULT NULL,
  `annual_interest_rate` decimal(19,6) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKIRS00000000000001` (`interest_rate_chart_id`),
  CONSTRAINT `FKIRS00000000000001` FOREIGN KEY (`interest_rate_chart_id`) REFERENCES `m_interest_rate_chart` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_interest_rate_slab`
--

LOCK TABLES `m_interest_rate_slab` WRITE;
/*!40000 ALTER TABLE `m_interest_rate_slab` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_interest_rate_slab` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan`
--

DROP TABLE IF EXISTS `m_loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(20) NOT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `fund_id` bigint(20) DEFAULT NULL,
  `loan_officer_id` bigint(20) DEFAULT NULL,
  `dsa_officer_id` bigint(20) DEFAULT NULL,
  `loanpurpose_cv_id` int(11) DEFAULT NULL,
  `loan_status_id` smallint(5) NOT NULL,
  `loan_type_enum` smallint(5) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `principal_amount_proposed` decimal(19,6) NOT NULL,
  `principal_amount` decimal(19,6) NOT NULL,
  `approved_principal` decimal(19,6) NOT NULL,
  `arrearstolerance_amount` decimal(19,6) DEFAULT NULL,
  `nominal_interest_rate_per_period` decimal(19,6) NOT NULL,
  `interest_period_frequency_enum` smallint(5) NOT NULL,
  `annual_nominal_interest_rate` decimal(19,6) NOT NULL,
  `interest_method_enum` smallint(5) NOT NULL,
  `interest_calculated_in_period_enum` smallint(5) NOT NULL DEFAULT '1',
  `term_frequency` smallint(5) NOT NULL DEFAULT '0',
  `term_period_frequency_enum` smallint(5) NOT NULL DEFAULT '2',
  `repay_every` smallint(5) NOT NULL,
  `repayment_period_frequency_enum` smallint(5) NOT NULL,
  `repayment_frequency_nth_day_enum` smallint(5) DEFAULT '0',
  `repayment_frequency_day_of_week_enum` smallint(5) DEFAULT '0',
  `number_of_repayments` smallint(5) NOT NULL,
  `grace_on_principal_periods` smallint(5) DEFAULT NULL,
  `grace_on_interest_periods` smallint(5) DEFAULT NULL,
  `grace_interest_free_periods` smallint(5) DEFAULT NULL,
  `amortization_method_enum` smallint(5) NOT NULL,
  `submittedon_date` date DEFAULT NULL,
  `submittedon_userid` bigint(20) DEFAULT NULL,
  `approvedon_date` date DEFAULT NULL,
  `approvedon_userid` bigint(20) DEFAULT NULL,
  `expected_disbursedon_date` date DEFAULT NULL,
  `expected_firstrepaymenton_date` date DEFAULT NULL,
  `interest_calculated_from_date` date DEFAULT NULL,
  `disbursedon_date` date DEFAULT NULL,
  `disbursedon_userid` bigint(20) DEFAULT NULL,
  `expected_maturedon_date` date DEFAULT NULL,
  `maturedon_date` date DEFAULT NULL,
  `closedon_date` date DEFAULT NULL,
  `closedon_userid` bigint(20) DEFAULT NULL,
  `total_charges_due_at_disbursement_derived` decimal(19,6) DEFAULT NULL,
  `principal_disbursed_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `principal_repaid_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `principal_writtenoff_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `principal_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_charged_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_repaid_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_waived_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_writtenoff_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_charged_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_repaid_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_waived_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_writtenoff_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_charged_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_repaid_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_waived_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_writtenoff_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_expected_repayment_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_repayment_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_expected_costofloan_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_costofloan_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_waived_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_writtenoff_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_overpaid_derived` decimal(19,6) DEFAULT NULL,
  `rejectedon_date` date DEFAULT NULL,
  `rejectedon_userid` bigint(20) DEFAULT NULL,
  `rescheduledon_date` date DEFAULT NULL,
  `rescheduledon_userid` bigint(20) DEFAULT NULL,
  `withdrawnon_date` date DEFAULT NULL,
  `withdrawnon_userid` bigint(20) DEFAULT NULL,
  `writtenoffon_date` date DEFAULT NULL,
  `loan_transaction_strategy_id` bigint(20) DEFAULT NULL,
  `sync_disbursement_with_meeting` tinyint(1) DEFAULT NULL,
  `loan_counter` smallint(6) DEFAULT NULL,
  `loan_product_counter` smallint(6) DEFAULT NULL,
  `fixed_emi_amount` decimal(19,6) DEFAULT NULL,
  `max_outstanding_loan_balance` decimal(19,6) DEFAULT NULL,
  `grace_on_arrears_ageing` smallint(5) DEFAULT NULL,
  `is_npa` tinyint(1) NOT NULL DEFAULT '0',
  `total_recovered_derived` decimal(19,6) DEFAULT NULL,
  `accrued_till` date DEFAULT NULL,
  `days_in_month_enum` smallint(5) NOT NULL DEFAULT '1',
  `days_in_year_enum` smallint(5) NOT NULL DEFAULT '1',
  `interest_recalculation_enabled` tinyint(4) NOT NULL DEFAULT '0',
  `guarantee_amount_derived` decimal(19,6) DEFAULT NULL,
  `create_standing_instruction_at_disbursement` tinyint(1) DEFAULT NULL,
  `version` int(15) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loan_account_no_UNIQUE` (`account_no`),
  UNIQUE KEY `loan_externalid_UNIQUE` (`external_id`),
  KEY `FKB6F935D87179A0CB` (`client_id`),
  KEY `FKB6F935D8C8D4B434` (`product_id`),
  KEY `FK7C885877240145` (`fund_id`),
  KEY `FK_loan_ltp_strategy` (`loan_transaction_strategy_id`),
  KEY `FK_m_loan_m_staff` (`loan_officer_id`),
  KEY `group_id` (`group_id`),
  KEY `FK_m_loanpurpose_codevalue` (`loanpurpose_cv_id`),
  KEY `FK_submittedon_userid` (`submittedon_userid`),
  KEY `FK_approvedon_userid` (`approvedon_userid`),
  KEY `FK_rejectedon_userid` (`rejectedon_userid`),
  KEY `FK_withdrawnon_userid` (`withdrawnon_userid`),
  KEY `FK_disbursedon_userid` (`disbursedon_userid`),
  KEY `FK_closedon_userid` (`closedon_userid`),
  KEY `fk_m_group_client_001_idx` (`group_id`,`client_id`),
  CONSTRAINT `FK7C885877240145` FOREIGN KEY (`fund_id`) REFERENCES `m_fund` (`id`),
  CONSTRAINT `FKB6F935D87179A0CB` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKB6F935D8C8D4B434` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`),
  CONSTRAINT `FK_approvedon_userid` FOREIGN KEY (`approvedon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_closedon_userid` FOREIGN KEY (`closedon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_disbursedon_userid` FOREIGN KEY (`disbursedon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_loan_ltp_strategy` FOREIGN KEY (`loan_transaction_strategy_id`) REFERENCES `ref_loan_transaction_processing_strategy` (`id`),
  CONSTRAINT `FK_m_loan_m_staff` FOREIGN KEY (`loan_officer_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `FK_m_loanpurpose_codevalue` FOREIGN KEY (`loanpurpose_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_rejectedon_userid` FOREIGN KEY (`rejectedon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_submittedon_userid` FOREIGN KEY (`submittedon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_withdrawnon_userid` FOREIGN KEY (`withdrawnon_userid`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `m_loan_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan`
--

LOCK TABLES `m_loan` WRITE;
/*!40000 ALTER TABLE `m_loan` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_arrears_aging`
--

DROP TABLE IF EXISTS `m_loan_arrears_aging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_arrears_aging` (
  `loan_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `principal_overdue_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_overdue_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_overdue_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_overdue_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_overdue_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `overdue_since_date_derived` date DEFAULT NULL,
  PRIMARY KEY (`loan_id`),
  CONSTRAINT `m_loan_arrears_aging_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_arrears_aging`
--

LOCK TABLES `m_loan_arrears_aging` WRITE;
/*!40000 ALTER TABLE `m_loan_arrears_aging` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_arrears_aging` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_charge`
--

DROP TABLE IF EXISTS `m_loan_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `charge_id` bigint(20) NOT NULL,
  `is_penalty` tinyint(1) NOT NULL DEFAULT '0',
  `charge_time_enum` smallint(5) NOT NULL,
  `due_for_collection_as_of_date` date DEFAULT NULL,
  `charge_calculation_enum` smallint(5) NOT NULL,
  `charge_payment_mode_enum` smallint(5) NOT NULL DEFAULT '0',
  `calculation_percentage` decimal(19,6) DEFAULT NULL,
  `calculation_on_amount` decimal(19,6) DEFAULT NULL,
  `charge_amount_or_percentage` decimal(19,6) DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `amount_paid_derived` decimal(19,6) DEFAULT NULL,
  `amount_waived_derived` decimal(19,6) DEFAULT NULL,
  `amount_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `amount_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `is_paid_derived` tinyint(1) NOT NULL DEFAULT '0',
  `waived` tinyint(1) NOT NULL DEFAULT '0',
  `min_cap` decimal(19,6) DEFAULT NULL,
  `max_cap` decimal(19,6) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `charge_id` (`charge_id`),
  KEY `m_loan_charge_ibfk_2` (`loan_id`),
  CONSTRAINT `m_loan_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  CONSTRAINT `m_loan_charge_ibfk_2` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_charge`
--

LOCK TABLES `m_loan_charge` WRITE;
/*!40000 ALTER TABLE `m_loan_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_charge_paid_by`
--

DROP TABLE IF EXISTS `m_loan_charge_paid_by`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_charge_paid_by` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_transaction_id` bigint(20) NOT NULL,
  `loan_charge_id` bigint(20) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `installment_number` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK__m_loan_transaction` (`loan_transaction_id`),
  KEY `FK__m_loan_charge` (`loan_charge_id`),
  CONSTRAINT `FK__m_loan_charge` FOREIGN KEY (`loan_charge_id`) REFERENCES `m_loan_charge` (`id`),
  CONSTRAINT `FK__m_loan_transaction` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_charge_paid_by`
--

LOCK TABLES `m_loan_charge_paid_by` WRITE;
/*!40000 ALTER TABLE `m_loan_charge_paid_by` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_charge_paid_by` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_collateral`
--

DROP TABLE IF EXISTS `m_loan_collateral`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_collateral` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `type_cv_id` int(11) NOT NULL,
  `value` decimal(19,6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `gold_fine_cv_id` int(20) DEFAULT NULL,
  `jewellery_cv_id` int(20) DEFAULT NULL,
  `actualcost` decimal(19,2) DEFAULT NULL,
  `gross` decimal(19,2) DEFAULT NULL,
  `impurity` decimal(19,2) DEFAULT NULL,
  `net` decimal(19,2) DEFAULT NULL,
  `stone` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_collateral_m_loan` (`loan_id`),
  KEY `FK_collateral_code_value` (`type_cv_id`),
  CONSTRAINT `FK_collateral_code_value` FOREIGN KEY (`type_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK_collateral_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_collateral`
--

LOCK TABLES `m_loan_collateral` WRITE;
/*!40000 ALTER TABLE `m_loan_collateral` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_collateral` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_disbursement_detail`
--

DROP TABLE IF EXISTS `m_loan_disbursement_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_disbursement_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `expected_disburse_date` datetime NOT NULL,
  `disbursedon_date` datetime DEFAULT NULL,
  `principal` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_loan_disbursement_detail_loan_id` (`loan_id`),
  CONSTRAINT `FK_loan_disbursement_detail_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_disbursement_detail`
--

LOCK TABLES `m_loan_disbursement_detail` WRITE;
/*!40000 ALTER TABLE `m_loan_disbursement_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_disbursement_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_installment_charge`
--

DROP TABLE IF EXISTS `m_loan_installment_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_installment_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_charge_id` bigint(20) NOT NULL,
  `loan_schedule_id` bigint(20) NOT NULL,
  `due_date` date DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `amount_paid_derived` decimal(19,6) DEFAULT NULL,
  `amount_waived_derived` decimal(19,6) DEFAULT NULL,
  `amount_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `amount_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `is_paid_derived` tinyint(1) NOT NULL DEFAULT '0',
  `waived` tinyint(1) NOT NULL DEFAULT '0',
  `amount_through_charge_payment` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_loan_charge_id_charge_schedule` (`loan_charge_id`),
  KEY `FK_loan_schedule_id_charge_schedule` (`loan_schedule_id`),
  CONSTRAINT `FK_loan_charge_id_charge_schedule` FOREIGN KEY (`loan_charge_id`) REFERENCES `m_loan_charge` (`id`),
  CONSTRAINT `FK_loan_schedule_id_charge_schedule` FOREIGN KEY (`loan_schedule_id`) REFERENCES `m_loan_repayment_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_installment_charge`
--

LOCK TABLES `m_loan_installment_charge` WRITE;
/*!40000 ALTER TABLE `m_loan_installment_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_installment_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_officer_assignment_history`
--

DROP TABLE IF EXISTS `m_loan_officer_assignment_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_officer_assignment_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `loan_officer_id` bigint(20) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_m_loan_officer_assignment_history_0001` (`loan_id`),
  KEY `fk_m_loan_officer_assignment_history_0002` (`loan_officer_id`),
  CONSTRAINT `fk_m_loan_officer_assignment_history_0001` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `fk_m_loan_officer_assignment_history_0002` FOREIGN KEY (`loan_officer_id`) REFERENCES `m_staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_officer_assignment_history`
--

LOCK TABLES `m_loan_officer_assignment_history` WRITE;
/*!40000 ALTER TABLE `m_loan_officer_assignment_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_officer_assignment_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_overdue_installment_charge`
--

DROP TABLE IF EXISTS `m_loan_overdue_installment_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_overdue_installment_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_charge_id` bigint(20) NOT NULL,
  `loan_schedule_id` bigint(20) NOT NULL,
  `frequency_number` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_loan_overdue_installment_charge_m_loan_charge` (`loan_charge_id`),
  KEY `FK_m_loan_overdue_installment_charge_m_loan_repayment_schedule` (`loan_schedule_id`),
  CONSTRAINT `FK_m_loan_overdue_installment_charge_m_loan_charge` FOREIGN KEY (`loan_charge_id`) REFERENCES `m_loan_charge` (`id`),
  CONSTRAINT `FK_m_loan_overdue_installment_charge_m_loan_repayment_schedule` FOREIGN KEY (`loan_schedule_id`) REFERENCES `m_loan_repayment_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_overdue_installment_charge`
--

LOCK TABLES `m_loan_overdue_installment_charge` WRITE;
/*!40000 ALTER TABLE `m_loan_overdue_installment_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_overdue_installment_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_paid_in_advance`
--

DROP TABLE IF EXISTS `m_loan_paid_in_advance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_paid_in_advance` (
  `loan_id` bigint(20) NOT NULL,
  `principal_in_advance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `interest_in_advance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `fee_charges_in_advance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `penalty_charges_in_advance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `total_in_advance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  PRIMARY KEY (`loan_id`),
  CONSTRAINT `m_loan_paid_in_advance_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_paid_in_advance`
--

LOCK TABLES `m_loan_paid_in_advance` WRITE;
/*!40000 ALTER TABLE `m_loan_paid_in_advance` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_paid_in_advance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_recalculation_details`
--

DROP TABLE IF EXISTS `m_loan_recalculation_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_recalculation_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `compound_type_enum` smallint(5) NOT NULL,
  `reschedule_strategy_enum` smallint(5) NOT NULL,
  `rest_frequency_type_enum` smallint(1) NOT NULL,
  `rest_frequency_interval` smallint(3) NOT NULL DEFAULT '0',
  `rest_freqency_date` date DEFAULT NULL,
  `compounding_frequency_type_enum` smallint(1) DEFAULT NULL,
  `compounding_frequency_interval` smallint(3) DEFAULT NULL,
  `compounding_freqency_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_loan_m_loan_recalculation_details` (`loan_id`),
  CONSTRAINT `FK_m_loan_m_loan_recalculation_details` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_recalculation_details`
--

LOCK TABLES `m_loan_recalculation_details` WRITE;
/*!40000 ALTER TABLE `m_loan_recalculation_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_recalculation_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_repayment_schedule`
--

DROP TABLE IF EXISTS `m_loan_repayment_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_repayment_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `fromdate` date DEFAULT NULL,
  `duedate` date NOT NULL,
  `installment` smallint(5) NOT NULL,
  `principal_amount` decimal(19,6) DEFAULT NULL,
  `principal_completed_derived` decimal(19,6) DEFAULT NULL,
  `principal_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `interest_amount` decimal(19,6) DEFAULT NULL,
  `interest_completed_derived` decimal(19,6) DEFAULT NULL,
  `interest_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `interest_waived_derived` decimal(19,6) DEFAULT NULL,
  `accrual_interest_derived` decimal(19,6) DEFAULT NULL,
  `fee_charges_amount` decimal(19,6) DEFAULT NULL,
  `fee_charges_completed_derived` decimal(19,6) DEFAULT NULL,
  `fee_charges_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `fee_charges_waived_derived` decimal(19,6) DEFAULT NULL,
  `accrual_fee_charges_derived` decimal(19,6) DEFAULT NULL,
  `penalty_charges_amount` decimal(19,6) DEFAULT NULL,
  `penalty_charges_completed_derived` decimal(19,6) DEFAULT NULL,
  `penalty_charges_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `penalty_charges_waived_derived` decimal(19,6) DEFAULT NULL,
  `accrual_penalty_charges_derived` decimal(19,6) DEFAULT NULL,
  `total_paid_in_advance_derived` decimal(19,6) DEFAULT NULL,
  `total_paid_late_derived` decimal(19,6) DEFAULT NULL,
  `completed_derived` bit(1) NOT NULL,
  `obligations_met_on_date` date DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `recalculated_interest_component` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK488B92AA40BE0710` (`loan_id`),
  CONSTRAINT `FK488B92AA40BE0710` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_repayment_schedule`
--

LOCK TABLES `m_loan_repayment_schedule` WRITE;
/*!40000 ALTER TABLE `m_loan_repayment_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_repayment_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_repayment_schedule_history`
--

DROP TABLE IF EXISTS `m_loan_repayment_schedule_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_repayment_schedule_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `loan_reschedule_request_id` bigint(20) DEFAULT NULL,
  `fromdate` date DEFAULT NULL,
  `duedate` date NOT NULL,
  `installment` smallint(5) NOT NULL,
  `principal_amount` decimal(19,6) DEFAULT NULL,
  `interest_amount` decimal(19,6) DEFAULT NULL,
  `fee_charges_amount` decimal(19,6) DEFAULT NULL,
  `penalty_charges_amount` decimal(19,6) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `version` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `loan_id` (`loan_id`),
  KEY `loan_reschedule_request_id` (`loan_reschedule_request_id`),
  CONSTRAINT `m_loan_repayment_schedule_history_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `m_loan_repayment_schedule_history_ibfk_2` FOREIGN KEY (`loan_reschedule_request_id`) REFERENCES `m_loan_reschedule_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_repayment_schedule_history`
--

LOCK TABLES `m_loan_repayment_schedule_history` WRITE;
/*!40000 ALTER TABLE `m_loan_repayment_schedule_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_repayment_schedule_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_reschedule_request`
--

DROP TABLE IF EXISTS `m_loan_reschedule_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_reschedule_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `status_enum` smallint(5) NOT NULL,
  `reschedule_from_installment` smallint(5) NOT NULL COMMENT 'Rescheduling will start from this installment',
  `grace_on_principal` smallint(5) DEFAULT NULL COMMENT 'Number of installments that should be added with 0 principal amount',
  `grace_on_interest` smallint(5) DEFAULT NULL COMMENT 'Number of installments that should be added with 0 interest rate',
  `reschedule_from_date` date NOT NULL COMMENT 'Rescheduling will start from the installment with due date similar to this date.',
  `extra_terms` smallint(5) DEFAULT NULL COMMENT 'Number of extra terms to be added to the schedule',
  `interest_rate` decimal(19,6) DEFAULT NULL COMMENT 'If provided, the interest rate for the unpaid installments will be recalculated',
  `recalculate_interest` tinyint(1) DEFAULT NULL COMMENT 'If set to 1, interest will be recalculated starting from the reschedule period.',
  `adjusted_due_date` date DEFAULT NULL COMMENT 'New due date for the first rescheduled installment',
  `reschedule_reason_cv_id` int(11) DEFAULT NULL COMMENT 'ID of code value of reason for rescheduling',
  `reschedule_reason_comment` varchar(500) DEFAULT NULL COMMENT 'Text provided in addition to the reason code value',
  `submitted_on_date` date NOT NULL,
  `submitted_by_user_id` bigint(20) NOT NULL,
  `approved_on_date` date DEFAULT NULL,
  `approved_by_user_id` bigint(20) DEFAULT NULL,
  `rejected_on_date` date DEFAULT NULL,
  `rejected_by_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `loan_id` (`loan_id`),
  KEY `reschedule_reason_cv_id` (`reschedule_reason_cv_id`),
  KEY `submitted_by_user_id` (`submitted_by_user_id`),
  KEY `approved_by_user_id` (`approved_by_user_id`),
  KEY `rejected_by_user_id` (`rejected_by_user_id`),
  CONSTRAINT `m_loan_reschedule_request_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `m_loan_reschedule_request_ibfk_2` FOREIGN KEY (`reschedule_reason_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `m_loan_reschedule_request_ibfk_3` FOREIGN KEY (`submitted_by_user_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `m_loan_reschedule_request_ibfk_4` FOREIGN KEY (`approved_by_user_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `m_loan_reschedule_request_ibfk_5` FOREIGN KEY (`rejected_by_user_id`) REFERENCES `m_appuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_reschedule_request`
--

LOCK TABLES `m_loan_reschedule_request` WRITE;
/*!40000 ALTER TABLE `m_loan_reschedule_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_reschedule_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_term_variations`
--

DROP TABLE IF EXISTS `m_loan_term_variations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_term_variations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `term_type` smallint(2) NOT NULL,
  `applicable_from` date NOT NULL,
  `term_value` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_loan_id_m_loan_id` (`loan_id`),
  CONSTRAINT `FK_loan_id_m_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_term_variations`
--

LOCK TABLES `m_loan_term_variations` WRITE;
/*!40000 ALTER TABLE `m_loan_term_variations` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_term_variations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_loan_transaction`
--

DROP TABLE IF EXISTS `m_loan_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_loan_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `office_id` bigint(20) NOT NULL,
  `payment_detail_id` bigint(20) DEFAULT NULL,
  `is_reversed` tinyint(1) NOT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `transaction_type_enum` smallint(5) NOT NULL,
  `transaction_date` date NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `principal_portion_derived` decimal(19,6) DEFAULT NULL,
  `interest_portion_derived` decimal(19,6) DEFAULT NULL,
  `fee_charges_portion_derived` decimal(19,6) DEFAULT NULL,
  `penalty_charges_portion_derived` decimal(19,6) DEFAULT NULL,
  `overpayment_portion_derived` decimal(19,6) DEFAULT NULL,
  `unrecognized_income_portion` decimal(19,6) DEFAULT NULL,
  `outstanding_loan_balance_derived` decimal(19,6) DEFAULT NULL,
  `submitted_on_date` date NOT NULL,
  `manually_adjusted_or_reversed` tinyint(1) DEFAULT '0',
  `created_date` datetime DEFAULT NULL,
  `appuser_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `external_id_UNIQUE` (`external_id`),
  KEY `FKCFCEA42640BE0710` (`loan_id`),
  KEY `FK_m_loan_transaction_m_payment_detail` (`payment_detail_id`),
  KEY `FK_m_loan_transaction_m_office` (`office_id`),
  CONSTRAINT `FKCFCEA42640BE0710` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FK_m_loan_transaction_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_loan_transaction_m_payment_detail` FOREIGN KEY (`payment_detail_id`) REFERENCES `m_payment_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_loan_transaction`
--

LOCK TABLES `m_loan_transaction` WRITE;
/*!40000 ALTER TABLE `m_loan_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_loan_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_mandatory_savings_schedule`
--

DROP TABLE IF EXISTS `m_mandatory_savings_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_mandatory_savings_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL,
  `fromdate` date DEFAULT NULL,
  `duedate` date NOT NULL,
  `installment` smallint(5) NOT NULL,
  `deposit_amount` decimal(19,6) DEFAULT NULL,
  `deposit_amount_completed_derived` decimal(19,6) DEFAULT NULL,
  `total_paid_in_advance_derived` decimal(19,6) DEFAULT NULL,
  `total_paid_late_derived` decimal(19,6) DEFAULT NULL,
  `completed_derived` bit(1) NOT NULL,
  `obligations_met_on_date` date DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKMSS0000000001` (`savings_account_id`),
  CONSTRAINT `FKMSS0000000001` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_mandatory_savings_schedule`
--

LOCK TABLES `m_mandatory_savings_schedule` WRITE;
/*!40000 ALTER TABLE `m_mandatory_savings_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_mandatory_savings_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_meeting`
--

DROP TABLE IF EXISTS `m_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_meeting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `calendar_instance_id` bigint(20) NOT NULL,
  `meeting_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_calendar_instance_id_meeting_date` (`calendar_instance_id`,`meeting_date`),
  CONSTRAINT `FK_m_calendar_instance_m_meeting` FOREIGN KEY (`calendar_instance_id`) REFERENCES `m_calendar_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_meeting`
--

LOCK TABLES `m_meeting` WRITE;
/*!40000 ALTER TABLE `m_meeting` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_note`
--

DROP TABLE IF EXISTS `m_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `loan_id` bigint(20) DEFAULT NULL,
  `loan_transaction_id` bigint(20) DEFAULT NULL,
  `savings_account_id` bigint(20) DEFAULT NULL,
  `savings_account_transaction_id` bigint(20) DEFAULT NULL,
  `note_type_enum` smallint(5) NOT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7C9708924D26803` (`loan_transaction_id`),
  KEY `FK7C97089541F0A56` (`createdby_id`),
  KEY `FK7C970897179A0CB` (`client_id`),
  KEY `FK_m_note_m_group` (`group_id`),
  KEY `FK7C970898F889C3F` (`lastmodifiedby_id`),
  KEY `FK7C9708940BE0710` (`loan_id`),
  KEY `FK_savings_account_id` (`savings_account_id`),
  CONSTRAINT `FK7C9708924D26803` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `FK7C9708940BE0710` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FK7C97089541F0A56` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK7C970897179A0CB` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK7C970898F889C3F` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_m_note_m_group` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `FK_savings_account_id` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_note`
--

LOCK TABLES `m_note` WRITE;
/*!40000 ALTER TABLE `m_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_office`
--

DROP TABLE IF EXISTS `m_office`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_office` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `hierarchy` varchar(100) DEFAULT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `opening_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_org` (`name`),
  UNIQUE KEY `externalid_org` (`external_id`),
  KEY `FK2291C477E2551DCC` (`parent_id`),
  CONSTRAINT `FK2291C477E2551DCC` FOREIGN KEY (`parent_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_office`
--

LOCK TABLES `m_office` WRITE;
/*!40000 ALTER TABLE `m_office` DISABLE KEYS */;
INSERT INTO `m_office` VALUES (1, NULL, '.', '1', 'Head Office', '2009-01-01'),
(2, 4, '.4.2.', 'NDELH03', 'Delhi-Krishna Nagar', '2010-01-01'),
(3, 4, '.4.3.', 'NDELH02', 'Delhi-Lakshmi Nagar', '2010-01-01'),
(4, 1, '.4.', 'NORTH 1', 'North 1', '2010-01-01'),
(5, 1, '.5.', 'NORTH 2', 'North 2', '2010-01-01'),
(6, 1, '.6.', 'WEST', 'West', '2010-01-01'),
(7, 1, '.7.', 'SOUTH', 'South', '2010-01-01'),
(8, 5, '.5.8.', 'NJAIP01', 'Jaipur Raja Park', '2010-01-01'),
(9, 5, '.5.9.', 'NJAIP04', 'Jaipur Mansarovar', '2010-01-01'),
(10, 5, '.5.10.', 'NJAIP02', 'Jaipur Tripolia Bazaar', '2010-01-01'),
(11, 5, '.5.11.', 'NAJME02', 'Ajmer Marwad House', '2015-05-04'),
(12, 6, '.6.12.', 'WMUMB01', 'Mumbai Thane', '2010-01-01'),
(13, 6, '.6.13.', 'WPUNE01', 'Pune MG Road', '2010-01-01'),
(14, 7, '.7.14.', 'SCHEN01', 'Chennai Ashok Nagar', '2010-01-01'),
(15, 7, '.7.15.', 'SVELL01', 'Vellore Katpadi', '2010-01-01'),
(16, 6, '.6.16.', 'WMUMB02', 'Mumbai Mira Road', '2010-01-01'),
(17, 7, '.7.17.', 'SCHEN03', 'Chennai Valasaravakkam', '2010-01-01'),
(18, 7, '.7.18.', 'SCHEN05', 'Chennai Mannady', '2010-01-01'),
(19, 7, '.7.19.', 'SCHEN04', 'Chennai Mogappair', '2010-01-01'),
(20, 6, '.6.20.', 'WMUMB03', 'Mumbai Bhayandar', '2010-01-01'),
(21, 7, '.7.21.', 'SVELL02', 'Vellore Gudiyatham', '2010-01-01'),
(22, 6, '.6.22.', 'WPUNE02', 'Pune Nigdi', '2010-01-01');

/*!40000 ALTER TABLE `m_office` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_office_transaction`
--

DROP TABLE IF EXISTS `m_office_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_office_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_office_id` bigint(20) DEFAULT NULL,
  `to_office_id` bigint(20) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` int(11) NOT NULL,
  `transaction_amount` decimal(19,6) NOT NULL,
  `transaction_date` date NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1E37728B93C6C1B6` (`to_office_id`),
  KEY `FK1E37728B783C5C25` (`from_office_id`),
  CONSTRAINT `FK1E37728B783C5C25` FOREIGN KEY (`from_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK1E37728B93C6C1B6` FOREIGN KEY (`to_office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_office_transaction`
--

LOCK TABLES `m_office_transaction` WRITE;
/*!40000 ALTER TABLE `m_office_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_office_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_organisation_currency`
--

DROP TABLE IF EXISTS `m_organisation_currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_organisation_currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(3) NOT NULL,
  `decimal_places` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `display_symbol` varchar(10) DEFAULT NULL,
  `internationalized_name_code` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_organisation_currency`
--

LOCK TABLES `m_organisation_currency` WRITE;
/*!40000 ALTER TABLE `m_organisation_currency` DISABLE KEYS */;
INSERT INTO `m_organisation_currency` VALUES (22, 'INR', 2, NULL, 'Indian Rupee', '₹', 'currency.INR');
/*!40000 ALTER TABLE `m_organisation_currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_password_validation_policy`
--

DROP TABLE IF EXISTS `m_password_validation_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_password_validation_policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `regex` text NOT NULL,
  `description` text NOT NULL,
  `active` tinyint(4) NOT NULL DEFAULT '0',
  `key` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_password_validation_policy`
--

LOCK TABLES `m_password_validation_policy` WRITE;
/*!40000 ALTER TABLE `m_password_validation_policy` DISABLE KEYS */;
INSERT INTO `m_password_validation_policy` VALUES (1,'^.{1,50}$','Password most be at least 1 character and not more that 50 characters long',1,'simple'),(2,'^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,50}$','Password must be at least 6 characters, no more than 50 characters long, must include at least one upper case letter, one lower case letter, one numeric digit and no space',0,'secure');
/*!40000 ALTER TABLE `m_password_validation_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_payment_detail`
--

DROP TABLE IF EXISTS `m_payment_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_payment_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_type_id` int(11) DEFAULT NULL,
  `account_number` varchar(100) DEFAULT NULL,
  `check_number` varchar(100) DEFAULT NULL,
  `receipt_number` varchar(100) DEFAULT NULL,
  `bank_number` varchar(100) DEFAULT NULL,
  `routing_code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_payment_detail_m_payment_type` (`payment_type_id`),
  CONSTRAINT `FK_m_payment_detail_m_payment_type` FOREIGN KEY (`payment_type_id`) REFERENCES `m_payment_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_payment_detail`
--

LOCK TABLES `m_payment_detail` WRITE;
/*!40000 ALTER TABLE `m_payment_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_payment_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_payment_type`
--

DROP TABLE IF EXISTS `m_payment_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_payment_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_cash_payment` tinyint(1) DEFAULT '0',
  `order_position` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_payment_type`
--

LOCK TABLES `m_payment_type` WRITE;
/*!40000 ALTER TABLE `m_payment_type` DISABLE KEYS */;
INSERT INTO `m_payment_type` VALUES 
(1, 'cash', 'cash', 1, 1),
(2, 'Cheque', 'cheque', 0, 2),
(3, 'ECS', 'ECS', 0, 3);


/*!40000 ALTER TABLE `m_payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_permission`
--

DROP TABLE IF EXISTS `m_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grouping` varchar(45) DEFAULT NULL,
  `code` varchar(100) NOT NULL,
  `entity_name` varchar(100) DEFAULT NULL,
  `action_name` varchar(100) DEFAULT NULL,
  `can_maker_checker` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=659 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_permission`
--

LOCK TABLES `m_permission` WRITE;
/*!40000 ALTER TABLE `m_permission` DISABLE KEYS */;
INSERT INTO `m_permission` VALUES INSERT INTO `m_permission`
VALUES
	(1, 'special', 'ALL_FUNCTIONS', NULL, NULL, 0),
	(2, 'special', 'ALL_FUNCTIONS_READ', NULL, NULL, 0),
	(3, 'special', 'CHECKER_SUPER_USER', NULL, NULL, 0),
	(4, 'special', 'REPORTING_SUPER_USER', NULL, NULL, 0),
	(5, 'authorisation', 'READ_PERMISSION', 'PERMISSION', 'READ', 0),
	(6, 'authorisation', 'PERMISSIONS_ROLE', 'ROLE', 'PERMISSIONS', 0),
	(7, 'authorisation', 'CREATE_ROLE', 'ROLE', 'CREATE', 0),
	(8, 'authorisation', 'CREATE_ROLE_CHECKER', 'ROLE', 'CREATE_CHECKER', 0),
	(9, 'authorisation', 'READ_ROLE', 'ROLE', 'READ', 0),
	(10, 'authorisation', 'UPDATE_ROLE', 'ROLE', 'UPDATE', 0),
	(11, 'authorisation', 'UPDATE_ROLE_CHECKER', 'ROLE', 'UPDATE_CHECKER', 0),
	(12, 'authorisation', 'DELETE_ROLE', 'ROLE', 'DELETE', 0),
	(13, 'authorisation', 'DELETE_ROLE_CHECKER', 'ROLE', 'DELETE_CHECKER', 0),
	(14, 'authorisation', 'CREATE_USER', 'USER', 'CREATE', 0),
	(15, 'authorisation', 'CREATE_USER_CHECKER', 'USER', 'CREATE_CHECKER', 0),
	(16, 'authorisation', 'READ_USER', 'USER', 'READ', 0),
	(17, 'authorisation', 'UPDATE_USER', 'USER', 'UPDATE', 0),
	(18, 'authorisation', 'UPDATE_USER_CHECKER', 'USER', 'UPDATE_CHECKER', 0),
	(19, 'authorisation', 'DELETE_USER', 'USER', 'DELETE', 0),
	(20, 'authorisation', 'DELETE_USER_CHECKER', 'USER', 'DELETE_CHECKER', 0),
	(21, 'configuration', 'READ_CONFIGURATION', 'CONFIGURATION', 'READ', 0),
	(22, 'configuration', 'UPDATE_CONFIGURATION', 'CONFIGURATION', 'UPDATE', 0),
	(23, 'configuration', 'UPDATE_CONFIGURATION_CHECKER', 'CONFIGURATION', 'UPDATE_CHECKER', 0),
	(24, 'configuration', 'READ_CODE', 'CODE', 'READ', 0),
	(25, 'configuration', 'CREATE_CODE', 'CODE', 'CREATE', 0),
	(26, 'configuration', 'CREATE_CODE_CHECKER', 'CODE', 'CREATE_CHECKER', 0),
	(27, 'configuration', 'UPDATE_CODE', 'CODE', 'UPDATE', 0),
	(28, 'configuration', 'UPDATE_CODE_CHECKER', 'CODE', 'UPDATE_CHECKER', 0),
	(29, 'configuration', 'DELETE_CODE', 'CODE', 'DELETE', 0),
	(30, 'configuration', 'DELETE_CODE_CHECKER', 'CODE', 'DELETE_CHECKER', 0),
	(31, 'configuration', 'READ_CODEVALUE', 'CODEVALUE', 'READ', 0),
	(32, 'configuration', 'CREATE_CODEVALUE', 'CODEVALUE', 'CREATE', 0),
	(33, 'configuration', 'CREATE_CODEVALUE_CHECKER', 'CODEVALUE', 'CREATE_CHECKER', 0),
	(34, 'configuration', 'UPDATE_CODEVALUE', 'CODEVALUE', 'UPDATE', 0),
	(35, 'configuration', 'UPDATE_CODEVALUE_CHECKER', 'CODEVALUE', 'UPDATE_CHECKER', 0),
	(36, 'configuration', 'DELETE_CODEVALUE', 'CODEVALUE', 'DELETE', 0),
	(37, 'configuration', 'DELETE_CODEVALUE_CHECKER', 'CODEVALUE', 'DELETE_CHECKER', 0),
	(38, 'configuration', 'READ_CURRENCY', 'CURRENCY', 'READ', 0),
	(39, 'configuration', 'UPDATE_CURRENCY', 'CURRENCY', 'UPDATE', 0),
	(40, 'configuration', 'UPDATE_CURRENCY_CHECKER', 'CURRENCY', 'UPDATE_CHECKER', 0),
	(41, 'configuration', 'UPDATE_PERMISSION', 'PERMISSION', 'UPDATE', 0),
	(42, 'configuration', 'UPDATE_PERMISSION_CHECKER', 'PERMISSION', 'UPDATE_CHECKER', 0),
	(43, 'configuration', 'READ_DATATABLE', 'DATATABLE', 'READ', 0),
	(44, 'configuration', 'REGISTER_DATATABLE', 'DATATABLE', 'REGISTER', 0),
	(45, 'configuration', 'REGISTER_DATATABLE_CHECKER', 'DATATABLE', 'REGISTER_CHECKER', 0),
	(46, 'configuration', 'DEREGISTER_DATATABLE', 'DATATABLE', 'DEREGISTER', 0),
	(47, 'configuration', 'DEREGISTER_DATATABLE_CHECKER', 'DATATABLE', 'DEREGISTER_CHECKER', 0),
	(48, 'configuration', 'READ_AUDIT', 'AUDIT', 'READ', 0),
	(49, 'configuration', 'CREATE_CALENDAR', 'CALENDAR', 'CREATE', 0),
	(50, 'configuration', 'READ_CALENDAR', 'CALENDAR', 'READ', 0),
	(51, 'configuration', 'UPDATE_CALENDAR', 'CALENDAR', 'UPDATE', 0),
	(52, 'configuration', 'DELETE_CALENDAR', 'CALENDAR', 'DELETE', 0),
	(53, 'configuration', 'CREATE_CALENDAR_CHECKER', 'CALENDAR', 'CREATE_CHECKER', 0),
	(54, 'configuration', 'UPDATE_CALENDAR_CHECKER', 'CALENDAR', 'UPDATE_CHECKER', 0),
	(55, 'configuration', 'DELETE_CALENDAR_CHECKER', 'CALENDAR', 'DELETE_CHECKER', 0),
	(57, 'organisation', 'READ_CHARGE', 'CHARGE', 'READ', 0),
	(58, 'organisation', 'CREATE_CHARGE', 'CHARGE', 'CREATE', 0),
	(59, 'organisation', 'CREATE_CHARGE_CHECKER', 'CHARGE', 'CREATE_CHECKER', 0),
	(60, 'organisation', 'UPDATE_CHARGE', 'CHARGE', 'UPDATE', 0),
	(61, 'organisation', 'UPDATE_CHARGE_CHECKER', 'CHARGE', 'UPDATE_CHECKER', 0),
	(62, 'organisation', 'DELETE_CHARGE', 'CHARGE', 'DELETE', 1),
	(63, 'organisation', 'DELETE_CHARGE_CHECKER', 'CHARGE', 'DELETE_CHECKER', 0),
	(64, 'organisation', 'READ_FUND', 'FUND', 'READ', 0),
	(65, 'organisation', 'CREATE_FUND', 'FUND', 'CREATE', 0),
	(66, 'organisation', 'CREATE_FUND_CHECKER', 'FUND', 'CREATE_CHECKER', 0),
	(67, 'organisation', 'UPDATE_FUND', 'FUND', 'UPDATE', 0),
	(68, 'organisation', 'UPDATE_FUND_CHECKER', 'FUND', 'UPDATE_CHECKER', 0),
	(69, 'organisation', 'DELETE_FUND', 'FUND', 'DELETE', 1),
	(70, 'organisation', 'DELETE_FUND_CHECKER', 'FUND', 'DELETE_CHECKER', 0),
	(71, 'organisation', 'READ_LOANPRODUCT', 'LOANPRODUCT', 'READ', 0),
	(72, 'organisation', 'CREATE_LOANPRODUCT', 'LOANPRODUCT', 'CREATE', 0),
	(73, 'organisation', 'CREATE_LOANPRODUCT_CHECKER', 'LOANPRODUCT', 'CREATE_CHECKER', 0),
	(74, 'organisation', 'UPDATE_LOANPRODUCT', 'LOANPRODUCT', 'UPDATE', 0),
	(75, 'organisation', 'UPDATE_LOANPRODUCT_CHECKER', 'LOANPRODUCT', 'UPDATE_CHECKER', 0),
	(76, 'organisation', 'DELETE_LOANPRODUCT', 'LOANPRODUCT', 'DELETE', 1),
	(77, 'organisation', 'DELETE_LOANPRODUCT_CHECKER', 'LOANPRODUCT', 'DELETE_CHECKER', 0),
	(78, 'organisation', 'READ_OFFICE', 'OFFICE', 'READ', 0),
	(79, 'organisation', 'CREATE_OFFICE', 'OFFICE', 'CREATE', 0),
	(80, 'organisation', 'CREATE_OFFICE_CHECKER', 'OFFICE', 'CREATE_CHECKER', 0),
	(81, 'organisation', 'UPDATE_OFFICE', 'OFFICE', 'UPDATE', 0),
	(82, 'organisation', 'UPDATE_OFFICE_CHECKER', 'OFFICE', 'UPDATE_CHECKER', 0),
	(83, 'organisation', 'READ_OFFICETRANSACTION', 'OFFICETRANSACTION', 'READ', 0),
	(84, 'organisation', 'DELETE_OFFICE_CHECKER', 'OFFICE', 'DELETE_CHECKER', 0),
	(85, 'organisation', 'CREATE_OFFICETRANSACTION', 'OFFICETRANSACTION', 'CREATE', 0),
	(86, 'organisation', 'CREATE_OFFICETRANSACTION_CHECKER', 'OFFICETRANSACTION', 'CREATE_CHECKER', 0),
	(87, 'organisation', 'DELETE_OFFICETRANSACTION', 'OFFICETRANSACTION', 'DELETE', 1),
	(88, 'organisation', 'DELETE_OFFICETRANSACTION_CHECKER', 'OFFICETRANSACTION', 'DELETE_CHECKER', 0),
	(89, 'organisation', 'READ_STAFF', 'STAFF', 'READ', 0),
	(90, 'organisation', 'CREATE_STAFF', 'STAFF', 'CREATE', 0),
	(91, 'organisation', 'CREATE_STAFF_CHECKER', 'STAFF', 'CREATE_CHECKER', 0),
	(92, 'organisation', 'UPDATE_STAFF', 'STAFF', 'UPDATE', 0),
	(93, 'organisation', 'UPDATE_STAFF_CHECKER', 'STAFF', 'UPDATE_CHECKER', 0),
	(94, 'organisation', 'DELETE_STAFF', 'STAFF', 'DELETE', 1),
	(95, 'organisation', 'DELETE_STAFF_CHECKER', 'STAFF', 'DELETE_CHECKER', 0),
	(96, 'organisation', 'READ_SAVINGSPRODUCT', 'SAVINGSPRODUCT', 'READ', 0),
	(97, 'organisation', 'CREATE_SAVINGSPRODUCT', 'SAVINGSPRODUCT', 'CREATE', 1),
	(98, 'organisation', 'CREATE_SAVINGSPRODUCT_CHECKER', 'SAVINGSPRODUCT', 'CREATE_CHECKER', 0),
	(99, 'organisation', 'UPDATE_SAVINGSPRODUCT', 'SAVINGSPRODUCT', 'UPDATE', 1),
	(100, 'organisation', 'UPDATE_SAVINGSPRODUCT_CHECKER', 'SAVINGSPRODUCT', 'UPDATE_CHECKER', 0),
	(101, 'organisation', 'DELETE_SAVINGSPRODUCT', 'SAVINGSPRODUCT', 'DELETE', 1),
	(102, 'organisation', 'DELETE_SAVINGSPRODUCT_CHECKER', 'SAVINGSPRODUCT', 'DELETE_CHECKER', 0),
	(103, 'portfolio', 'READ_LOAN', 'LOAN', 'READ', 0),
	(104, 'portfolio', 'CREATE_LOAN', 'LOAN', 'CREATE', 0),
	(105, 'portfolio', 'CREATE_LOAN_CHECKER', 'LOAN', 'CREATE_CHECKER', 0),
	(106, 'portfolio', 'UPDATE_LOAN', 'LOAN', 'UPDATE', 0),
	(107, 'portfolio', 'UPDATE_LOAN_CHECKER', 'LOAN', 'UPDATE_CHECKER', 0),
	(108, 'portfolio', 'DELETE_LOAN', 'LOAN', 'DELETE', 1),
	(109, 'portfolio', 'DELETE_LOAN_CHECKER', 'LOAN', 'DELETE_CHECKER', 0),
	(110, 'portfolio', 'READ_CLIENT', 'CLIENT', 'READ', 0),
	(111, 'portfolio', 'CREATE_CLIENT', 'CLIENT', 'CREATE', 0),
	(112, 'portfolio', 'CREATE_CLIENT_CHECKER', 'CLIENT', 'CREATE_CHECKER', 0),
	(113, 'portfolio', 'UPDATE_CLIENT', 'CLIENT', 'UPDATE', 0),
	(114, 'portfolio', 'UPDATE_CLIENT_CHECKER', 'CLIENT', 'UPDATE_CHECKER', 0),
	(115, 'portfolio', 'DELETE_CLIENT', 'CLIENT', 'DELETE', 0),
	(116, 'portfolio', 'DELETE_CLIENT_CHECKER', 'CLIENT', 'DELETE_CHECKER', 0),
	(117, 'portfolio', 'READ_CLIENTIMAGE', 'CLIENTIMAGE', 'READ', 0),
	(118, 'portfolio', 'CREATE_CLIENTIMAGE', 'CLIENTIMAGE', 'CREATE', 0),
	(119, 'portfolio', 'CREATE_CLIENTIMAGE_CHECKER', 'CLIENTIMAGE', 'CREATE_CHECKER', 0),
	(120, 'portfolio', 'DELETE_CLIENTIMAGE', 'CLIENTIMAGE', 'DELETE', 1),
	(121, 'portfolio', 'DELETE_CLIENTIMAGE_CHECKER', 'CLIENTIMAGE', 'DELETE_CHECKER', 0),
	(122, 'portfolio', 'READ_CLIENTNOTE', 'CLIENTNOTE', 'READ', 0),
	(123, 'portfolio', 'CREATE_CLIENTNOTE', 'CLIENTNOTE', 'CREATE', 0),
	(124, 'portfolio', 'CREATE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'CREATE_CHECKER', 0),
	(125, 'portfolio', 'UPDATE_CLIENTNOTE', 'CLIENTNOTE', 'UPDATE', 0),
	(126, 'portfolio', 'UPDATE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'UPDATE_CHECKER', 0),
	(127, 'portfolio', 'DELETE_CLIENTNOTE', 'CLIENTNOTE', 'DELETE', 1),
	(128, 'portfolio', 'DELETE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'DELETE_CHECKER', 0),
	(129, 'portfolio_group', 'READ_GROUPNOTE', 'GROUPNOTE', 'READ', 0),
	(130, 'portfolio_group', 'CREATE_GROUPNOTE', 'GROUPNOTE', 'CREATE', 0),
	(131, 'portfolio_group', 'UPDATE_GROUPNOTE', 'GROUPNOTE', 'UPDATE', 0),
	(132, 'portfolio_group', 'DELETE_GROUPNOTE', 'GROUPNOTE', 'DELETE', 0),
	(133, 'portfolio_group', 'CREATE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'CREATE_CHECKER', 0),
	(134, 'portfolio_group', 'UPDATE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'UPDATE_CHECKER', 0),
	(135, 'portfolio_group', 'DELETE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'DELETE_CHECKER', 0),
	(136, 'portfolio', 'READ_LOANNOTE', 'LOANNOTE', 'READ', 0),
	(137, 'portfolio', 'CREATE_LOANNOTE', 'LOANNOTE', 'CREATE', 0),
	(138, 'portfolio', 'UPDATE_LOANNOTE', 'LOANNOTE', 'UPDATE', 0),
	(139, 'portfolio', 'DELETE_LOANNOTE', 'LOANNOTE', 'DELETE', 1),
	(140, 'portfolio', 'CREATE_LOANNOTE_CHECKER', 'LOANNOTE', 'CREATE_CHECKER', 0),
	(141, 'portfolio', 'UPDATE_LOANNOTE_CHECKER', 'LOANNOTE', 'UPDATE_CHECKER', 0),
	(142, 'portfolio', 'DELETE_LOANNOTE_CHECKER', 'LOANNOTE', 'DELETE_CHECKER', 0),
	(143, 'portfolio', 'READ_LOANTRANSACTIONNOTE', 'LOANTRANSACTIONNOTE', 'READ', 0),
	(144, 'portfolio', 'CREATE_LOANTRANSACTIONNOTE', 'LOANTRANSACTIONNOTE', 'CREATE', 0),
	(145, 'portfolio', 'UPDATE_LOANTRANSACTIONNOTE', 'LOANTRANSACTIONNOTE', 'UPDATE', 0),
	(146, 'portfolio', 'DELETE_LOANTRANSACTIONNOTE', 'LOANTRANSACTIONNOTE', 'DELETE', 1),
	(147, 'portfolio', 'CREATE_LOANTRANSACTIONNOTE_CHECKER', 'LOANTRANSACTIONNOTE', 'CREATE_CHECKER', 0),
	(148, 'portfolio', 'UPDATE_LOANTRANSACTIONNOTE_CHECKER', 'LOANTRANSACTIONNOTE', 'UPDATE_CHECKER', 0),
	(149, 'portfolio', 'DELETE_LOANTRANSACTIONNOTE_CHECKER', 'LOANTRANSACTIONNOTE', 'DELETE_CHECKER', 0),
	(150, 'portfolio', 'READ_SAVINGNOTE', 'SAVINGNOTE', 'READ', 0),
	(151, 'portfolio', 'CREATE_SAVINGNOTE', 'SAVINGNOTE', 'CREATE', 1),
	(152, 'portfolio', 'UPDATE_SAVINGNOTE', 'SAVINGNOTE', 'UPDATE', 1),
	(153, 'portfolio', 'DELETE_SAVINGNOTE', 'SAVINGNOTE', 'DELETE', 1),
	(154, 'portfolio', 'CREATE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'CREATE_CHECKER', 0),
	(155, 'portfolio', 'UPDATE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'UPDATE_CHECKER', 0),
	(156, 'portfolio', 'DELETE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'DELETE_CHECKER', 0),
	(157, 'portfolio', 'READ_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'READ', 0),
	(158, 'portfolio', 'CREATE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'CREATE', 0),
	(159, 'portfolio', 'CREATE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'CREATE_CHECKER', 0),
	(160, 'portfolio', 'UPDATE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'UPDATE', 0),
	(161, 'portfolio', 'UPDATE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'UPDATE_CHECKER', 0),
	(162, 'portfolio', 'DELETE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'DELETE', 1),
	(163, 'portfolio', 'DELETE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'DELETE_CHECKER', 0),
	(164, 'portfolio', 'READ_DOCUMENT', 'DOCUMENT', 'READ', 0),
	(165, 'portfolio', 'CREATE_DOCUMENT', 'DOCUMENT', 'CREATE', 0),
	(166, 'portfolio', 'CREATE_DOCUMENT_CHECKER', 'DOCUMENT', 'CREATE_CHECKER', 0),
	(167, 'portfolio', 'UPDATE_DOCUMENT', 'DOCUMENT', 'UPDATE', 0),
	(168, 'portfolio', 'UPDATE_DOCUMENT_CHECKER', 'DOCUMENT', 'UPDATE_CHECKER', 0),
	(169, 'portfolio', 'DELETE_DOCUMENT', 'DOCUMENT', 'DELETE', 1),
	(170, 'portfolio', 'DELETE_DOCUMENT_CHECKER', 'DOCUMENT', 'DELETE_CHECKER', 0),
	(171, 'portfolio_group', 'READ_GROUP', 'GROUP', 'READ', 0),
	(172, 'portfolio_group', 'CREATE_GROUP', 'GROUP', 'CREATE', 0),
	(173, 'portfolio_group', 'CREATE_GROUP_CHECKER', 'GROUP', 'CREATE_CHECKER', 0),
	(174, 'portfolio_group', 'UPDATE_GROUP', 'GROUP', 'UPDATE', 0),
	(175, 'portfolio_group', 'UPDATE_GROUP_CHECKER', 'GROUP', 'UPDATE_CHECKER', 0),
	(176, 'portfolio_group', 'DELETE_GROUP', 'GROUP', 'DELETE', 0),
	(177, 'portfolio_group', 'DELETE_GROUP_CHECKER', 'GROUP', 'DELETE_CHECKER', 0),
	(178, 'portfolio_group', 'UNASSIGNSTAFF_GROUP', 'GROUP', 'UNASSIGNSTAFF', 0),
	(179, 'portfolio_group', 'UNASSIGNSTAFF_GROUP_CHECKER', 'GROUP', 'UNASSIGNSTAFF_CHECKER', 0),
	(180, 'portfolio', 'CREATE_LOANCHARGE', 'LOANCHARGE', 'CREATE', 0),
	(181, 'portfolio', 'CREATE_LOANCHARGE_CHECKER', 'LOANCHARGE', 'CREATE_CHECKER', 0),
	(182, 'portfolio', 'UPDATE_LOANCHARGE', 'LOANCHARGE', 'UPDATE', 0),
	(183, 'portfolio', 'UPDATE_LOANCHARGE_CHECKER', 'LOANCHARGE', 'UPDATE_CHECKER', 0),
	(184, 'portfolio', 'DELETE_LOANCHARGE', 'LOANCHARGE', 'DELETE', 1),
	(185, 'portfolio', 'DELETE_LOANCHARGE_CHECKER', 'LOANCHARGE', 'DELETE_CHECKER', 0),
	(186, 'portfolio', 'WAIVE_LOANCHARGE', 'LOANCHARGE', 'WAIVE', 0),
	(187, 'portfolio', 'WAIVE_LOANCHARGE_CHECKER', 'LOANCHARGE', 'WAIVE_CHECKER', 0),
	(188, 'portfolio', 'READ_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'READ', 0),
	(189, 'portfolio', 'CREATE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'CREATE', 1),
	(190, 'portfolio', 'CREATE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'CREATE_CHECKER', 0),
	(191, 'portfolio', 'UPDATE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'UPDATE', 1),
	(192, 'portfolio', 'UPDATE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'UPDATE_CHECKER', 0),
	(193, 'portfolio', 'DELETE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'DELETE', 1),
	(194, 'portfolio', 'DELETE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'DELETE_CHECKER', 0),
	(195, 'portfolio', 'READ_GUARANTOR', 'GUARANTOR', 'READ', 0),
	(196, 'portfolio', 'CREATE_GUARANTOR', 'GUARANTOR', 'CREATE', 0),
	(197, 'portfolio', 'CREATE_GUARANTOR_CHECKER', 'GUARANTOR', 'CREATE_CHECKER', 0),
	(198, 'portfolio', 'UPDATE_GUARANTOR', 'GUARANTOR', 'UPDATE', 0),
	(199, 'portfolio', 'UPDATE_GUARANTOR_CHECKER', 'GUARANTOR', 'UPDATE_CHECKER', 0),
	(200, 'portfolio', 'DELETE_GUARANTOR', 'GUARANTOR', 'DELETE', 1),
	(201, 'portfolio', 'DELETE_GUARANTOR_CHECKER', 'GUARANTOR', 'DELETE_CHECKER', 0),
	(202, 'portfolio', 'READ_COLLATERAL', 'COLLATERAL', 'READ', 0),
	(203, 'portfolio', 'CREATE_COLLATERAL', 'COLLATERAL', 'CREATE', 0),
	(204, 'portfolio', 'UPDATE_COLLATERAL', 'COLLATERAL', 'UPDATE', 0),
	(205, 'portfolio', 'DELETE_COLLATERAL', 'COLLATERAL', 'DELETE', 1),
	(206, 'portfolio', 'CREATE_COLLATERAL_CHECKER', 'COLLATERAL', 'CREATE_CHECKER', 0),
	(207, 'portfolio', 'UPDATE_COLLATERAL_CHECKER', 'COLLATERAL', 'UPDATE_CHECKER', 0),
	(208, 'portfolio', 'DELETE_COLLATERAL_CHECKER', 'COLLATERAL', 'DELETE_CHECKER', 0),
	(209, 'transaction_loan', 'APPROVE_LOAN', 'LOAN', 'APPROVE', 0),
	(211, 'transaction_loan', 'REJECT_LOAN', 'LOAN', 'REJECT', 0),
	(213, 'transaction_loan', 'WITHDRAW_LOAN', 'LOAN', 'WITHDRAW', 0),
	(215, 'transaction_loan', 'APPROVALUNDO_LOAN', 'LOAN', 'APPROVALUNDO', 1),
	(216, 'transaction_loan', 'DISBURSE_LOAN', 'LOAN', 'DISBURSE', 1),
	(218, 'transaction_loan', 'DISBURSALUNDO_LOAN', 'LOAN', 'DISBURSALUNDO', 1),
	(219, 'transaction_loan', 'REPAYMENT_LOAN', 'LOAN', 'REPAYMENT', 0),
	(221, 'transaction_loan', 'ADJUST_LOAN', 'LOAN', 'ADJUST', 0),
	(222, 'transaction_loan', 'WAIVEINTERESTPORTION_LOAN', 'LOAN', 'WAIVEINTERESTPORTION', 1),
	(223, 'transaction_loan', 'WRITEOFF_LOAN', 'LOAN', 'WRITEOFF', 1),
	(224, 'transaction_loan', 'CLOSE_LOAN', 'LOAN', 'CLOSE', 1),
	(225, 'transaction_loan', 'CLOSEASRESCHEDULED_LOAN', 'LOAN', 'CLOSEASRESCHEDULED', 0),
	(226, 'transaction_loan', 'UPDATELOANOFFICER_LOAN', 'LOAN', 'UPDATELOANOFFICER', 0),
	(227, 'transaction_loan', 'UPDATELOANOFFICER_LOAN_CHECKER', 'LOAN', 'UPDATELOANOFFICER_CHECKER', 0),
	(228, 'transaction_loan', 'REMOVELOANOFFICER_LOAN', 'LOAN', 'REMOVELOANOFFICER', 0),
	(229, 'transaction_loan', 'REMOVELOANOFFICER_LOAN_CHECKER', 'LOAN', 'REMOVELOANOFFICER_CHECKER', 0),
	(230, 'transaction_loan', 'BULKREASSIGN_LOAN', 'LOAN', 'BULKREASSIGN', 0),
	(231, 'transaction_loan', 'BULKREASSIGN_LOAN_CHECKER', 'LOAN', 'BULKREASSIGN_CHECKER', 0),
	(232, 'transaction_loan', 'APPROVE_LOAN_CHECKER', 'LOAN', 'APPROVE_CHECKER', 0),
	(234, 'transaction_loan', 'REJECT_LOAN_CHECKER', 'LOAN', 'REJECT_CHECKER', 0),
	(236, 'transaction_loan', 'WITHDRAW_LOAN_CHECKER', 'LOAN', 'WITHDRAW_CHECKER', 0),
	(238, 'transaction_loan', 'APPROVALUNDO_LOAN_CHECKER', 'LOAN', 'APPROVALUNDO_CHECKER', 0),
	(239, 'transaction_loan', 'DISBURSE_LOAN_CHECKER', 'LOAN', 'DISBURSE_CHECKER', 0),
	(241, 'transaction_loan', 'DISBURSALUNDO_LOAN_CHECKER', 'LOAN', 'DISBURSALUNDO_CHECKER', 0),
	(242, 'transaction_loan', 'REPAYMENT_LOAN_CHECKER', 'LOAN', 'REPAYMENT_CHECKER', 0),
	(244, 'transaction_loan', 'ADJUST_LOAN_CHECKER', 'LOAN', 'ADJUST_CHECKER', 0),
	(245, 'transaction_loan', 'WAIVEINTERESTPORTION_LOAN_CHECKER', 'LOAN', 'WAIVEINTERESTPORTION_CHECKER', 0),
	(246, 'transaction_loan', 'WRITEOFF_LOAN_CHECKER', 'LOAN', 'WRITEOFF_CHECKER', 0),
	(247, 'transaction_loan', 'CLOSE_LOAN_CHECKER', 'LOAN', 'CLOSE_CHECKER', 0),
	(248, 'transaction_loan', 'CLOSEASRESCHEDULED_LOAN_CHECKER', 'LOAN', 'CLOSEASRESCHEDULED_CHECKER', 0),
	(249, 'transaction_savings', 'DEPOSIT_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'DEPOSIT', 0),
	(250, 'transaction_savings', 'DEPOSIT_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'DEPOSIT_CHECKER', 0),
	(251, 'transaction_savings', 'WITHDRAWAL_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'WITHDRAWAL', 0),
	(252, 'transaction_savings', 'WITHDRAWAL_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'WITHDRAWAL_CHECKER', 0),
	(253, 'transaction_savings', 'ACTIVATE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'ACTIVATE', 0),
	(254, 'transaction_savings', 'ACTIVATE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'ACTIVATE_CHECKER', 0),
	(255, 'transaction_savings', 'CALCULATEINTEREST_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'CALCULATEINTEREST', 0),
	(256, 'transaction_savings', 'CALCULATEINTEREST_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'CALCULATEINTEREST_CHECKER', 0),
	(257, 'accounting', 'CREATE_GLACCOUNT', 'GLACCOUNT', 'CREATE', 0),
	(258, 'accounting', 'UPDATE_GLACCOUNT', 'GLACCOUNT', 'UPDATE', 0),
	(259, 'accounting', 'DELETE_GLACCOUNT', 'GLACCOUNT', 'DELETE', 0),
	(260, 'accounting', 'CREATE_GLCLOSURE', 'GLCLOSURE', 'CREATE', 0),
	(261, 'accounting', 'UPDATE_GLCLOSURE', 'GLCLOSURE', 'UPDATE', 0),
	(262, 'accounting', 'DELETE_GLCLOSURE', 'GLCLOSURE', 'DELETE', 0),
	(263, 'accounting', 'CREATE_JOURNALENTRY', 'JOURNALENTRY', 'CREATE', 0),
	(264, 'accounting', 'REVERSE_JOURNALENTRY', 'JOURNALENTRY', 'REVERSE', 0),
	(265, 'report', 'READ_Active Loans - Details', 'Active Loans - Details', 'READ', 0),
	(266, 'report', 'READ_Active Loans - Summary', 'Active Loans - Summary', 'READ', 0),
	(267, 'report', 'READ_Active Loans by Disbursal Period', 'Active Loans by Disbursal Period', 'READ', 0),
	(268, 'report', 'READ_Active Loans in last installment', 'Active Loans in last installment', 'READ', 0),
	(269, 'report', 'READ_Active Loans in last installment Summary', 'Active Loans in last installment Summary', 'READ', 0),
	(270, 'report', 'READ_Active Loans Passed Final Maturity', 'Active Loans Passed Final Maturity', 'READ', 0),
	(271, 'report', 'READ_Active Loans Passed Final Maturity Summary', 'Active Loans Passed Final Maturity Summary', 'READ', 0),
	(272, 'report', 'READ_Aging Detail', 'Aging Detail', 'READ', 0),
	(273, 'report', 'READ_Aging Summary (Arrears in Months)', 'Aging Summary (Arrears in Months)', 'READ', 0),
	(274, 'report', 'READ_Aging Summary (Arrears in Weeks)', 'Aging Summary (Arrears in Weeks)', 'READ', 0),
	(275, 'report', 'READ_Balance Sheet', 'Balance Sheet', 'READ', 0),
	(276, 'report', 'READ_Branch Expected Cash Flow', 'Branch Expected Cash Flow', 'READ', 0),
	(277, 'report', 'READ_Client Listing', 'Client Listing', 'READ', 0),
	(278, 'report', 'READ_Client Loans Listing', 'Client Loans Listing', 'READ', 0),
	(279, 'report', 'READ_Expected Payments By Date - Basic', 'Expected Payments By Date - Basic', 'READ', 0),
	(280, 'report', 'READ_Expected Payments By Date - Formatted', 'Expected Payments By Date - Formatted', 'READ', 0),
	(281, 'report', 'READ_Funds Disbursed Between Dates Summary', 'Funds Disbursed Between Dates Summary', 'READ', 0),
	(282, 'report', 'READ_Funds Disbursed Between Dates Summary by Office', 'Funds Disbursed Between Dates Summary by Office', 'READ', 0),
	(283, 'report', 'READ_Income Statement', 'Income Statement', 'READ', 0),
	(284, 'report', 'READ_Loan Account Schedule', 'Loan Account Schedule', 'READ', 0),
	(285, 'report', 'READ_Loans Awaiting Disbursal', 'Loans Awaiting Disbursal', 'READ', 0),
	(286, 'report', 'READ_Loans Awaiting Disbursal Summary', 'Loans Awaiting Disbursal Summary', 'READ', 0),
	(287, 'report', 'READ_Loans Awaiting Disbursal Summary by Month', 'Loans Awaiting Disbursal Summary by Month', 'READ', 0),
	(288, 'report', 'READ_Loans Pending Approval', 'Loans Pending Approval', 'READ', 0),
	(289, 'report', 'READ_Obligation Met Loans Details', 'Obligation Met Loans Details', 'READ', 0),
	(290, 'report', 'READ_Obligation Met Loans Summary', 'Obligation Met Loans Summary', 'READ', 0),
	(291, 'report', 'READ_Portfolio at Risk', 'Portfolio at Risk', 'READ', 0),
	(292, 'report', 'READ_Portfolio at Risk by Branch', 'Portfolio at Risk by Branch', 'READ', 0),
	(293, 'report', 'READ_Rescheduled Loans', 'Rescheduled Loans', 'READ', 0),
	(294, 'report', 'READ_Trial Balance', 'Trial Balance', 'READ', 0),
	(295, 'report', 'READ_Written-Off Loans', 'Written-Off Loans', 'READ', 0),
	(296, 'transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'POSTINTEREST', 0),
	(297, 'transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'POSTINTEREST_CHECKER', 0),
	(298, 'portfolio_center', 'READ_CENTER', 'CENTER', 'READ', 0),
	(299, 'portfolio_center', 'CREATE_CENTER', 'CENTER', 'CREATE', 0),
	(300, 'portfolio_center', 'CREATE_CENTER_CHECKER', 'CENTER', 'CREATE_CHECKER', 0),
	(301, 'portfolio_center', 'UPDATE_CENTER', 'CENTER', 'UPDATE', 0),
	(302, 'portfolio_center', 'UPDATE_CENTER_CHECKER', 'CENTER', 'UPDATE_CHECKER', 0),
	(303, 'portfolio_center', 'DELETE_CENTER', 'CENTER', 'DELETE', 0),
	(304, 'portfolio_center', 'DELETE_CENTER_CHECKER', 'CENTER', 'DELETE_CHECKER', 0),
	(305, 'configuration', 'READ_REPORT', 'REPORT', 'READ', 0),
	(306, 'configuration', 'CREATE_REPORT', 'REPORT', 'CREATE', 0),
	(307, 'configuration', 'CREATE_REPORT_CHECKER', 'REPORT', 'CREATE_CHECKER', 0),
	(308, 'configuration', 'UPDATE_REPORT', 'REPORT', 'UPDATE', 0),
	(309, 'configuration', 'UPDATE_REPORT_CHECKER', 'REPORT', 'UPDATE_CHECKER', 0),
	(310, 'configuration', 'DELETE_REPORT', 'REPORT', 'DELETE', 0),
	(311, 'configuration', 'DELETE_REPORT_CHECKER', 'REPORT', 'DELETE_CHECKER', 0),
	(312, 'portfolio', 'ACTIVATE_CLIENT', 'CLIENT', 'ACTIVATE', 0),
	(313, 'portfolio', 'ACTIVATE_CLIENT_CHECKER', 'CLIENT', 'ACTIVATE_CHECKER', 0),
	(314, 'portfolio_center', 'ACTIVATE_CENTER', 'CENTER', 'ACTIVATE', 0),
	(315, 'portfolio_center', 'ACTIVATE_CENTER_CHECKER', 'CENTER', 'ACTIVATE_CHECKER', 0),
	(316, 'portfolio_group', 'ACTIVATE_GROUP', 'GROUP', 'ACTIVATE', 0),
	(317, 'portfolio_group', 'ACTIVATE_GROUP_CHECKER', 'GROUP', 'ACTIVATE_CHECKER', 0),
	(318, 'portfolio_group', 'ASSOCIATECLIENTS_GROUP', 'GROUP', 'ASSOCIATECLIENTS', 0),
	(319, 'portfolio_group', 'DISASSOCIATECLIENTS_GROUP', 'GROUP', 'DISASSOCIATECLIENTS', 0),
	(320, 'portfolio_group', 'SAVECOLLECTIONSHEET_GROUP', 'GROUP', 'SAVECOLLECTIONSHEET', 0),
	(321, 'portfolio_center', 'SAVECOLLECTIONSHEET_CENTER', 'CENTER', 'SAVECOLLECTIONSHEET', 0),
	(323, 'accounting', 'DELETE_ACCOUNTINGRULE', 'ACCOUNTINGRULE', 'DELETE', 0),
	(324, 'accounting', 'CREATE_ACCOUNTINGRULE', 'ACCOUNTINGRULE', 'CREATE', 0),
	(325, 'accounting', 'UPDATE_ACCOUNTINGRULE', 'ACCOUNTINGRULE', 'UPDATE', 0),
	(326, 'report', 'READ_GroupSummaryCounts', 'GroupSummaryCounts', 'READ', 0),
	(327, 'report', 'READ_GroupSummaryAmounts', 'GroupSummaryAmounts', 'READ', 0),
	(328, 'configuration', 'CREATE_DATATABLE', 'DATATABLE', 'CREATE', 0),
	(329, 'configuration', 'CREATE_DATATABLE_CHECKER', 'DATATABLE', 'CREATE_CHECKER', 0),
	(330, 'configuration', 'UPDATE_DATATABLE', 'DATATABLE', 'UPDATE', 0),
	(331, 'configuration', 'UPDATE_DATATABLE_CHECKER', 'DATATABLE', 'UPDATE_CHECKER', 0),
	(332, 'configuration', 'DELETE_DATATABLE', 'DATATABLE', 'DELETE', 0),
	(333, 'configuration', 'DELETE_DATATABLE_CHECKER', 'DATATABLE', 'DELETE_CHECKER', 0),
	(334, 'organisation', 'CREATE_HOLIDAY', 'HOLIDAY', 'CREATE', 0),
	(335, 'portfolio_group', 'ASSIGNROLE_GROUP', 'GROUP', 'ASSIGNROLE', 0),
	(336, 'portfolio_group', 'UNASSIGNROLE_GROUP', 'GROUP', 'UNASSIGNROLE', 0),
	(337, 'portfolio_group', 'UPDATEROLE_GROUP', 'GROUP', 'UPDATEROLE', 0),
	(346, 'report', 'READ_TxnRunningBalances', 'TxnRunningBalances', 'READ', 0),
	(347, 'portfolio', 'UNASSIGNSTAFF_CLIENT', 'CLIENT', 'UNASSIGNSTAFF', 0),
	(348, 'portfolio', 'ASSIGNSTAFF_CLIENT', 'CLIENT', 'ASSIGNSTAFF', 0),
	(349, 'portfolio', 'CLOSE_CLIENT', 'CLIENT', 'CLOSE', 0),
	(350, 'report', 'READ_FieldAgentStats', 'FieldAgentStats', 'READ', 0),
	(351, 'report', 'READ_FieldAgentPrograms', 'FieldAgentPrograms', 'READ', 0),
	(352, 'report', 'READ_ProgramDetails', 'ProgramDetails', 'READ', 0),
	(353, 'report', 'READ_ChildrenStaffList', 'ChildrenStaffList', 'READ', 0),
	(354, 'report', 'READ_CoordinatorStats', 'CoordinatorStats', 'READ', 0),
	(355, 'report', 'READ_BranchManagerStats', 'BranchManagerStats', 'READ', 0),
	(356, 'report', 'READ_ProgramDirectorStats', 'ProgramDirectorStats', 'READ', 0),
	(357, 'report', 'READ_ProgramStats', 'ProgramStats', 'READ', 0),
	(358, 'transaction_savings', 'APPROVE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'APPROVE', 0),
	(359, 'transaction_savings', 'REJECT_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'REJECT', 0),
	(360, 'transaction_savings', 'WITHDRAW_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'WITHDRAW', 0),
	(361, 'transaction_savings', 'APPROVALUNDO_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'APPROVALUNDO', 0),
	(362, 'transaction_savings', 'CLOSE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'CLOSE', 0),
	(363, 'transaction_savings', 'APPROVE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'APPROVE_CHECKER', 0),
	(364, 'transaction_savings', 'REJECT_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'REJECT_CHECKER', 0),
	(365, 'transaction_savings', 'WITHDRAW_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'WITHDRAW_CHECKER', 0),
	(366, 'transaction_savings', 'APPROVALUNDO_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'APPROVALUNDO_CHECKER', 0),
	(367, 'transaction_savings', 'CLOSE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'CLOSE_CHECKER', 0),
	(368, 'transaction_savings', 'UNDOTRANSACTION_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'UNDOTRANSACTION', 0),
	(369, 'transaction_savings', 'UNDOTRANSACTION_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'UNDOTRANSACTION_CHECKER', 0),
	(370, 'portfolio', 'CREATE_PRODUCTMIX', 'PRODUCTMIX', 'CREATE', 0),
	(371, 'portfolio', 'UPDATE_PRODUCTMIX', 'PRODUCTMIX', 'UPDATE', 0),
	(372, 'portfolio', 'DELETE_PRODUCTMIX', 'PRODUCTMIX', 'DELETE', 1),
	(373, 'jobs', 'UPDATE_SCHEDULER', 'SCHEDULER', 'UPDATE', 0),
	(374, 'transaction_savings', 'APPLYANNUALFEE_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'APPLYANNUALFEE', 0),
	(375, 'transaction_savings', 'APPLYANNUALFEE_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'APPLYANNUALFEE_CHECKER', 0),
	(376, 'portfolio_group', 'ASSIGNSTAFF_GROUP', 'GROUP', 'ASSIGNSTAFF', 0),
	(377, 'transaction_savings', 'READ_ACCOUNTTRANSFER', 'ACCOUNTTRANSFER', 'READ', 0),
	(378, 'transaction_savings', 'CREATE_ACCOUNTTRANSFER', 'ACCOUNTTRANSFER', 'CREATE', 0),
	(379, 'transaction_savings', 'CREATE_ACCOUNTTRANSFER_CHECKER', 'ACCOUNTTRANSFER', 'CREATE_CHECKER', 0),
	(380, 'transaction_savings', 'ADJUSTTRANSACTION_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'ADJUSTTRANSACTION', 0),
	(381, 'portfolio', 'CREATE_MEETING', 'MEETING', 'CREATE', 0),
	(382, 'portfolio', 'UPDATE_MEETING', 'MEETING', 'UPDATE', 0),
	(383, 'portfolio', 'DELETE_MEETING', 'MEETING', 'DELETE', 0),
	(384, 'portfolio', 'SAVEORUPDATEATTENDANCE_MEETING', 'MEETING', 'SAVEORUPDATEATTENDANCE', 0),
	(385, 'portfolio_group', 'TRANSFERCLIENTS_GROUP', 'GROUP', 'TRANSFERCLIENTS', 0),
	(386, 'portfolio_group', 'TRANSFERCLIENTS_GROUP_CHECKER', 'GROUP', 'TRANSFERCLIENTS_CHECKER', 0),
	(389, 'portfolio', 'PROPOSETRANSFER_CLIENT', 'CLIENT', 'PROPOSETRANSFER', 0),
	(390, 'portfolio', 'PROPOSETRANSFER_CLIENT_CHECKER', 'CLIENT', 'PROPOSETRANSFER_CHECKER', 0),
	(391, 'portfolio', 'ACCEPTTRANSFER_CLIENT', 'CLIENT', 'ACCEPTTRANSFER', 0),
	(392, 'portfolio', 'ACCEPTTRANSFER_CLIENT_CHECKER', 'CLIENT', 'ACCEPTTRANSFER_CHECKER', 0),
	(393, 'portfolio', 'REJECTTRANSFER_CLIENT', 'CLIENT', 'REJECTTRANSFER', 0),
	(394, 'portfolio', 'REJECTTRANSFER_CLIENT_CHECKER', 'CLIENT', 'REJECTTRANSFER_CHECKER', 0),
	(395, 'portfolio', 'WITHDRAWTRANSFER_CLIENT', 'CLIENT', 'WITHDRAWTRANSFER', 0),
	(396, 'portfolio', 'WITHDRAWTRANSFER_CLIENT_CHECKER', 'CLIENT', 'WITHDRAWTRANSFER_CHECKER', 0),
	(397, 'portfolio', 'CLOSE_GROUP', 'GROUP', 'CLOSE', 1),
	(398, 'portfolio', 'CLOSE_CENTER', 'CENTER', 'CLOSE', 0),
	(399, 'xbrlmapping', 'UPDATE_XBRLMAPPING', 'XBRLMAPPING', 'UPDATE', 0),
	(400, 'configuration', 'READ_CACHE', 'CACHE', 'READ', 0),
	(401, 'configuration', 'UPDATE_CACHE', 'CACHE', 'UPDATE', 0),
	(402, 'transaction_loan', 'PAY_LOANCHARGE', 'LOANCHARGE', 'PAY', 0),
	(403, 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'CREATE', 1),
	(404, 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'CREATE_CHECKER', 0),
	(405, 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'UPDATE', 1),
	(406, 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'UPDATE_CHECKER', 0),
	(407, 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'DELETE', 1),
	(408, 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'DELETE_CHECKER', 0),
	(409, 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'WAIVE', 1),
	(410, 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'WAIVE_CHECKER', 0),
	(411, 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'PAY', 1),
	(412, 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'PAY_CHECKER', 0),
	(413, 'portfolio', 'PROPOSEANDACCEPTTRANSFER_CLIENT', 'CLIENT', 'PROPOSEANDACCEPTTRANSFER', 0),
	(414, 'portfolio', 'PROPOSEANDACCEPTTRANSFER_CLIENT_CHECKER', 'CLIENT', 'PROPOSEANDACCEPTTRANSFER_CHECKER', 0),
	(415, 'organisation', 'DELETE_TEMPLATE', 'TEMPLATE', 'DELETE', 1),
	(416, 'organisation', 'CREATE_TEMPLATE', 'TEMPLATE', 'CREATE', 0),
	(417, 'organisation', 'UPDATE_TEMPLATE', 'TEMPLATE', 'UPDATE', 0),
	(418, 'organisation', 'READ_TEMPLATE', 'TEMPLATE', 'READ', 0),
	(419, 'accounting', 'UPDATERUNNINGBALANCE_JOURNALENTRY', 'JOURNALENTRY', 'UPDATERUNNINGBALANCE', 0),
	(420, 'organisation', 'READ_SMS', 'SMS', 'READ', 0),
	(421, 'organisation', 'CREATE_SMS', 'SMS', 'CREATE', 0),
	(422, 'organisation', 'CREATE_SMS_CHECKER', 'SMS', 'CREATE_CHECKER', 0),
	(423, 'organisation', 'UPDATE_SMS', 'SMS', 'UPDATE', 0),
	(424, 'organisation', 'UPDATE_SMS_CHECKER', 'SMS', 'UPDATE_CHECKER', 0),
	(425, 'organisation', 'DELETE_SMS', 'SMS', 'DELETE', 1),
	(426, 'organisation', 'DELETE_SMS_CHECKER', 'SMS', 'DELETE_CHECKER', 0),
	(427, 'organisation', 'CREATE_HOLIDAY_CHECKER', 'HOLIDAY', 'CREATE_CHECKER', 0),
	(428, 'organisation', 'ACTIVATE_HOLIDAY', 'HOLIDAY', 'ACTIVATE', 0),
	(429, 'organisation', 'ACTIVATE_HOLIDAY_CHECKER', 'HOLIDAY', 'ACTIVATE_CHECKER', 0),
	(430, 'organisation', 'UPDATE_HOLIDAY', 'HOLIDAY', 'UPDATE', 0),
	(431, 'organisation', 'UPDATE_HOLIDAY_CHECKER', 'HOLIDAY', 'UPDATE_CHECKER', 0),
	(432, 'organisation', 'DELETE_HOLIDAY', 'HOLIDAY', 'DELETE', 1),
	(433, 'organisation', 'DELETE_HOLIDAY_CHECKER', 'HOLIDAY', 'DELETE_CHECKER', 0),
	(434, 'transaction_loan', 'UNDOWRITEOFF_LOAN', 'LOAN', 'UNDOWRITEOFF', 1),
	(435, 'portfolio', 'READ_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'READ', 0),
	(436, 'accounting', 'CREATE_JOURNALENTRY_CHECKER', 'JOURNALENTRY', 'CREATE_CHECKER', 0),
	(437, 'portfolio', 'UPDATE_DISBURSEMENTDETAIL', 'DISBURSEMENTDETAIL', 'UPDATE', 0),
	(438, 'portfolio', 'UPDATESAVINGSACCOUNT_CLIENT', 'CLIENT', 'UPDATESAVINGSACCOUNT', 0),
	(439, 'accounting', 'READ_ACCOUNTINGRULE', 'ACCOUNTINGRULE', 'READ', 0),
	(440, 'accounting', 'READ_JOURNALENTRY', 'JOURNALENTRY', 'READ', 0),
	(441, 'accounting', 'READ_GLACCOUNT', 'GLACCOUNT', 'READ', 0),
	(442, 'accounting', 'READ_GLCLOSURE', 'GLCLOSURE', 'READ', 0),
	(443, 'organisation', 'READ_HOLIDAY', 'HOLIDAY', 'READ', 0),
	(444, 'jobs', 'READ_SCHEDULER', 'SCHEDULER', 'READ', 0),
	(445, 'portfolio', 'READ_PRODUCTMIX', 'PRODUCTMIX', 'READ', 0),
	(446, 'portfolio', 'READ_MEETING', 'MEETING', 'READ', 0),
	(447, 'jobs', 'EXECUTEJOB_SCHEDULER', 'SCHEDULER', 'EXECUTEJOB', 0),
	(448, 'account_transfer', 'READ_STANDINGINSTRUCTION ', 'STANDINGINSTRUCTION ', 'READ', 0),
	(449, 'account_transfer', 'CREATE_STANDINGINSTRUCTION ', 'STANDINGINSTRUCTION ', 'CREATE', 0),
	(450, 'account_transfer', 'UPDATE_STANDINGINSTRUCTION ', 'STANDINGINSTRUCTION ', 'UPDATE', 0),
	(451, 'account_transfer', 'DELETE_STANDINGINSTRUCTION ', 'STANDINGINSTRUCTION ', 'DELETE', 0),
	(452, 'portfolio', 'CREATE_INTERESTRATECHART', 'INTERESTRATECHART', 'CREATE', 0),
	(453, 'portfolio', 'CREATE_INTERESTRATECHART_CHECKER', 'INTERESTRATECHART', 'CREATE_CHECKER', 0),
	(454, 'portfolio', 'UPDATE_INTERESTRATECHART', 'INTERESTRATECHART', 'UPDATE', 0),
	(455, 'portfolio', 'DELETE_INTERESTRATECHART', 'INTERESTRATECHART', 'DELETE', 1),
	(456, 'portfolio', 'UPDATE_INTERESTRATECHART_CHECKER', 'INTERESTRATECHART', 'UPDATE_CHECKER', 0),
	(457, 'portfolio', 'DELETE_INTERESTRATECHART_CHECKER', 'INTERESTRATECHART', 'DELETE_CHECKER', 0),
	(458, 'portfolio', 'CREATE_CHARTSLAB', 'CHARTSLAB', 'CREATE', 0),
	(459, 'portfolio', 'CREATE_CHARTSLAB_CHECKER', 'CHARTSLAB', 'CREATE_CHECKER', 0),
	(460, 'portfolio', 'UPDATE_CHARTSLAB', 'CHARTSLAB', 'UPDATE', 0),
	(461, 'portfolio', 'DELETE_CHARTSLAB', 'CHARTSLAB', 'DELETE', 0),
	(462, 'portfolio', 'UPDATE_CHARTSLAB_CHECKER', 'CHARTSLAB', 'UPDATE_CHECKER', 0),
	(463, 'portfolio', 'DELETE_CHARTSLAB_CHECKER', 'CHARTSLAB', 'DELETE_CHECKER', 0),
	(464, 'portfolio', 'CREATE_FIXEDDEPOSITPRODUCT', 'FIXEDDEPOSITPRODUCT', 'CREATE', 0),
	(465, 'portfolio', 'CREATE_FIXEDDEPOSITPRODUCT_CHECKER', 'FIXEDDEPOSITPRODUCT', 'CREATE_CHECKER', 0),
	(466, 'portfolio', 'UPDATE_FIXEDDEPOSITPRODUCT', 'FIXEDDEPOSITPRODUCT', 'UPDATE', 0),
	(467, 'portfolio', 'DELETE_FIXEDDEPOSITPRODUCT', 'FIXEDDEPOSITPRODUCT', 'DELETE', 0),
	(468, 'portfolio', 'UPDATE_FIXEDDEPOSITPRODUCT_CHECKER', 'FIXEDDEPOSITPRODUCT', 'UPDATE_CHECKER', 0),
	(469, 'portfolio', 'DELETE_FIXEDDEPOSITPRODUCT_CHECKER', 'FIXEDDEPOSITPRODUCT', 'DELETE_CHECKER', 0),
	(470, 'portfolio', 'CREATE_RECURRINGDEPOSITPRODUCT', 'RECURRINGDEPOSITPRODUCT', 'CREATE', 1),
	(471, 'portfolio', 'CREATE_RECURRINGDEPOSITPRODUCT_CHECKER', 'RECURRINGDEPOSITPRODUCT', 'CREATE_CHECKER', 0),
	(472, 'portfolio', 'UPDATE_RECURRINGDEPOSITPRODUCT', 'RECURRINGDEPOSITPRODUCT', 'UPDATE', 1),
	(473, 'portfolio', 'DELETE_RECURRINGDEPOSITPRODUCT', 'RECURRINGDEPOSITPRODUCT', 'DELETE', 1),
	(474, 'portfolio', 'UPDATE_RECURRINGDEPOSITPRODUCT_CHECKER', 'RECURRINGDEPOSITPRODUCT', 'UPDATE_CHECKER', 0),
	(475, 'portfolio', 'DELETE_RECURRINGDEPOSITPRODUCT_CHECKER', 'RECURRINGDEPOSITPRODUCT', 'DELETE_CHECKER', 0),
	(476, 'portfolio', 'READ_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'READ', 0),
	(477, 'portfolio', 'CREATE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'CREATE', 0),
	(478, 'portfolio', 'CREATE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'CREATE_CHECKER', 0),
	(479, 'portfolio', 'UPDATE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'UPDATE', 0),
	(480, 'portfolio', 'UPDATE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'UPDATE_CHECKER', 0),
	(481, 'portfolio', 'DELETE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'DELETE', 0),
	(482, 'portfolio', 'DELETE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'DELETE_CHECKER', 0),
	(483, 'transaction_savings', 'DEPOSIT_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'DEPOSIT', 0),
	(484, 'transaction_savings', 'DEPOSIT_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'DEPOSIT_CHECKER', 0),
	(485, 'transaction_savings', 'WITHDRAWAL_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'WITHDRAWAL', 0),
	(486, 'transaction_savings', 'WITHDRAWAL_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'WITHDRAWAL_CHECKER', 0),
	(487, 'transaction_savings', 'ACTIVATE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'ACTIVATE', 0),
	(488, 'transaction_savings', 'ACTIVATE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'ACTIVATE_CHECKER', 0),
	(489, 'transaction_savings', 'CALCULATEINTEREST_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'CALCULATEINTEREST', 0),
	(490, 'transaction_savings', 'CALCULATEINTEREST_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'CALCULATEINTEREST_CHECKER', 0),
	(491, 'transaction_savings', 'POSTINTEREST_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'POSTINTEREST', 0),
	(492, 'transaction_savings', 'POSTINTEREST_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'POSTINTEREST_CHECKER', 0),
	(493, 'transaction_savings', 'APPROVE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'APPROVE', 0),
	(494, 'transaction_savings', 'REJECT_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'REJECT', 0),
	(495, 'transaction_savings', 'WITHDRAW_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'WITHDRAW', 0),
	(496, 'transaction_savings', 'APPROVALUNDO_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'APPROVALUNDO', 0),
	(497, 'transaction_savings', 'CLOSE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'CLOSE', 0),
	(498, 'transaction_savings', 'APPROVE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'APPROVE_CHECKER', 0),
	(499, 'transaction_savings', 'REJECT_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'REJECT_CHECKER', 0),
	(500, 'transaction_savings', 'WITHDRAW_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'WITHDRAW_CHECKER', 0),
	(501, 'transaction_savings', 'APPROVALUNDO_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'APPROVALUNDO_CHECKER', 0),
	(502, 'transaction_savings', 'CLOSE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'CLOSE_CHECKER', 0),
	(503, 'transaction_savings', 'UNDOTRANSACTION_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'UNDOTRANSACTION', 0),
	(504, 'transaction_savings', 'UNDOTRANSACTION_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'UNDOTRANSACTION_CHECKER', 0),
	(505, 'transaction_savings', 'ADJUSTTRANSACTION_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'ADJUSTTRANSACTION', 0),
	(506, 'portfolio', 'READ_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'READ', 0),
	(507, 'portfolio', 'CREATE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'CREATE', 1),
	(508, 'portfolio', 'CREATE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'CREATE_CHECKER', 0),
	(509, 'portfolio', 'UPDATE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'UPDATE', 1),
	(510, 'portfolio', 'UPDATE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'UPDATE_CHECKER', 0),
	(511, 'portfolio', 'DELETE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'DELETE', 1),
	(512, 'portfolio', 'DELETE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'DELETE_CHECKER', 0),
	(513, 'transaction_savings', 'DEPOSIT_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'DEPOSIT', 0),
	(514, 'transaction_savings', 'DEPOSIT_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'DEPOSIT_CHECKER', 0),
	(515, 'transaction_savings', 'WITHDRAWAL_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'WITHDRAWAL', 0),
	(516, 'transaction_savings', 'WITHDRAWAL_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'WITHDRAWAL_CHECKER', 0),
	(517, 'transaction_savings', 'ACTIVATE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'ACTIVATE', 0),
	(518, 'transaction_savings', 'ACTIVATE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'ACTIVATE_CHECKER', 0),
	(519, 'transaction_savings', 'CALCULATEINTEREST_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'CALCULATEINTEREST', 0),
	(520, 'transaction_savings', 'CALCULATEINTEREST_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'CALCULATEINTEREST_CHECKER', 0),
	(521, 'transaction_savings', 'POSTINTEREST_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'POSTINTEREST', 0),
	(522, 'transaction_savings', 'POSTINTEREST_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'POSTINTEREST_CHECKER', 0),
	(523, 'transaction_savings', 'APPROVE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'APPROVE', 0),
	(524, 'transaction_savings', 'REJECT_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'REJECT', 0),
	(525, 'transaction_savings', 'WITHDRAW_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'WITHDRAW', 0),
	(526, 'transaction_savings', 'APPROVALUNDO_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'APPROVALUNDO', 0),
	(527, 'transaction_savings', 'CLOSE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'CLOSE', 0),
	(528, 'transaction_savings', 'APPROVE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'APPROVE_CHECKER', 0),
	(529, 'transaction_savings', 'REJECT_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'REJECT_CHECKER', 0),
	(530, 'transaction_savings', 'WITHDRAW_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'WITHDRAW_CHECKER', 0),
	(531, 'transaction_savings', 'APPROVALUNDO_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'APPROVALUNDO_CHECKER', 0),
	(532, 'transaction_savings', 'CLOSE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'CLOSE_CHECKER', 0),
	(533, 'transaction_savings', 'UNDOTRANSACTION_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'UNDOTRANSACTION', 0),
	(534, 'transaction_savings', 'UNDOTRANSACTION_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'UNDOTRANSACTION_CHECKER', 0),
	(535, 'transaction_savings', 'ADJUSTTRANSACTION_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'ADJUSTTRANSACTION', 0),
	(536, 'transaction_savings', 'PREMATURECLOSE_FIXEDDEPOSITACCOUNT_CHECKER', 'FIXEDDEPOSITACCOUNT', 'PREMATURECLOSE_CHECKER', 0),
	(537, 'transaction_savings', 'PREMATURECLOSE_FIXEDDEPOSITACCOUNT', 'FIXEDDEPOSITACCOUNT', 'PREMATURECLOSE', 0),
	(538, 'transaction_savings', 'PREMATURECLOSE_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'PREMATURECLOSE_CHECKER', 0),
	(539, 'transaction_savings', 'PREMATURECLOSE_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'PREMATURECLOSE', 0),
	(540, 'transaction_loan', 'DISBURSETOSAVINGS_LOAN', 'LOAN', 'DISBURSETOSAVINGS', 0),
	(541, 'transaction_loan', 'RECOVERYPAYMENT_LOAN', 'LOAN', 'RECOVERYPAYMENT', 0),
	(542, 'organisation', 'READ_RECURRINGDEPOSITPRODUCT', 'RECURRINGDEPOSITPRODUCT', 'READ', 0),
	(543, 'organisation', 'READ_FIXEDDEPOSITPRODUCT', 'FIXEDDEPOSITPRODUCT', 'READ', 0),
	(544, 'accounting', 'READ_FINANCIALACTIVITYACCOUNT', 'FINANCIALACTIVITYACCOUNT', 'READ', 0),
	(545, 'accounting', 'CREATE_FINANCIALACTIVITYACCOUNT', 'FINANCIALACTIVITYACCOUNT', 'CREATE', 0),
	(546, 'accounting', 'DELETE_FINANCIALACTIVITYACCOUNT', 'FINANCIALACTIVITYACCOUNT', 'DELETE', 0),
	(547, 'accounting', 'UPDATE_FINANCIALACTIVITYACCOUNT', 'FINANCIALACTIVITYACCOUNT', 'UPDATE', 0),
	(548, 'datatable', 'UPDATE_LIKELIHOOD', 'likelihood', 'UPDATE', 0),
	(549, 'survey', 'REGISTER_SURVEY', 'survey', 'CREATE', 0),
	(550, 'accounting', 'EXECUTE_PERIODICACCRUALACCOUNTING', 'PERIODICACCRUALACCOUNTING', 'EXECUTE', 0),
	(551, 'portfolio', 'INACTIVATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'INACTIVATE', 1),
	(552, 'portfolio', 'INACTIVATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'INACTIVATE_CHECKER', 0),
	(553, 'portfolio_center', 'DISASSOCIATEGROUPS_CENTER', 'CENTER', 'DISASSOCIATEGROUPS', 0),
	(554, 'portfolio_center', 'ASSOCIATEGROUPS_CENTER', 'CENTER', 'ASSOCIATEGROUPS', 0),
	(555, 'portfolio_center', 'DISASSOCIATEGROUPS_CENTER_CHECKER', 'CENTER', 'DISASSOCIATEGROUPS_CHECKER', 0),
	(556, 'portfolio_center', 'ASSOCIATEGROUPS_CENTER_CHECKER', 'CENTER', 'ASSOCIATEGROUPS_CHECKER', 0),
	(557, 'loan_reschedule', 'READ_RESCHEDULELOAN', 'RESCHEDULELOAN', 'READ', 0),
	(558, 'loan_reschedule', 'CREATE_RESCHEDULELOAN', 'RESCHEDULELOAN', 'CREATE', 0),
	(559, 'loan_reschedule', 'REJECT_RESCHEDULELOAN', 'RESCHEDULELOAN', 'REJECT', 0),
	(560, 'loan_reschedule', 'APPROVE_RESCHEDULELOAN', 'RESCHEDULELOAN', 'APPROVE', 0),
	(561, 'configuration', 'CREATE_HOOK', 'HOOK', 'CREATE', 0),
	(562, 'configuration', 'READ_HOOK', 'HOOK', 'READ', 0),
	(563, 'configuration', 'UPDATE_HOOK', 'HOOK', 'UPDATE', 0),
	(564, 'configuration', 'DELETE_HOOK', 'HOOK', 'DELETE', 0),
	(565, 'portfolio', 'REMOVESAVINGSOFFICER_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'REMOVESAVINGSOFFICER', 1),
	(566, 'portfolio', 'UPDATESAVINGSOFFICER_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'UPDATESAVINGSOFFICER', 1),
	(567, 'report', 'READ_Active Loans - Summary(Pentaho)', 'Active Loans - Summary(Pentaho)', 'READ', 0),
	(568, 'report', 'READ_Active Loans by Disbursal Period(Pentaho)', 'Active Loans by Disbursal Period(Pentaho)', 'READ', 0),
	(569, 'report', 'READ_Active Loans in last installment Summary(Pentaho)', 'Active Loans in last installment Summary(Pentaho)', 'READ', 0),
	(570, 'report', 'READ_Active Loans in last installment(Pentaho)', 'Active Loans in last installment(Pentaho)', 'READ', 0),
	(571, 'report', 'READ_Active Loans Passed Final Maturity Summary(Pentaho)', 'Active Loans Passed Final Maturity Summary(Pentaho)', 'READ', 0),
	(572, 'report', 'READ_Active Loans Passed Final Maturity(Pentaho)', 'Active Loans Passed Final Maturity(Pentaho)', 'READ', 0),
	(573, 'report', 'READ_Aging Detail(Pentaho)', 'Aging Detail(Pentaho)', 'READ', 0),
	(574, 'report', 'READ_Aging Summary (Arrears in Months)(Pentaho)', 'Aging Summary (Arrears in Months)(Pentaho)', 'READ', 0),
	(575, 'report', 'READ_Aging Summary (Arrears in Weeks)(Pentaho)', 'Aging Summary (Arrears in Weeks)(Pentaho)', 'READ', 0),
	(576, 'report', 'READ_Client Listing(Pentaho)', 'Client Listing(Pentaho)', 'READ', 0),
	(577, 'report', 'READ_Client Loan Account Schedule', 'Client Loan Account Schedule', 'READ', 0),
	(578, 'report', 'READ_Client Loans Listing(Pentaho)', 'Client Loans Listing(Pentaho)', 'READ', 0),
	(579, 'report', 'READ_Client Saving Transactions', 'Client Saving Transactions', 'READ', 0),
	(580, 'report', 'READ_Client Savings Summary', 'Client Savings Summary', 'READ', 0),
	(581, 'report', 'READ_ClientSummary ', 'ClientSummary ', 'READ', 0),
	(582, 'report', 'READ_ClientTrendsByDay', 'ClientTrendsByDay', 'READ', 0),
	(583, 'report', 'READ_ClientTrendsByMonth', 'ClientTrendsByMonth', 'READ', 0),
	(584, 'report', 'READ_ClientTrendsByWeek', 'ClientTrendsByWeek', 'READ', 0),
	(585, 'report', 'READ_Demand_Vs_Collection', 'Demand_Vs_Collection', 'READ', 0),
	(586, 'report', 'READ_Disbursal_Vs_Awaitingdisbursal', 'Disbursal_Vs_Awaitingdisbursal', 'READ', 0),
	(587, 'report', 'READ_Expected Payments By Date - Basic(Pentaho)', 'Expected Payments By Date - Basic(Pentaho)', 'READ', 0),
	(588, 'report', 'READ_Funds Disbursed Between Dates Summary by Office(Pentaho)', 'Funds Disbursed Between Dates Summary by Office(Pentaho)', 'READ', 0),
	(589, 'report', 'READ_Funds Disbursed Between Dates Summary(Pentaho)', 'Funds Disbursed Between Dates Summary(Pentaho)', 'READ', 0),
	(590, 'report', 'READ_GroupNamesByStaff', 'GroupNamesByStaff', 'READ', 0),
	(591, 'report', 'READ_GroupSavingSummary', 'GroupSavingSummary', 'READ', 0),
	(592, 'report', 'READ_LoanCyclePerProduct', 'LoanCyclePerProduct', 'READ', 0),
	(593, 'report', 'READ_Loans Awaiting Disbursal Summary by Month(Pentaho)', 'Loans Awaiting Disbursal Summary by Month(Pentaho)', 'READ', 0),
	(594, 'report', 'READ_Loans Awaiting Disbursal Summary(Pentaho)', 'Loans Awaiting Disbursal Summary(Pentaho)', 'READ', 0),
	(595, 'report', 'READ_Loans Awaiting Disbursal(Pentaho)', 'Loans Awaiting Disbursal(Pentaho)', 'READ', 0),
	(596, 'report', 'READ_Loans Pending Approval(Pentaho)', 'Loans Pending Approval(Pentaho)', 'READ', 0),
	(597, 'report', 'READ_LoanTrendsByDay', 'LoanTrendsByDay', 'READ', 0),
	(598, 'report', 'READ_LoanTrendsByMonth', 'LoanTrendsByMonth', 'READ', 0),
	(599, 'report', 'READ_LoanTrendsByWeek', 'LoanTrendsByWeek', 'READ', 0),
	(600, 'report', 'READ_Obligation Met Loans Details(Pentaho)', 'Obligation Met Loans Details(Pentaho)', 'READ', 0),
	(601, 'report', 'READ_Obligation Met Loans Summary(Pentaho)', 'Obligation Met Loans Summary(Pentaho)', 'READ', 0),
	(602, 'report', 'READ_Portfolio at Risk by Branch(Pentaho)', 'Portfolio at Risk by Branch(Pentaho)', 'READ', 0),
	(603, 'report', 'READ_Portfolio at Risk(Pentaho)', 'Portfolio at Risk(Pentaho)', 'READ', 0),
	(604, 'report', 'READ_Rescheduled Loans(Pentaho)', 'Rescheduled Loans(Pentaho)', 'READ', 0),
	(605, 'report', 'READ_Savings Transactions', 'Savings Transactions', 'READ', 0),
	(606, 'report', 'READ_TxnRunningBalances(Pentaho)', 'TxnRunningBalances(Pentaho)', 'READ', 0),
	(607, 'report', 'READ_Written-Off Loans(Pentaho)', 'Written-Off Loans(Pentaho)', 'READ', 0),
	(608, 'configuration', 'CREATE_ACCOUNTNUMBERFORMAT', 'ACCOUNTNUMBERFORMAT', 'CREATE', 0),
	(609, 'configuration', 'READ_ACCOUNTNUMBERFORMAT', 'ACCOUNTNUMBERFORMAT', 'READ', 0),
	(610, 'configuration', 'UPDATE_ACCOUNTNUMBERFORMAT', 'ACCOUNTNUMBERFORMAT', 'UPDATE', 0),
	(611, 'configuration', 'DELETE_ACCOUNTNUMBERFORMAT', 'HOOK', 'DELETE', 0),
	(612, 'portfolio', 'RECOVERGUARANTEES_LOAN', 'LOAN', 'RECOVERGUARANTEES', 0),
	(613, 'portfolio', 'RECOVERGUARANTEES_LOAN_CHECKER', 'LOAN', 'RECOVERGUARANTEES_CHECKER', 0),
	(614, 'portfolio', 'REJECT_CLIENT', 'CLIENT', 'REJECT', 0),
	(615, 'portfolio', 'REJECT_CLIENT_CHECKER', 'CLIENT', 'REJECT_CHECKER', 0),
	(616, 'portfolio', 'WITHDRAW_CLIENT', 'CLIENT', 'WITHDRAW', 0),
	(617, 'portfolio', 'WITHDRAW_CLIENT_CHECKER', 'CLIENT', 'WITHDRAW_CHECKER', 0),
	(618, 'portfolio', 'REACTIVATE_CLIENT', 'CLIENT', 'REACTIVATE', 0),
	(619, 'portfolio', 'REACTIVATE_CLIENT_CHECKER', 'CLIENT', 'REACTIVATE_CHECKER', 0),
	(620, 'transaction_savings', 'UPDATEDEPOSITAMOUNT_RECURRINGDEPOSITACCOUNT', 'RECURRINGDEPOSITACCOUNT', 'UPDATEDEPOSITAMOUNT', 0),
	(621, 'transaction_savings', 'UPDATEDEPOSITAMOUNT_RECURRINGDEPOSITACCOUNT_CHECKER', 'RECURRINGDEPOSITACCOUNT', 'UPDATEDEPOSITAMOUNT', 1),
	(622, 'transaction_savings', 'REFUNDBYTRANSFER_ACCOUNTTRANSFER_CHECKER', 'ACCOUNTTRANSFER', 'REFUNDBYTRANSFER', 0),
	(623, 'transaction_savings', 'REFUNDBYTRANSFER_ACCOUNTTRANSFER', 'ACCOUNTTRANSFER', 'REFUNDBYTRANSFER', 0),
	(624, 'transaction_loan', 'REFUNDBYCASH_LOAN', 'LOAN', 'REFUNDBYCASH', 0),
	(625, 'transaction_loan', 'REFUNDBYCASH_LOAN_CHECKER', 'LOAN', 'REFUNDBYCASH', 0),
	(626, 'cash_mgmt', 'CREATE_TELLER', 'TELLER', 'CREATE', 0),
	(627, 'cash_mgmt', 'UPDATE_TELLER', 'TELLER', 'UPDATE', 0),
	(628, 'cash_mgmt', 'ALLOCATECASHIER_TELLER', 'TELLER', 'ALLOCATE', 0),
	(629, 'cash_mgmt', 'UPDATECASHIERALLOCATION_TELLER', 'TELLER', 'UPDATECASHIERALLOCATION', 0),
	(630, 'cash_mgmt', 'DELETECASHIERALLOCATION_TELLER', 'TELLER', 'DELETECASHIERALLOCATION', 0),
	(631, 'cash_mgmt', 'ALLOCATECASHTOCASHIER_TELLER', 'TELLER', 'ALLOCATECASHTOCASHIER', 0),
	(632, 'cash_mgmt', 'SETTLECASHFROMCASHIER_TELLER', 'TELLER', 'SETTLECASHFROMCASHIER', 0),
	(633, 'authorisation', 'DISABLE_ROLE', 'ROLE', 'DISABLE', 0),
	(634, 'authorisation', 'DISABLE_ROLE_CHECKER', 'ROLE', 'DISABLE_CHECKER', 0),
	(635, 'authorisation', 'ENABLE_ROLE', 'ROLE', 'ENABLE', 0),
	(636, 'authorisation', 'ENABLE_ROLE_CHECKER', 'ROLE', 'ENABLE_CHECKER', 0),
	(637, 'accounting', 'DEFINEOPENINGBALANCE_JOURNALENTRY', 'JOURNALENTRY', 'DEFINEOPENINGBALANCE', 0),
	(638, 'collection_sheet', 'READ_COLLECTIONSHEET', 'COLLECTIONSHEET', 'READ', 0),
	(639, 'collection_sheet', 'SAVE_COLLECTIONSHEET', 'COLLECTIONSHEET', 'SAVE', 0),
	(640, 'infrastructure', 'CREATE_ENTITYMAPPING', 'ENTITYMAPPING', 'CREATE', 0),
	(641, 'infrastructure', 'UPDATE_ENTITYMAPPING', 'ENTITYMAPPING', 'UPDATE', 0),
	(642, 'infrastructure', 'DELETE_ENTITYMAPPING', 'ENTITYMAPPING', 'DELETE', 0),
	(643, 'organisation', 'READ_WORKINGDAYS', 'WORKINGDAYS', 'READ', 0),
	(644, 'organisation', 'UPDATE_WORKINGDAYS', 'WORKINGDAYS', 'UPDATE', 0),
	(645, 'organisation', 'UPDATE_WORKINGDAYS_CHECKER', 'WORKINGDAYS', 'UPDATE_CHECKER', 0),
	(646, 'authorisation', 'READ_PASSWORD_PREFERENCES', 'PASSWORD_PREFERENCES', 'READ', 0),
	(647, 'authorisation', 'UPDATE_PASSWORD_PREFERENCES', 'PASSWORD_PREFERENCES', 'UPDATE', 0),
	(648, 'authorisation', 'UPDATE_PASSWORD_PREFERENCES_CHECKER', 'PASSWORD_PREFERENCES', 'UPDATE_CHECKER', 0),
	(649, 'portfolio', 'CREATE_PAYMENTTYPE', 'PAYMENTTYPE', 'CREATE', 0),
	(650, 'portfolio', 'UPDATE_PAYMENTTYPE', 'PAYMENTTYPE', 'UPDATE', 0),
	(651, 'portfolio', 'DELETE_PAYMENTTYPE', 'PAYMENTTYPE', 'DELETE', 1),
	(652, 'cash_mgmt', 'DELETE_TELLER', 'TELLER', 'DELETE', 0),
	(653, 'report', 'READ_General Ledger Report', 'General Ledger Report', 'READ', 0),
	(654, 'portfolio', 'READ_STAFFIMAGE', 'STAFFIMAGE', 'READ', 0),
	(655, 'portfolio', 'CREATE_STAFFIMAGE', 'STAFFIMAGE', 'CREATE', 1),
	(656, 'portfolio', 'CREATE_STAFFIMAGE_CHECKER', 'STAFFIMAGE', 'CREATE', 0),
	(657, 'portfolio', 'DELETE_STAFFIMAGE', 'STAFFIMAGE', 'DELETE', 1),
	(658, 'portfolio', 'DELETE_STAFFIMAGE_CHECKER', 'STAFFIMAGE', 'DELETE', 0),
	(708, 'datatable', 'CREATE_Banking Details', 'Banking Details', 'CREATE', 0),
	(709, 'datatable', 'CREATE_Banking Details_CHECKER', 'Banking Details', 'CREATE', 0),
	(710, 'datatable', 'READ_Banking Details', 'Banking Details', 'READ', 0),
	(711, 'datatable', 'UPDATE_Banking Details', 'Banking Details', 'UPDATE', 0),
	(712, 'datatable', 'UPDATE_Banking Details_CHECKER', 'Banking Details', 'UPDATE', 0),
	(713, 'datatable', 'DELETE_Banking Details', 'Banking Details', 'DELETE', 1),
	(714, 'datatable', 'DELETE_Banking Details_CHECKER', 'Banking Details', 'DELETE', 0),
	(715, 'datatable', 'CREATE_TVR', 'TVR', 'CREATE', 0),
	(716, 'datatable', 'CREATE_TVR_CHECKER', 'TVR', 'CREATE', 0),
	(717, 'datatable', 'READ_TVR', 'TVR', 'READ', 0),
	(718, 'datatable', 'UPDATE_TVR', 'TVR', 'UPDATE', 0),
	(719, 'datatable', 'UPDATE_TVR_CHECKER', 'TVR', 'UPDATE', 0),
	(720, 'datatable', 'DELETE_TVR', 'TVR', 'DELETE', 1),
	(721, 'datatable', 'DELETE_TVR_CHECKER', 'TVR', 'DELETE', 0),
	(722, 'datatable', 'CREATE_CPV', 'CPV', 'CREATE', 0),
	(723, 'datatable', 'CREATE_CPV_CHECKER', 'CPV', 'CREATE', 0),
	(724, 'datatable', 'READ_CPV', 'CPV', 'READ', 0),
	(725, 'datatable', 'UPDATE_CPV', 'CPV', 'UPDATE', 0),
	(726, 'datatable', 'UPDATE_CPV_CHECKER', 'CPV', 'UPDATE', 0),
	(727, 'datatable', 'DELETE_CPV', 'CPV', 'DELETE', 1),
	(728, 'datatable', 'DELETE_CPV_CHECKER', 'CPV', 'DELETE', 0),
	(729, 'datatable', 'CREATE_Nominee', 'Nominee', 'CREATE', 0),
	(730, 'datatable', 'CREATE_Nominee_CHECKER', 'Nominee', 'CREATE', 0),
	(731, 'datatable', 'READ_Nominee', 'Nominee', 'READ', 0),
	(732, 'datatable', 'UPDATE_Nominee', 'Nominee', 'UPDATE', 0),
	(733, 'datatable', 'UPDATE_Nominee_CHECKER', 'Nominee', 'UPDATE', 0),
	(734, 'datatable', 'DELETE_Nominee', 'Nominee', 'DELETE', 1),
	(735, 'datatable', 'DELETE_Nominee_CHECKER', 'Nominee', 'DELETE', 0),
	(745, 'datatable', 'CREATE_PDC', 'PDC', 'CREATE', 1),
	(746, 'datatable', 'CREATE_PDC_CHECKER', 'PDC', 'CREATE', 0),
	(747, 'datatable', 'READ_PDC', 'PDC', 'READ', 0),
	(748, 'datatable', 'UPDATE_PDC', 'PDC', 'UPDATE', 1),
	(749, 'datatable', 'UPDATE_PDC_CHECKER', 'PDC', 'UPDATE', 0),
	(750, 'datatable', 'DELETE_PDC', 'PDC', 'DELETE', 1),
	(751, 'datatable', 'DELETE_PDC_CHECKER', 'PDC', 'DELETE', 0),
	(752, 'report', 'READ_test1', 'test1', 'READ', 0),
	(753, 'report', 'READ_Active Loan Summary per Branch', 'Active Loan Summary per Branch', 'READ', 0),
	(754, 'report', 'READ_Disbursal Report', 'Disbursal Report', 'READ', 0),
	(755, 'report', 'READ_Balance Outstanding', 'Balance Outstanding', 'READ', 0),
	(756, 'report', 'READ_Collection Report', 'Collection Report', 'READ', 0),
	(757, 'portfolio', 'READ_PAYMENTTYPE', 'PAYMENTTYPE', 'READ', 0),
	(758, 'report', 'READ_Staff Assignment History', 'Staff Assignment History(Pentaho)', 'READ', 0),
	(762, 'organisation', 'READ_DSA', 'DSA', 'READ', 0),
	(763, 'organisation', 'CREATE_DSA', 'DSA', 'CREATE', 0),
	(764, 'organisation', 'CREATE_DSA_CHECKER', 'DSA', 'CREATE_CHECKER', 0),
	(765, 'organisation', 'UPDATE_DSA', 'DSA', 'UPDATE', 0),
	(766, 'organisation', 'UPDATE_DSA_CHECKER', 'DSA', 'UPDATE_CHECKER', 0),
	(767, 'organisation', 'DELETE_DSA', 'DSA', 'DELETE', 1),
	(768, 'organisation', 'DELETE_DSA_CHECKER', 'DSA', 'DELETE_CHECKER', 0),
	(769, 'portfolio', 'READ_CLIENTADDRESSES', 'CLIENTADDRESSES', 'READ', 0),
	(770, 'portfolio', 'CREATE_CLIENTADDRESSES', 'CLIENTADDRESSES', 'CREATE', 0),
	(771, 'portfolio', 'CREATE_CLIENTADDRESSES_CHECKER', 'CLIENTADDRESSES', 'CREATE_CHECKER', 0),
	(772, 'portfolio', 'UPDATE_CLIENTADDRESSES', 'CLIENTADDRESSES', 'UPDATE', 0),
	(773, 'portfolio', 'UPDATE_CLIENTADDRESSES_CHECKER', 'CLIENTADDRESSES', 'UPDATE_CHECKER', 0),
	(774, 'portfolio', 'DELETE_CLIENTADDRESSES', 'CLIENTADDRESSES', 'DELETE', 0),
	(775, 'portfolio', 'DELETE_CLIENTADDRESSES_CHECKER', 'CLIENTADDRESSES', 'DELETE_CHECKER', 0);

/*!40000 ALTER TABLE `m_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_portfolio_account_associations`
--

DROP TABLE IF EXISTS `m_portfolio_account_associations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_portfolio_account_associations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_account_id` bigint(20) DEFAULT NULL,
  `savings_account_id` bigint(20) DEFAULT NULL,
  `linked_loan_account_id` bigint(20) DEFAULT NULL,
  `linked_savings_account_id` bigint(20) DEFAULT NULL,
  `association_type_enum` smallint(1) NOT NULL DEFAULT '1',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `account_association_loan_fk` (`loan_account_id`),
  KEY `account_association_savings_fk` (`savings_account_id`),
  KEY `linked_loan_fk` (`linked_loan_account_id`),
  KEY `linked_savings_fk` (`linked_savings_account_id`),
  CONSTRAINT `account_association_loan_fk` FOREIGN KEY (`loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `account_association_savings_fk` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `linked_loan_fk` FOREIGN KEY (`linked_loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `linked_savings_fk` FOREIGN KEY (`linked_savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_portfolio_account_associations`
--

LOCK TABLES `m_portfolio_account_associations` WRITE;
/*!40000 ALTER TABLE `m_portfolio_account_associations` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_portfolio_account_associations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_portfolio_command_source`
--

DROP TABLE IF EXISTS `m_portfolio_command_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_portfolio_command_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_name` varchar(50) NOT NULL,
  `entity_name` varchar(50) NOT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `loan_id` bigint(20) DEFAULT NULL,
  `savings_account_id` bigint(20) DEFAULT NULL,
  `api_get_url` varchar(100) NOT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  `subresource_id` bigint(20) DEFAULT NULL,
  `command_as_json` text NOT NULL,
  `maker_id` bigint(20) NOT NULL,
  `made_on_date` datetime NOT NULL,
  `checker_id` bigint(20) DEFAULT NULL,
  `checked_on_date` datetime DEFAULT NULL,
  `processing_result_enum` smallint(5) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_maker_m_appuser` (`maker_id`),
  KEY `FK_m_checker_m_appuser` (`checker_id`),
  KEY `action_name` (`action_name`),
  KEY `entity_name` (`entity_name`,`resource_id`),
  KEY `made_on_date` (`made_on_date`),
  KEY `checked_on_date` (`checked_on_date`),
  KEY `processing_result_enum` (`processing_result_enum`),
  KEY `office_id` (`office_id`),
  KEY `group_id` (`office_id`),
  KEY `client_id` (`office_id`),
  KEY `loan_id` (`office_id`),
  CONSTRAINT `FK_m_checker_m_appuser` FOREIGN KEY (`checker_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_m_maker_m_appuser` FOREIGN KEY (`maker_id`) REFERENCES `m_appuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_portfolio_command_source`
--

LOCK TABLES `m_portfolio_command_source` WRITE;
/*!40000 ALTER TABLE `m_portfolio_command_source` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_portfolio_command_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan`
--

DROP TABLE IF EXISTS `m_product_loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `short_name` varchar(4) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `principal_amount` decimal(19,6) DEFAULT NULL,
  `min_principal_amount` decimal(19,6) DEFAULT NULL,
  `max_principal_amount` decimal(19,6) DEFAULT NULL,
  `arrearstolerance_amount` decimal(19,6) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `fund_id` bigint(20) DEFAULT NULL,
  `nominal_interest_rate_per_period` decimal(19,6) NOT NULL,
  `min_nominal_interest_rate_per_period` decimal(19,6) DEFAULT NULL,
  `max_nominal_interest_rate_per_period` decimal(19,6) DEFAULT NULL,
  `interest_period_frequency_enum` smallint(5) NOT NULL,
  `annual_nominal_interest_rate` decimal(19,6) NOT NULL,
  `interest_method_enum` smallint(5) NOT NULL,
  `interest_calculated_in_period_enum` smallint(5) NOT NULL DEFAULT '1',
  `repay_every` smallint(5) NOT NULL,
  `repayment_period_frequency_enum` smallint(5) NOT NULL,
  `number_of_repayments` smallint(5) NOT NULL,
  `min_number_of_repayments` smallint(5) DEFAULT NULL,
  `max_number_of_repayments` smallint(5) DEFAULT NULL,
  `grace_on_principal_periods` smallint(5) DEFAULT NULL,
  `grace_on_interest_periods` smallint(5) DEFAULT NULL,
  `grace_interest_free_periods` smallint(5) DEFAULT NULL,
  `amortization_method_enum` smallint(5) NOT NULL,
  `accounting_type` smallint(5) NOT NULL,
  `loan_transaction_strategy_id` bigint(20) DEFAULT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `include_in_borrower_cycle` tinyint(1) NOT NULL DEFAULT '0',
  `use_borrower_cycle` tinyint(1) NOT NULL DEFAULT '0',
  `start_date` date DEFAULT NULL,
  `close_date` date DEFAULT NULL,
  `allow_multiple_disbursals` tinyint(1) NOT NULL DEFAULT '0',
  `max_disbursals` int(2) DEFAULT NULL,
  `max_outstanding_loan_balance` decimal(19,6) DEFAULT NULL,
  `grace_on_arrears_ageing` smallint(5) DEFAULT NULL,
  `overdue_days_for_npa` smallint(5) DEFAULT NULL,
  `days_in_month_enum` smallint(5) NOT NULL DEFAULT '1',
  `days_in_year_enum` smallint(5) NOT NULL DEFAULT '1',
  `interest_recalculation_enabled` tinyint(4) NOT NULL DEFAULT '0',
  `min_days_between_disbursal_and_first_repayment` int(3) DEFAULT NULL,
  `hold_guarantee_funds` tinyint(1) NOT NULL DEFAULT '0',
  `principal_threshold_for_last_installment` decimal(5,2) NOT NULL DEFAULT '50.00',
  `account_moves_out_of_npa_only_on_arrears_completion` tinyint(1) NOT NULL DEFAULT '0',
  `can_define_fixed_emi_amount` tinyint(1) NOT NULL DEFAULT '0',
  `instalment_amount_in_multiples_of` decimal(19,6) DEFAULT NULL,
  `marked_interest` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`),
  UNIQUE KEY `unq_short_name` (`short_name`),
  UNIQUE KEY `external_id_UNIQUE` (`external_id`),
  KEY `FKA6A8A7D77240145` (`fund_id`),
  KEY `FK_ltp_strategy` (`loan_transaction_strategy_id`),
  CONSTRAINT `FKA6A8A7D77240145` FOREIGN KEY (`fund_id`) REFERENCES `m_fund` (`id`),
  CONSTRAINT `FK_ltp_strategy` FOREIGN KEY (`loan_transaction_strategy_id`) REFERENCES `ref_loan_transaction_processing_strategy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan`
--

LOCK TABLES `m_product_loan` WRITE;
/*!40000 ALTER TABLE `m_product_loan` DISABLE KEYS */;
INSERT INTO `m_product_loan` VALUES (34, 'GEMI', 'INR', 0, 0, 100000.000000, 10000.000000, 150000.000000, NULL, 'Gold EMI final', NULL, 2, 26.620000, 24.000000, 28.000000, 3, 26.620000, 0, 1, 1, 2, 12, 3, 24, 0, 0, 0, 1, 1, 6, NULL, 1, 0, NULL, NULL, 0, NULL, NULL, 5, NULL, 1, 1, 1, NULL, 0, 0.00, 0, 1, NULL, 0.000000),
(36, 'EMIT', 'INR', 0, 0, 50000.000000, 10000.000000, 100000.000000, NULL, 'TW EMI', NULL, NULL, 26.890000, 24.000000, 28.000000, 3, 26.890000, 0, 1, 1, 2, 12, 3, 36, NULL, NULL, NULL, 1, 1, 6, NULL, 1, 0, NULL, NULL, 0, NULL, NULL, 5, 90, 1, 1, 1, NULL, 0, 0.00, 0, 0, NULL, 0.000000),
(37, 'GTL1', 'INR', 0, 0, 50000.000000, 10000.000000, 100000.000000, NULL, 'Gold Term Loan 1', 'gold term loan', NULL, 24.000000, 22.000000, 26.000000, 3, 24.000000, 0, 1, 30, 0, 1, 1, 1, NULL, NULL, NULL, 1, 1, 6, NULL, 0, 0, NULL, NULL, 0, NULL, NULL, 5, NULL, 1, 360, 1, NULL, 0, 0.00, 0, 0, NULL, 12.000000),
(38, 'GTL2', 'INR', 0, 0, 50000.000000, 10000.000000, 100000.000000, NULL, 'Gold Term Loan 2', NULL, NULL, 24.000000, 24.000000, 24.000000, 3, 24.000000, 0, 1, 90, 0, 1, 1, 1, NULL, NULL, NULL, 1, 1, 6, NULL, 1, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, 1, 1, 1, NULL, 0, 0.00, 0, 0, NULL, 0.000000),
(39, 'GTL3', 'INR', 0, 0, 50000.000000, 10000.000000, 100000.000000, NULL, 'Gold Term Loan 3', NULL, 2, 2.000000, 2.000000, 2.000000, 2, 24.000000, 0, 1, 1, 2, 1, 1, 1, NULL, NULL, NULL, 1, 1, 6, NULL, 1, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, 30, 360, 1, NULL, 0, 0.00, 0, 0, NULL, 0.000000);
/*!40000 ALTER TABLE `m_product_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan_charge`
--

DROP TABLE IF EXISTS `m_product_loan_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan_charge` (
  `product_loan_id` bigint(20) NOT NULL,
  `charge_id` bigint(20) NOT NULL,
  PRIMARY KEY (`product_loan_id`,`charge_id`),
  KEY `charge_id` (`charge_id`),
  CONSTRAINT `m_product_loan_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  CONSTRAINT `m_product_loan_charge_ibfk_2` FOREIGN KEY (`product_loan_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan_charge`
--

LOCK TABLES `m_product_loan_charge` WRITE;
/*!40000 ALTER TABLE `m_product_loan_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_loan_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan_configurable_attributes`
--

DROP TABLE IF EXISTS `m_product_loan_configurable_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan_configurable_attributes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_product_id` bigint(20) NOT NULL,
  `amortization_method_enum` tinyint(4) NOT NULL DEFAULT '1',
  `interest_method_enum` tinyint(4) NOT NULL DEFAULT '1',
  `loan_transaction_strategy_id` tinyint(4) NOT NULL DEFAULT '1',
  `interest_calculated_in_period_enum` tinyint(4) NOT NULL DEFAULT '1',
  `arrearstolerance_amount` tinyint(4) NOT NULL DEFAULT '1',
  `repay_every` tinyint(4) NOT NULL DEFAULT '1',
  `moratorium` tinyint(4) NOT NULL DEFAULT '1',
  `grace_on_arrears_ageing` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_m_product_loan_configurable_attributes_0001` (`loan_product_id`),
  CONSTRAINT `fk_m_product_loan_configurable_attributes_0001` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan_configurable_attributes`
--

LOCK TABLES `m_product_loan_configurable_attributes` WRITE;
/*!40000 ALTER TABLE `m_product_loan_configurable_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_loan_configurable_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan_guarantee_details`
--

DROP TABLE IF EXISTS `m_product_loan_guarantee_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan_guarantee_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_product_id` bigint(20) NOT NULL,
  `mandatory_guarantee` decimal(19,5) NOT NULL,
  `minimum_guarantee_from_own_funds` decimal(19,5) DEFAULT NULL,
  `minimum_guarantee_from_guarantor_funds` decimal(19,5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_guarantee_details_loan_product` (`loan_product_id`),
  CONSTRAINT `FK_guarantee_details_loan_product` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan_guarantee_details`
--

LOCK TABLES `m_product_loan_guarantee_details` WRITE;
/*!40000 ALTER TABLE `m_product_loan_guarantee_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_loan_guarantee_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan_recalculation_details`
--

DROP TABLE IF EXISTS `m_product_loan_recalculation_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan_recalculation_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL,
  `compound_type_enum` smallint(5) NOT NULL,
  `reschedule_strategy_enum` smallint(5) NOT NULL,
  `rest_frequency_type_enum` smallint(1) NOT NULL,
  `rest_frequency_interval` smallint(3) NOT NULL DEFAULT '0',
  `rest_freqency_date` date DEFAULT NULL,
  `arrears_based_on_original_schedule` tinyint(1) NOT NULL DEFAULT '0',
  `pre_close_interest_calculation_strategy` smallint(3) NOT NULL DEFAULT '1',
  `compounding_frequency_type_enum` smallint(1) DEFAULT NULL,
  `compounding_frequency_interval` smallint(3) DEFAULT NULL,
  `compounding_freqency_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_product_loan_m_product_loan_recalculation_details` (`product_id`),
  CONSTRAINT `FK_m_product_loan_m_product_loan_recalculation_details` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan_recalculation_details`
--

LOCK TABLES `m_product_loan_recalculation_details` WRITE;
/*!40000 ALTER TABLE `m_product_loan_recalculation_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_loan_recalculation_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_loan_variations_borrower_cycle`
--

DROP TABLE IF EXISTS `m_product_loan_variations_borrower_cycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_loan_variations_borrower_cycle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_product_id` bigint(20) NOT NULL DEFAULT '0',
  `borrower_cycle_number` int(3) NOT NULL DEFAULT '0',
  `value_condition` int(1) NOT NULL DEFAULT '0',
  `param_type` int(1) NOT NULL DEFAULT '0',
  `default_value` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `max_value` decimal(19,6) DEFAULT NULL,
  `min_value` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `borrower_cycle_loan_product_FK` (`loan_product_id`),
  CONSTRAINT `borrower_cycle_loan_product_FK` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_loan_variations_borrower_cycle`
--

LOCK TABLES `m_product_loan_variations_borrower_cycle` WRITE;
/*!40000 ALTER TABLE `m_product_loan_variations_borrower_cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_loan_variations_borrower_cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_product_mix`
--

DROP TABLE IF EXISTS `m_product_mix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_product_mix` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL,
  `restricted_product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_product_mix_product_id_to_m_product_loan` (`product_id`),
  KEY `FK_m_product_mix_restricted_product_id_to_m_product_loan` (`restricted_product_id`),
  CONSTRAINT `FK_m_product_mix_product_id_to_m_product_loan` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`),
  CONSTRAINT `FK_m_product_mix_restricted_product_id_to_m_product_loan` FOREIGN KEY (`restricted_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_product_mix`
--

LOCK TABLES `m_product_mix` WRITE;
/*!40000 ALTER TABLE `m_product_mix` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_product_mix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_role`
--

DROP TABLE IF EXISTS `m_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  `is_disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_role`
--

LOCK TABLES `m_role` WRITE;
/*!40000 ALTER TABLE `m_role` DISABLE KEYS */;
INSERT INTO `m_role` VALUES INSERT INTO `m_role` (`id`, `name`, `description`, `is_disabled`) VALUES
(1, 'Super user', 'This role provides all application permissions.', 0),
(8, 'Level 1', 'client data entry,loan application,transactions,loan collection, repayment, interface between NBFC and client', 0),
(9, 'Verification', 'verification of data entry by level 1', 1),
(10, 'Approval', 'Level 2 approval of case by going through client and loan application and verification reports', 0),
(11, 'Disbursal authority', 'disbursal department', 0),
(12, 'Auditing and reporting', 'for audit purposes both internal as well as external', 0),
(13, 'Reporting', 'Reporting for sales purposes', 0),
(15, 'Operations', 'admin tasks such as holidays, officials etc, loan products configurations etc.', 0),
(16, 'Disbursal and reporting', 'disbursal and reports', 0),
(17, 'Accounting and Reporting', 'accounting and reporting', 0);
/*!40000 ALTER TABLE `m_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_role_permission`
--

DROP TABLE IF EXISTS `m_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_role_permission` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `FK8DEDB04815CEC7AB` (`role_id`),
  KEY `FK8DEDB048103B544B` (`permission_id`),
  CONSTRAINT `FK8DEDB048103B544B` FOREIGN KEY (`permission_id`) REFERENCES `m_permission` (`id`),
  CONSTRAINT `FK8DEDB04815CEC7AB` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_role_permission`
--

LOCK TABLES `m_role_permission` WRITE;
/*!40000 ALTER TABLE `m_role_permission` DISABLE KEYS */;
INSERT INTO `m_role_permission` VALUES (1, 1),
(8, 71),
(8, 103),
(8, 104),
(8, 106),
(8, 108),
(8, 110),
(8, 111),
(8, 113),
(8, 115),
(8, 117),
(8, 118),
(8, 120),
(8, 122),
(8, 123),
(8, 125),
(8, 127),
(8, 136),
(8, 137),
(8, 138),
(8, 139),
(8, 143),
(8, 144),
(8, 145),
(8, 146),
(8, 157),
(8, 158),
(8, 160),
(8, 162),
(8, 164),
(8, 165),
(8, 167),
(8, 169),
(8, 180),
(8, 182),
(8, 184),
(8, 186),
(8, 195),
(8, 196),
(8, 198),
(8, 200),
(8, 202),
(8, 203),
(8, 204),
(8, 205),
(8, 213),
(8, 215),
(8, 218),
(8, 219),
(8, 222),
(8, 223),
(8, 224),
(8, 312),
(8, 347),
(8, 349),
(8, 389),
(8, 391),
(8, 395),
(8, 402),
(8, 413),
(8, 437),
(8, 557),
(8, 558),
(8, 614),
(8, 616),
(8, 618),
(8, 638),
(8, 663),
(8, 665),
(8, 667),
(8, 668),
(8, 670),
(8, 672),
(8, 674),
(8, 675),
(8, 677),
(8, 679),
(8, 681),
(8, 682),
(8, 684),
(8, 686),
(8, 688),
(8, 689),
(8, 691),
(8, 693),
(8, 695),
(8, 696),
(8, 698),
(8, 700),
(8, 702),
(8, 703),
(8, 705),
(8, 707),
(8, 709),
(8, 710),
(8, 712),
(8, 714),
(8, 716),
(8, 717),
(8, 719),
(10, 9),
(10, 48),
(10, 50),
(10, 57),
(10, 64),
(10, 71),
(10, 103),
(10, 104),
(10, 106),
(10, 108),
(10, 109),
(10, 110),
(10, 111),
(10, 113),
(10, 114),
(10, 115),
(10, 116),
(10, 117),
(10, 118),
(10, 120),
(10, 121),
(10, 122),
(10, 123),
(10, 125),
(10, 127),
(10, 128),
(10, 136),
(10, 137),
(10, 138),
(10, 139),
(10, 142),
(10, 143),
(10, 144),
(10, 145),
(10, 146),
(10, 149),
(10, 157),
(10, 158),
(10, 160),
(10, 162),
(10, 163),
(10, 164),
(10, 165),
(10, 167),
(10, 169),
(10, 170),
(10, 180),
(10, 182),
(10, 184),
(10, 185),
(10, 186),
(10, 187),
(10, 195),
(10, 196),
(10, 198),
(10, 200),
(10, 201),
(10, 202),
(10, 203),
(10, 204),
(10, 205),
(10, 208),
(10, 209),
(10, 211),
(10, 213),
(10, 215),
(10, 216),
(10, 218),
(10, 219),
(10, 221),
(10, 222),
(10, 223),
(10, 224),
(10, 226),
(10, 227),
(10, 228),
(10, 229),
(10, 230),
(10, 234),
(10, 236),
(10, 242),
(10, 244),
(10, 245),
(10, 246),
(10, 247),
(10, 305),
(10, 312),
(10, 347),
(10, 349),
(10, 389),
(10, 390),
(10, 391),
(10, 392),
(10, 393),
(10, 395),
(10, 396),
(10, 402),
(10, 413),
(10, 414),
(10, 418),
(10, 434),
(10, 437),
(10, 443),
(10, 448),
(10, 454),
(10, 541),
(10, 557),
(10, 558),
(10, 559),
(10, 560),
(10, 614),
(10, 615),
(10, 616),
(10, 617),
(10, 618),
(10, 619),
(10, 638),
(10, 654),
(10, 663),
(10, 665),
(10, 667),
(10, 668),
(10, 670),
(10, 671),
(10, 672),
(10, 674),
(10, 675),
(10, 677),
(10, 678),
(10, 679),
(10, 681),
(10, 682),
(10, 684),
(10, 685),
(10, 686),
(10, 688),
(10, 689),
(10, 691),
(10, 692),
(10, 693),
(10, 695),
(10, 696),
(10, 698),
(10, 699),
(10, 700),
(10, 702),
(10, 703),
(10, 705),
(10, 706),
(10, 707),
(10, 709),
(10, 710),
(10, 712),
(10, 713),
(10, 714),
(10, 716),
(10, 717),
(10, 719),
(10, 720),
(12, 2),
(12, 4),
(15, 2),
(15, 4),
(15, 5),
(15, 6),
(15, 7),
(15, 8),
(15, 9),
(15, 10),
(15, 11),
(15, 12),
(15, 13),
(15, 14),
(15, 15),
(15, 16),
(15, 17),
(15, 18),
(15, 19),
(15, 20),
(15, 21),
(15, 22),
(15, 23),
(15, 24),
(15, 25),
(15, 26),
(15, 27),
(15, 28),
(15, 29),
(15, 30),
(15, 31),
(15, 32),
(15, 33),
(15, 34),
(15, 35),
(15, 36),
(15, 37),
(15, 38),
(15, 48),
(15, 49),
(15, 50),
(15, 51),
(15, 52),
(15, 53),
(15, 54),
(15, 55),
(15, 57),
(15, 58),
(15, 59),
(15, 60),
(15, 61),
(15, 62),
(15, 63),
(15, 64),
(15, 65),
(15, 66),
(15, 67),
(15, 68),
(15, 69),
(15, 70),
(15, 71),
(15, 72),
(15, 73),
(15, 74),
(15, 75),
(15, 76),
(15, 77),
(15, 78),
(15, 79),
(15, 80),
(15, 81),
(15, 84),
(15, 89),
(15, 90),
(15, 91),
(15, 92),
(15, 93),
(15, 94),
(15, 95),
(15, 103),
(15, 106),
(15, 108),
(15, 109),
(15, 110),
(15, 113),
(15, 114),
(15, 115),
(15, 116),
(15, 117),
(15, 120),
(15, 121),
(15, 122),
(15, 125),
(15, 126),
(15, 127),
(15, 128),
(15, 136),
(15, 138),
(15, 141),
(15, 143),
(15, 145),
(15, 148),
(15, 157),
(15, 160),
(15, 161),
(15, 162),
(15, 163),
(15, 164),
(15, 167),
(15, 169),
(15, 170),
(15, 182),
(15, 183),
(15, 184),
(15, 185),
(15, 187),
(15, 195),
(15, 198),
(15, 199),
(15, 200),
(15, 201),
(15, 202),
(15, 204),
(15, 205),
(15, 208),
(15, 215),
(15, 216),
(15, 218),
(15, 221),
(15, 224),
(15, 225),
(15, 227),
(15, 229),
(15, 230),
(15, 231),
(15, 234),
(15, 236),
(15, 238),
(15, 239),
(15, 241),
(15, 244),
(15, 245),
(15, 246),
(15, 247),
(15, 248),
(15, 265),
(15, 266),
(15, 267),
(15, 268),
(15, 269),
(15, 270),
(15, 271),
(15, 272),
(15, 273),
(15, 274),
(15, 275),
(15, 276),
(15, 277),
(15, 278),
(15, 279),
(15, 280),
(15, 281),
(15, 282),
(15, 283),
(15, 284),
(15, 285),
(15, 286),
(15, 287),
(15, 288),
(15, 289),
(15, 290),
(15, 291),
(15, 292),
(15, 293),
(15, 294),
(15, 295),
(15, 305),
(15, 306),
(15, 307),
(15, 308),
(15, 309),
(15, 310),
(15, 311),
(15, 313),
(15, 326),
(15, 327),
(15, 334),
(15, 346),
(15, 347),
(15, 348),
(15, 349),
(15, 350),
(15, 351),
(15, 352),
(15, 353),
(15, 354),
(15, 355),
(15, 356),
(15, 357),
(15, 370),
(15, 371),
(15, 372),
(15, 373),
(15, 389),
(15, 390),
(15, 391),
(15, 392),
(15, 393),
(15, 394),
(15, 413),
(15, 414),
(15, 415),
(15, 416),
(15, 417),
(15, 418),
(15, 420),
(15, 421),
(15, 422),
(15, 423),
(15, 424),
(15, 425),
(15, 426),
(15, 427),
(15, 428),
(15, 429),
(15, 430),
(15, 431),
(15, 432),
(15, 433),
(15, 434),
(15, 437),
(15, 443),
(15, 444),
(15, 445),
(15, 447),
(15, 540),
(15, 541),
(15, 543),
(15, 548),
(15, 557),
(15, 559),
(15, 560),
(15, 567),
(15, 568),
(15, 569),
(15, 570),
(15, 571),
(15, 572),
(15, 573),
(15, 574),
(15, 575),
(15, 576),
(15, 577),
(15, 578),
(15, 579),
(15, 580),
(15, 581),
(15, 582),
(15, 583),
(15, 584),
(15, 585),
(15, 586),
(15, 587),
(15, 588),
(15, 589),
(15, 590),
(15, 591),
(15, 592),
(15, 593),
(15, 594),
(15, 595),
(15, 596),
(15, 597),
(15, 598),
(15, 599),
(15, 600),
(15, 601),
(15, 602),
(15, 603),
(15, 604),
(15, 605),
(15, 606),
(15, 607),
(15, 609),
(15, 614),
(15, 615),
(15, 616),
(15, 617),
(15, 618),
(15, 619),
(15, 624),
(15, 625),
(15, 626),
(15, 627),
(15, 628),
(15, 629),
(15, 630),
(15, 631),
(15, 632),
(15, 633),
(15, 634),
(15, 635),
(15, 636),
(15, 638),
(15, 639),
(15, 640),
(15, 641),
(15, 642),
(15, 643),
(15, 644),
(15, 645),
(15, 646),
(15, 647),
(15, 648),
(15, 649),
(15, 650),
(15, 651),
(15, 652),
(15, 653),
(15, 659),
(15, 660),
(15, 661),
(15, 662),
(15, 663),
(15, 664),
(15, 667),
(15, 668),
(15, 669),
(15, 670),
(15, 671),
(15, 674),
(15, 675),
(15, 676),
(15, 677),
(15, 678),
(15, 681),
(15, 682),
(15, 683),
(15, 684),
(15, 685),
(15, 688),
(15, 689),
(15, 690),
(15, 691),
(15, 692),
(15, 695),
(15, 696),
(15, 697),
(15, 698),
(15, 699),
(15, 702),
(15, 703),
(15, 704),
(15, 705),
(15, 706),
(15, 709),
(15, 710),
(15, 711),
(15, 712),
(15, 713),
(15, 716),
(15, 717),
(15, 718),
(15, 719),
(15, 720),
(16, 2),
(16, 4),
(16, 209),
(16, 211),
(16, 213),
(16, 215),
(16, 216),
(16, 218),
(16, 219),
(16, 221),
(16, 222),
(16, 223),
(16, 224),
(16, 225),
(16, 226),
(16, 227),
(16, 228),
(16, 229),
(16, 230),
(16, 231),
(16, 232),
(16, 234),
(16, 236),
(16, 238),
(16, 239),
(16, 241),
(16, 242),
(16, 244),
(16, 245),
(16, 246),
(16, 247),
(16, 248),
(16, 265),
(16, 266),
(16, 267),
(16, 268),
(16, 269),
(16, 270),
(16, 271),
(16, 272),
(16, 273),
(16, 274),
(16, 275),
(16, 276),
(16, 277),
(16, 278),
(16, 279),
(16, 280),
(16, 281),
(16, 282),
(16, 283),
(16, 284),
(16, 285),
(16, 286),
(16, 287),
(16, 288),
(16, 289),
(16, 290),
(16, 291),
(16, 292),
(16, 293),
(16, 294),
(16, 295),
(16, 326),
(16, 327),
(16, 346),
(16, 350),
(16, 351),
(16, 352),
(16, 353),
(16, 354),
(16, 355),
(16, 356),
(16, 357),
(16, 402),
(16, 434),
(16, 540),
(16, 541),
(16, 567),
(16, 568),
(16, 569),
(16, 570),
(16, 571),
(16, 572),
(16, 573),
(16, 574),
(16, 575),
(16, 576),
(16, 577),
(16, 578),
(16, 579),
(16, 580),
(16, 581),
(16, 582),
(16, 583),
(16, 584),
(16, 585),
(16, 586),
(16, 587),
(16, 588),
(16, 589),
(16, 590),
(16, 591),
(16, 592),
(16, 593),
(16, 594),
(16, 595),
(16, 596),
(16, 597),
(16, 598),
(16, 599),
(16, 600),
(16, 601),
(16, 602),
(16, 603),
(16, 604),
(16, 605),
(16, 606),
(16, 607),
(16, 624),
(16, 625),
(16, 638),
(16, 639),
(16, 653),
(16, 659),
(16, 660),
(16, 661),
(16, 662),
(16, 664);
/*!40000 ALTER TABLE `m_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account`
--

DROP TABLE IF EXISTS `m_savings_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(20) NOT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `field_officer_id` bigint(20) DEFAULT NULL,
  `status_enum` smallint(5) NOT NULL DEFAULT '300',
  `account_type_enum` smallint(5) NOT NULL DEFAULT '1',
  `deposit_type_enum` smallint(5) NOT NULL DEFAULT '100',
  `submittedon_date` date NOT NULL,
  `submittedon_userid` bigint(20) DEFAULT NULL,
  `approvedon_date` date DEFAULT NULL,
  `approvedon_userid` bigint(20) DEFAULT NULL,
  `rejectedon_date` date DEFAULT NULL,
  `rejectedon_userid` bigint(20) DEFAULT NULL,
  `withdrawnon_date` date DEFAULT NULL,
  `withdrawnon_userid` bigint(20) DEFAULT NULL,
  `activatedon_date` date DEFAULT NULL,
  `activatedon_userid` bigint(20) DEFAULT NULL,
  `closedon_date` date DEFAULT NULL,
  `closedon_userid` bigint(20) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `nominal_annual_interest_rate` decimal(19,6) NOT NULL,
  `interest_compounding_period_enum` smallint(5) NOT NULL,
  `interest_posting_period_enum` smallint(5) NOT NULL DEFAULT '4',
  `interest_calculation_type_enum` smallint(5) NOT NULL,
  `interest_calculation_days_in_year_type_enum` smallint(5) NOT NULL,
  `min_required_opening_balance` decimal(19,6) DEFAULT NULL,
  `lockin_period_frequency` decimal(19,6) DEFAULT NULL,
  `lockin_period_frequency_enum` smallint(5) DEFAULT NULL,
  `withdrawal_fee_for_transfer` tinyint(4) DEFAULT '1',
  `allow_overdraft` tinyint(1) NOT NULL DEFAULT '0',
  `overdraft_limit` decimal(19,6) DEFAULT NULL,
  `lockedin_until_date_derived` date DEFAULT NULL,
  `total_deposits_derived` decimal(19,6) DEFAULT NULL,
  `total_withdrawals_derived` decimal(19,6) DEFAULT NULL,
  `total_withdrawal_fees_derived` decimal(19,6) DEFAULT NULL,
  `total_fees_charge_derived` decimal(19,6) DEFAULT NULL,
  `total_penalty_charge_derived` decimal(19,6) DEFAULT NULL,
  `total_annual_fees_derived` decimal(19,6) DEFAULT NULL,
  `total_interest_earned_derived` decimal(19,6) DEFAULT NULL,
  `total_interest_posted_derived` decimal(19,6) DEFAULT NULL,
  `account_balance_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `min_required_balance` decimal(19,6) DEFAULT NULL,
  `enforce_min_required_balance` tinyint(1) NOT NULL DEFAULT '0',
  `min_balance_for_interest_calculation` decimal(19,6) DEFAULT NULL,
  `start_interest_calculation_date` date DEFAULT NULL,
  `on_hold_funds_derived` decimal(19,6) DEFAULT NULL,
  `version` int(15) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sa_account_no_UNIQUE` (`account_no`),
  UNIQUE KEY `sa_externalid_UNIQUE` (`external_id`),
  KEY `FKSA00000000000001` (`client_id`),
  KEY `FKSA00000000000002` (`group_id`),
  KEY `FKSA00000000000003` (`product_id`),
  CONSTRAINT `FKSA00000000000001` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKSA00000000000002` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `FKSA00000000000003` FOREIGN KEY (`product_id`) REFERENCES `m_savings_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account`
--

LOCK TABLES `m_savings_account` WRITE;
/*!40000 ALTER TABLE `m_savings_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account_charge`
--

DROP TABLE IF EXISTS `m_savings_account_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL,
  `charge_id` bigint(20) NOT NULL,
  `is_penalty` tinyint(1) NOT NULL DEFAULT '0',
  `charge_time_enum` smallint(5) NOT NULL,
  `charge_due_date` date DEFAULT NULL,
  `fee_on_month` smallint(5) DEFAULT NULL,
  `fee_on_day` smallint(5) DEFAULT NULL,
  `fee_interval` smallint(5) DEFAULT NULL,
  `charge_calculation_enum` smallint(5) NOT NULL,
  `calculation_percentage` decimal(19,6) DEFAULT NULL,
  `calculation_on_amount` decimal(19,6) DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `amount_paid_derived` decimal(19,6) DEFAULT NULL,
  `amount_waived_derived` decimal(19,6) DEFAULT NULL,
  `amount_writtenoff_derived` decimal(19,6) DEFAULT NULL,
  `amount_outstanding_derived` decimal(19,6) NOT NULL DEFAULT '0.000000',
  `is_paid_derived` tinyint(1) NOT NULL DEFAULT '0',
  `waived` tinyint(1) NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `inactivated_on_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `charge_id` (`charge_id`),
  KEY `m_savings_account_charge_ibfk_2` (`savings_account_id`),
  CONSTRAINT `m_savings_account_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  CONSTRAINT `m_savings_account_charge_ibfk_2` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account_charge`
--

LOCK TABLES `m_savings_account_charge` WRITE;
/*!40000 ALTER TABLE `m_savings_account_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account_charge_paid_by`
--

DROP TABLE IF EXISTS `m_savings_account_charge_paid_by`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account_charge_paid_by` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_transaction_id` bigint(20) NOT NULL,
  `savings_account_charge_id` bigint(20) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK__m_savings_account_transaction` (`savings_account_transaction_id`),
  KEY `FK__m_savings_account_charge` (`savings_account_charge_id`),
  CONSTRAINT `FK__m_savings_account_charge` FOREIGN KEY (`savings_account_charge_id`) REFERENCES `m_savings_account_charge` (`id`),
  CONSTRAINT `FK__m_savings_account_transaction` FOREIGN KEY (`savings_account_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account_charge_paid_by`
--

LOCK TABLES `m_savings_account_charge_paid_by` WRITE;
/*!40000 ALTER TABLE `m_savings_account_charge_paid_by` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account_charge_paid_by` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account_interest_rate_chart`
--

DROP TABLE IF EXISTS `m_savings_account_interest_rate_chart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account_interest_rate_chart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `from_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKSAIRC00000000000001` (`savings_account_id`),
  CONSTRAINT `FKSAIRC00000000000001` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account_interest_rate_chart`
--

LOCK TABLES `m_savings_account_interest_rate_chart` WRITE;
/*!40000 ALTER TABLE `m_savings_account_interest_rate_chart` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account_interest_rate_chart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account_interest_rate_slab`
--

DROP TABLE IF EXISTS `m_savings_account_interest_rate_slab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account_interest_rate_slab` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_interest_rate_chart_id` bigint(20) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `period_type_enum` smallint(5) NOT NULL DEFAULT '1',
  `from_period` int(11) NOT NULL DEFAULT '0',
  `to_period` int(11) DEFAULT NULL,
  `amount_range_from` decimal(19,6) DEFAULT NULL,
  `amount_range_to` decimal(19,6) DEFAULT NULL,
  `annual_interest_rate` decimal(19,6) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKSAIRS00000000000001` (`savings_account_interest_rate_chart_id`),
  CONSTRAINT `FKSAIRS00000000000001` FOREIGN KEY (`savings_account_interest_rate_chart_id`) REFERENCES `m_savings_account_interest_rate_chart` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account_interest_rate_slab`
--

LOCK TABLES `m_savings_account_interest_rate_slab` WRITE;
/*!40000 ALTER TABLE `m_savings_account_interest_rate_slab` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account_interest_rate_slab` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_account_transaction`
--

DROP TABLE IF EXISTS `m_savings_account_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_account_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_account_id` bigint(20) NOT NULL,
  `office_id` bigint(20) NOT NULL,
  `payment_detail_id` bigint(20) DEFAULT NULL,
  `transaction_type_enum` smallint(5) NOT NULL,
  `is_reversed` tinyint(1) NOT NULL,
  `transaction_date` date NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `overdraft_amount_derived` decimal(19,6) DEFAULT NULL,
  `balance_end_date_derived` date DEFAULT NULL,
  `balance_number_of_days_derived` int(11) DEFAULT NULL,
  `running_balance_derived` decimal(19,6) DEFAULT NULL,
  `cumulative_balance_derived` decimal(19,6) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `appuser_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKSAT0000000001` (`savings_account_id`),
  KEY `FK_m_savings_account_transaction_m_payment_detail` (`payment_detail_id`),
  KEY `FK_m_savings_account_transaction_m_office` (`office_id`),
  CONSTRAINT `FKSAT0000000001` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `FK_m_savings_account_transaction_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FK_m_savings_account_transaction_m_payment_detail` FOREIGN KEY (`payment_detail_id`) REFERENCES `m_payment_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_account_transaction`
--

LOCK TABLES `m_savings_account_transaction` WRITE;
/*!40000 ALTER TABLE `m_savings_account_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_account_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_interest_incentives`
--

DROP TABLE IF EXISTS `m_savings_interest_incentives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_interest_incentives` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deposit_account_interest_rate_slab_id` bigint(20) NOT NULL,
  `entiry_type` smallint(2) NOT NULL,
  `attribute_name` smallint(2) NOT NULL,
  `condition_type` smallint(2) NOT NULL,
  `attribute_value` varchar(50) NOT NULL,
  `incentive_type` smallint(2) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_savings_interest_incentives_m_savings_interest_rate_slab` (`deposit_account_interest_rate_slab_id`),
  CONSTRAINT `FK_m_savings_interest_incentives_m_savings_interest_rate_slab` FOREIGN KEY (`deposit_account_interest_rate_slab_id`) REFERENCES `m_savings_account_interest_rate_slab` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_interest_incentives`
--

LOCK TABLES `m_savings_interest_incentives` WRITE;
/*!40000 ALTER TABLE `m_savings_interest_incentives` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_interest_incentives` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_officer_assignment_history`
--

DROP TABLE IF EXISTS `m_savings_officer_assignment_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_officer_assignment_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `savings_officer_id` bigint(20) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_m_savings_officer_assignment_history_0001` (`account_id`),
  KEY `fk_m_savings_officer_assignment_history_0002` (`savings_officer_id`),
  CONSTRAINT `fk_m_savings_officer_assignment_history_0001` FOREIGN KEY (`account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `fk_m_savings_officer_assignment_history_0002` FOREIGN KEY (`savings_officer_id`) REFERENCES `m_staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_officer_assignment_history`
--

LOCK TABLES `m_savings_officer_assignment_history` WRITE;
/*!40000 ALTER TABLE `m_savings_officer_assignment_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_officer_assignment_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_product`
--

DROP TABLE IF EXISTS `m_savings_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `short_name` varchar(4) NOT NULL,
  `description` varchar(500) NOT NULL,
  `deposit_type_enum` smallint(5) NOT NULL DEFAULT '100',
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` smallint(5) NOT NULL,
  `currency_multiplesof` smallint(5) DEFAULT NULL,
  `nominal_annual_interest_rate` decimal(19,6) NOT NULL,
  `interest_compounding_period_enum` smallint(5) NOT NULL,
  `interest_posting_period_enum` smallint(5) NOT NULL DEFAULT '4',
  `interest_calculation_type_enum` smallint(5) NOT NULL,
  `interest_calculation_days_in_year_type_enum` smallint(5) NOT NULL,
  `min_required_opening_balance` decimal(19,6) DEFAULT NULL,
  `lockin_period_frequency` decimal(19,6) DEFAULT NULL,
  `lockin_period_frequency_enum` smallint(5) DEFAULT NULL,
  `accounting_type` smallint(5) NOT NULL,
  `withdrawal_fee_amount` decimal(19,6) DEFAULT NULL,
  `withdrawal_fee_type_enum` smallint(5) DEFAULT NULL,
  `withdrawal_fee_for_transfer` tinyint(4) DEFAULT '1',
  `allow_overdraft` tinyint(1) NOT NULL DEFAULT '0',
  `overdraft_limit` decimal(19,6) DEFAULT NULL,
  `min_required_balance` decimal(19,6) DEFAULT NULL,
  `enforce_min_required_balance` tinyint(1) NOT NULL DEFAULT '0',
  `min_balance_for_interest_calculation` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sp_unq_name` (`name`),
  UNIQUE KEY `sp_unq_short_name` (`short_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_product`
--

LOCK TABLES `m_savings_product` WRITE;
/*!40000 ALTER TABLE `m_savings_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_savings_product_charge`
--

DROP TABLE IF EXISTS `m_savings_product_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_savings_product_charge` (
  `savings_product_id` bigint(20) NOT NULL,
  `charge_id` bigint(20) NOT NULL,
  PRIMARY KEY (`savings_product_id`,`charge_id`),
  KEY `charge_id` (`charge_id`),
  CONSTRAINT `m_savings_product_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  CONSTRAINT `m_savings_product_charge_ibfk_2` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_savings_product_charge`
--

LOCK TABLES `m_savings_product_charge` WRITE;
/*!40000 ALTER TABLE `m_savings_product_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_savings_product_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_staff`
--

DROP TABLE IF EXISTS `m_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_loan_officer` tinyint(1) NOT NULL DEFAULT '0',
  `office_id` bigint(20) DEFAULT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `lastname` varchar(50) DEFAULT NULL,
  `display_name` varchar(102) NOT NULL,
  `mobile_no` varchar(50) DEFAULT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `organisational_role_enum` smallint(6) DEFAULT NULL,
  `organisational_role_parent_staff_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `joining_date` date DEFAULT NULL,
  `image_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `display_name` (`display_name`),
  UNIQUE KEY `external_id_UNIQUE` (`external_id`),
  UNIQUE KEY `mobile_no_UNIQUE` (`mobile_no`),
  KEY `FK_m_staff_m_office` (`office_id`),
  KEY `FK_m_staff_m_image` (`image_id`),
  CONSTRAINT `FK_m_staff_m_image` FOREIGN KEY (`image_id`) REFERENCES `m_image` (`id`),
  CONSTRAINT `FK_m_staff_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_staff`
--

LOCK TABLES `m_staff` WRITE;
/*!40000 ALTER TABLE `m_staff` DISABLE KEYS */;
INSERT INTO `m_staff` VALUES (1, 1, 4, 'Tribhuvan', 'Petshali', 'Petshali, Tribhuvan', NULL, NULL, NULL, NULL, 1, '2008-01-11', NULL),
(3, 0, 1, 'Ashutosh', 'Desai', 'Desai, Ashutosh', '987867544', NULL, NULL, NULL, 1, '2008-01-09', NULL),
(4, 0, 1, 'Hindol', 'Roy', 'Roy, Hindol', NULL, NULL, NULL, NULL, 1, '2008-03-02', NULL),
(5, 0, 1, 'Ganesan', 'SS', 'SS, Ganesan', NULL, NULL, NULL, NULL, 1, '2012-12-28', NULL),
(6, 0, 1, 'Dhiren', 'Chedda', 'Chedda, Dhiren', NULL, NULL, NULL, NULL, 1, '2013-02-12', NULL),
(7, 0, 1, 'Miteswar', 'Mahajan', 'Mahajan, Miteswar', NULL, NULL, NULL, NULL, 1, '2010-12-01', NULL),
(11, 1, 3, 'Manoj', 'Passi', 'Passi, Manoj', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(12, 0, 3, 'Sheba', 'Khanam', 'Khanam, Sheba', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(13, 1, 2, 'Ravi', 'Verma', 'Verma, Ravi', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(14, 0, 2, 'Manoj', 'Kumar', 'Kumar, Manoj', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(15, 1, 5, 'Jitendra', 'Taneja', 'Taneja, Jitendra', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(16, 1, 11, 'Mahesh', 'Gaur', 'Gaur, Mahesh', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(17, 0, 11, 'Vimal', 'Kumar Lakhani', 'Kumar Lakhani, Vimal', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(18, 1, 9, 'Vinod', 'Kumar Maheswari', 'Kumar Maheswari, Vinod', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(19, 1, 8, 'Dilip', 'Sahni', 'Sahni, Dilip', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(20, 1, 10, 'Naveen', 'Joshi', 'Joshi, Naveen', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(21, 0, 10, 'Amit', 'Nayak', 'Nayak, Amit', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(22, 0, 8, 'Preeti', 'Sharma', 'Sharma, Preeti', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(23, 0, 9, 'Neeraj', 'Gupta', 'Gupta, Neeraj', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(24, 1, 7, 'Manivannan', 'P', 'P, Manivannan', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(25, 1, 14, 'Sugumar', 'Moorthy', 'Moorthy, Sugumar', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(26, 1, 14, 'Elangovan', 'S', 'S, Elangovan', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(27, 0, 14, 'D Suresh', 'Babu', 'Babu, D Suresh', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(28, 1, 19, 'P', 'Kamaraju', 'Kamaraju, P', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(29, 0, 19, 'R', 'Latchathipathy', 'Latchathipathy, R', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(30, 1, 21, 'S', 'Kanagaraj', 'Kanagaraj, S', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(31, 0, 21, 'Mani', 'N', 'N, Mani', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(32, 1, 15, 'G', 'Selvakumar', 'Selvakumar, G', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(33, 0, 15, 'Shalini', 'R', 'R, Shalini', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(34, 1, 6, 'Sudhir', 'Bhanushali', 'Bhanushali, Sudhir', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(35, 1, 20, 'Rajiv', 'Ranjan', 'Ranjan, Rajiv', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(36, 0, 20, 'Pooja', 'Ganesh Upadhyay', 'Ganesh Upadhyay, Pooja', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(37, 1, 16, 'Vijay', 'Singh', 'Singh, Vijay', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(38, 0, 16, 'Ganesh', 'Chorghe', 'Chorghe, Ganesh', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(39, 0, 16, 'Mugdha', 'Gandhi', 'Gandhi, Mugdha', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(40, 1, 12, 'Rajesh', 'Dalvi', 'Dalvi, Rajesh', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(42, 0, 12, 'Anil', 'Shinde', 'Shinde, Anil', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(43, 0, 12, 'Vaibhav', 'More', 'More, Vaibhav', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(44, 0, 13, 'Anand', 'Korgaonkar', 'Korgaonkar, Anand', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(45, 1, 13, 'Vijay', 'Bhagwat Potdar', 'Bhagwat Potdar, Vijay', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(46, 0, 13, 'Fauziya', 'Sayyed', 'Sayyed, Fauziya', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(47, 1, 22, 'Mangesh', 'V Nakure', 'V Nakure, Mangesh', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(48, 0, 22, 'Vaibhav', 'Nagtilak', 'Nagtilak, Vaibhav', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(49, 0, 1, 'Anand', 'Mittal', 'Mittal, Anand', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(50, 0, 1, 'Pankaj', 'Sharma', 'Sharma, Pankaj', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(51, 0, 1, 'Shalini', 'Pandey', 'Pandey, Shalini', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(52, 0, 1, 'Rahul', 'Thorat', 'Thorat, Rahul', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(53, 0, 1, 'Dawood', 'Mulla', 'Mulla, Dawood', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(54, 0, 1, 'Sabina', 'Simon', 'Simon, Sabina', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(55, 0, 1, 'Pooja', 'Warikoo', 'Warikoo, Pooja', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(56, 0, 1, 'Ruksar', 'Shaikh', 'Shaikh, Ruksar', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(57, 0, 1, 'Aniket', 'Gaikwad', 'Gaikwad, Aniket', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(58, 0, 1, 'Krishna', 'Mohan', 'Mohan, Krishna', NULL, NULL, NULL, NULL, 1, NULL, NULL);
/*!40000 ALTER TABLE `m_staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_staff_assignment_history`
--

DROP TABLE IF EXISTS `m_staff_assignment_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_staff_assignment_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `centre_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m_staff_assignment_history_centre_id_m_group` (`centre_id`),
  KEY `FK_m_staff_assignment_history_m_staff` (`staff_id`),
  CONSTRAINT `FK_m_staff_assignment_history_centre_id_m_group` FOREIGN KEY (`centre_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `FK_m_staff_assignment_history_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_staff_assignment_history`
--

LOCK TABLES `m_staff_assignment_history` WRITE;
/*!40000 ALTER TABLE `m_staff_assignment_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_staff_assignment_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_tellers`
--

DROP TABLE IF EXISTS `m_tellers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_tellers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `debit_account_id` bigint(20) DEFAULT NULL,
  `credit_account_id` bigint(20) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `state` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `m_tellers_name_unq` (`name`),
  KEY `IK_m_tellers_m_office` (`office_id`),
  KEY `FK_m_tellers_gl_account_debit_account_id` (`debit_account_id`),
  KEY `FK_m_tellers_gl_account_credit_account_id` (`credit_account_id`),
  CONSTRAINT `FK_m_tellers_gl_account_credit_account_id` FOREIGN KEY (`credit_account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK_m_tellers_gl_account_debit_account_id` FOREIGN KEY (`debit_account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK_m_tellers_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_tellers`
--

LOCK TABLES `m_tellers` WRITE;
/*!40000 ALTER TABLE `m_tellers` DISABLE KEYS */;
/*!40000 ALTER TABLE `m_tellers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_template`
--

DROP TABLE IF EXISTS `m_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `text` longtext CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `entity` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_template`
--

LOCK TABLES `m_template` WRITE;
/*!40000 ALTER TABLE `m_template` DISABLE KEYS */;
INSERT INTO `m_template`  VALUES
(1, 'Credit Appraisal Memo', '<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp;</p>\n\n<p><strong>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;CREDIT APPRAISAL MEMO</strong>&nbsp;&nbsp; &nbsp;</p>\n\n<p><strong>Branch : &nbsp;</strong>{{client.officeName}}</p>\n\n<p><strong>Customer Id:&nbsp;</strong>{{loan.clientId}}</p>\n\n<p><strong>Name of Customer:</strong>&nbsp;{{loan.clientName}}</p>\n\n<p><strong>Father/Husband name :</strong>&nbsp;{{client.fathername}}</p>\n\n<p><strong>Date of Birth</strong>&nbsp;&nbsp;: &nbsp;{{client.dateOfBirth}}</p>\n\n<p><strong>Address:&nbsp;</strong>{{#address}} {{/address}}</p>\n\n<p><strong>Contact No.</strong>&nbsp; : &nbsp; {{client.mobileNo}}</p>\n\n<p><strong>Email</strong>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;: {{client.emailAddress}}</p>\n\n<p><strong>Date of Application:&nbsp;</strong>{{loan.timeline.submittedOnDate}}</p>\n\n<p><strong>Date of Approval:&nbsp;</strong>{{loan.timeline.approvedOnDate}}</p>\n\n<p><strong>Loan Account No.:&nbsp;</strong>{{loan.accountNo}}</p>\n\n<p><strong>Loan Product :&nbsp;</strong>{{loan.loanProductName}}</p>\n\n<p><strong>Product Description:&nbsp;</strong>{{loan.loanProductDescription}}</p>\n\n<p><strong>Approved Loan Amount:&nbsp;</strong>{{loan.approvedPrincipal}}</p>\n\n<p><strong>No. of Installment:&nbsp;</strong>{{loan.numberOfRepayments}}</p>\n\n<p><strong>Tenor in days:&nbsp;</strong>{{loan.repaymentSchedule.loanTermInDays}}</p>\n\n<p><strong>Interest Rate:&nbsp;</strong>{{loan.interestRatePerPeriod}}{{loan.interestRateFrequencyType.value}}</p>\n\n<p><strong>Customer Classification:</strong>{{client.clientClassification.name}}</p>\n\n<p><strong>Loan Purpose:</strong>{{loan.loanPurposeName}}&nbsp;</p>\n\n<p><strong>Primary Evaluator:&nbsp;</strong>{{loan.timeline.submittedByFirstname}}{{loan.timeline.submittedByLastname}}</p>\n\n<p><strong>Secondary Evaluator:&nbsp;</strong>{{loan.timeline.approvedByFirstname}}{{loan.timeline.approvedByLastname}}?</p>\n\n<p><strong>PE:</strong></p>\n\n<p><strong>Stamp Duty Charges:</strong></p>\n\n<p><strong>Ornament Details:</strong>{{#loan.collateral}}</p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Ornament ID</strong></td>\n			<td><strong>Ornament Type</strong></td>\n			<td><strong>Gross Weight(gms)</strong></td>\n			<td><strong>Impurity(%)</strong></td>\n			<td><strong>Net Weight(gms)</strong></td>\n			<td><strong>Rate/gm</strong></td>\n			<td><strong>Valuation</strong></td>\n			<td><strong>Fineness</strong></td>\n		</tr>\n		<tr>\n			<td>{{collateralId}}</td>\n			<td>{{jewellery.name}}</td>\n			<td>{{gross}}</td>\n			<td>{{impurity}}</td>\n			<td>{{net}}</td>\n			<td>&nbsp;</td>\n			<td>{{value}}</td>\n			<td>{{goldfine.name}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>{{/loan.collateral}}</p>\n\n<p><strong>Recommended By:</strong></p>\n\n<p><strong>Name of the user:</strong>&nbsp;{{loan.timeline.submittedByFirstname}}{{loan.timeline.submittedByLastname}}</p>\n\n<p><strong>User ID:</strong>{{loan.timeline.submittedByUsername}}</p>\n\n<p><strong>Approved By:</strong></p>\n\n<p><strong>Name of the user:&nbsp;</strong>{{loan.timeline.approvedByFirstname}}{{loan.timeline.approvedByLastname}}</p>\n\n<p><strong>User ID:&nbsp;</strong>{{loan.timeline.approvedByUsername}}</p>\n', 1, 0),
(2, 'Gold MID', '<p><strong><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;Customer Declaration-Most Important Document</strong>&nbsp; &nbsp;</p>\n\n<p><strong>Loan No.{{loan.accountNo}}</strong></p>\n\n<p>Dear Sir,</p>\n\n<p>I / We &nbsp;{{loan.clientName}} &nbsp;S/o, D/o w/o&nbsp; {{client.fatherName}} &nbsp; Resident of &nbsp; {{#client.addresses}}{{addressType.name}} &nbsp; And&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{address_line}}{{address_line_two}} {{city}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{/client.addresses}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; S/o D/o W/o&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; do hereby solemnly affirm /declare/understand and agree&nbsp; on this as hereunder.</p>\n\n<p>The Most important Terms and Conditions applicable to my Loan&nbsp;are as&nbsp;listed below and that they have been presented as a declaration / consent from my side as a mark of my acceptance and understanding of each and every point as mentioned below. This document is a composite document serving the purpose of listing out all the terms and conditions applicable as well as showing my acceptance and consent of the same and thus eliminates the need for multiple /repetitive documentation around the same objective.</p>\n\n<p>I /We declare/understand and agree that :</p>\n\n<p>(A) Explicit Declaration of Ownership : I / We say that I / We am/are the sole &amp; absolute owner/s of the ornaments as has been more specifically described, detailed in the Schedule I issued by Decimus Financial Ltd. and countersigned by me / us. (Here to and hereinafter referred as the said ornaments) pledged by me to Decimus Financial Ltd. We further assure that no other person has any share, right title or interest of any kind or nature whatsoever over the said ornaments and /or any part thereof. All the ornaments are completely free from all liens, charges, encumbrances, claims and demands and any other processes issued by any Court of Authority/Law. I /We have not created any gift or trust deed in respect of the said ornaments. I/We have an absolute, clear and marketable title thereto of the said ornaments and that nothing material has been concealed by me/us and that all the information provided is accurate &amp; complete.</p>\n\n<p>The endorsement made in the documents pertaining to the gold loan, in relation to the ornaments pledged by me/us are on the basis the representations made. I/We hereby irrevocably declare that in&nbsp;case&nbsp;at any later stage it is found by M/s. Decimus Financial Ltd. that the ornaments pledged by me/us are in any manner not as per the Specification assured / represented by us (including but not limited to being made of spurious/impure gold) then in that event I / We shall be bound by such a finding of M/s Decimus Financial Ltd. and that Decimus Financial Ltd. will pursue legal action against us according to the law of the land.</p>\n\n<p>(B) Explicit Declaration of Accepting Responsibility for Updation of Contact Details:&nbsp; I /We will inform Decimus Financial Ltd. in writing about any change of my /our address and telephone numbers until the loan and dues are completely repaid and closed. I/We understand and agree that all communication and notices sent by Decimus Financial Ltd. will be sent to the address recorded with them and that it is my/our responsibility to ensure that these contact details are updated with Decimus Financial Ltd.&nbsp;within 15 days of the change. All communications sent by Decimus Financial Ltd. will be deemed to be delivered in case I/We fail to update our contact details in time or in case of refusal on our part to accept the sent communication.</p>\n\n<p>(C) Explicit Declaration of Accepting Standards for Return of Collateral : The pledged collateral/gold would be returned within 24 working hours from the day /time on which the full payment of all outstanding dues is made on a&nbsp;best effort&nbsp;basis by Decimus Financial Ltd. In case of any further delay due to any special circumstances, Decimus Financial Ltd. will keep me/us informed.</p>\n\n<p>(D) Explicit Declaration of Accepting Standards / Practices for Collection and Recovery of Dues:&nbsp;&nbsp;&nbsp; In case the loan / any part thereof is not repaid on /within the stipulated period Decimus Financial Ltd. shall be entitled and reserves the exclusive right to&nbsp;re pledge/sell/auction/alienate /transfer/assign/securitize the said ornaments pledged with Decimus Financial Ltd. to recover the outstanding along with all other charges, levies, costs, expenses etc. (including the cost of selling the said ornaments) by way of private sale or otherwise (as may be deemed fit and proper by Decimus Financial Ltd.) and that in case the sale proceeds of the said ornaments are insufficient&nbsp; to recover my / our complete dues then I/We shall be completely and absolutely liable for the repayment of the amount due. In case the sale proceeds are higher than the total outstanding then it shall be my /our sole responsibility to take refund of the excess amount recovered from Decimus Financial Ltd. within 60 days of the date of sale and Decimus Financial Ltd. shall not pay any interest or any other charge over the same, under any circumstances whatsoever.</p>\n\n<p>The recovery of dues will be in accordance with the guidelines as laid down by the law of the land as well as the Reserve Bank of India (RBI). I understand that Decimus Financial Ltd. has a documented policy for collection and recovery of dues and that the salient features of the same have been explained to me in details and I agree to all the conditions mentioned in the same. This policy and the RBI guidelines will be available to me on request at any branch of Decimus Financial Ltd.</p>\n\n<p>I again agree that I /We will inform Decimus Financial Ltd. in writing about any change or my /our address and telephone numbers until the loan and dues are completely repaid and closed. I/We understand and agree that all communication and notices sent by Decimus Financial Ltd. will be sent to the address recorded with them and that it is my/our responsibility to ensure that these contact details are updated with Decimus Financial Ltd. within 15 days of the change.<br />\nAll communications sent by Decimus Financial Ltd. will be deemed to be delivered in case I/We fail to update our contact details in time or if I refuse to accept the communication.<br />\nThe auction of collateral ( gold ) will be done through an authorized auction house / auctioneer in a transparent and compliant manner to the widely accepted procedures and norms of the industry which include but are not limited to publicizing the event through advertisement in at least 2 newspapers and supporting an unbiased / fair auction process in line with &lsquo;Our Commitment to be Fair &ndash; Decimus Financial Ltd.&rsquo; that is available for review on request.</p>\n\n<p>(E) Explicit Declaration of Accepting Other Associated Terms and Conditions :&nbsp; I /We shall duly and faithfully observe and fulfill all the terms and conditions under which the loan has been granted to me/us by Decimus Financial Ltd. I/We declare and assure to Decimus Financial Ltd. that there is no action, suit proceeding or investigation decided, disposed or pending to my / our knowledge or threatened by /against me/us before any court of Law or any other competent authority or body which has/will have material effect on my/our business/profession/service, properties or affairs or which might put into question the validity or performance of the terms and conditions under which the loan has been granted to me/us.</p>\n\n<p>I /We am/are not aware of any document /judgment or legal process of latent /patent defect in my /our title to the said ornaments which may prejudicially affect Decimus Financial Ltd. interest, I/We hereby declare and undertake jointly and severally to&nbsp;irrevocably undertake to indemnify and keep Decimus Financial Ltd. indemnified from or against any loss, damage or risk that might arise to Decimus Financial Ltd. on account of any defect in my/our title to the said ornaments or account of any default on my/our part on account of non-performance or non-observance or breach of any terms, clause, conditions or covenant of the loan granted to me/us by Decimus Financial Ltd. , and/or any other documents axe cured by me/us or any one of me/us. I /We shall forthwith reimburse Decimus Financial Ltd. along with interest the amount of any such claim, demand, risk etc. together with all applicable costs with applicable interest as per the loan taken, as the case may be. Decimus Financial Ltd. has the right to recall the loan granted to me/us together with interest and other amounts payable to Decimus Financial Ltd. at any point in time at their own discretion with no liability for reason / explanation for the same. Decimus Financial Ltd. Reserves the right to modify / change the terms of the loan which will be binding on me / us.<br />\n&nbsp;I/We hereby specifically agree that in case of any loss or damage to the said ornaments pledged by me/us on any account whatsoever (including but not limited to theft, robbery, fire, earth-quake and/or any natural calamity), Decimus Financial Ltd. shall have a limited liability to the value of the Nett. Weight of the ornament as per the Schedule I (Nett. Gold Weight after deduction of impurity and stone weight) issued and confined to the payment/adjustment of the proportionate amount. And that such payment shall also be in proportion to my/our loan outstanding and Decimus Financial Ltd. shall be duly entitled to recover the outstanding dues from this amount of claim.</p>\n\n<p>Applicant Sign. &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Co-Applicant Sign</p>\n\n<p>____________________________ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; _____________________</p>\n\n<p>&nbsp;</p>\n\n<p>&nbsp; &nbsp;<strong>(F) EMI/Monthly SCHEME:</strong>&nbsp; Customer should repay EMI/Monthly interest&nbsp; every month through PDC/CASH/ECS/DD to DFL without fail upto end of Tenor otherwise contract will be lapsed on non-repayment periods before maturity date &amp; gold ornaments will be auctioned immediately.</p>\n\n<p><strong>(G) Explicit Declaration of Consent to Re pledge / Securitize / Assignment Collateral /Loan :&nbsp;</strong>Decimus Financial Ltd. shall have the right to assign/repledge /securitize the loan with or without security, if any, in any manner by transferring and /or assigning or otherwise (in any manner whatsoever) all or any part of its rights, title and interest which Decimus Financial Ltd. may deem appropriate. This declaration is to be considered my/our absolute permission and my/our consent toward such an arrangement and we further declare that I/We have no objection to the same.<br />\nI/We accept any such arrangement of securitization/assignment/ repledging/transfer and accept such other party/as the creditor exclusive or as a joint creditor along with Decimus Financial Ltd. or as a creditor exclusively with Decimus Financial Ltd.&rsquo;s right to continue to exercise all powers granted to Decimus Financial Ltd. under the present loan and under law, on behalf of any such other party. In case of any default on my/our part to pay dues on time any cost on this behalf, whether on account of sale securitization/assignment/ repledging/transfer or enforcement of rights to recover the pending dues,, the outstanding dues shall be recoverable from me /us and shall be levied to our account&nbsp; and that I /we irrevocably undertake to pay the difference between outstanding and the amount received by Decimus Financial Ltd. in the event to transfer of the portfolio by Decimus Financial Ltd. to a third party in such a case.</p>\n\n<p><strong>(H) Explicit Declaration of Understanding / Awareness of ALL Applicable Charges &amp; Rates :<br />\nSchedule of Charges / Loan Details:</strong><br />\nThe Schedule of Charges have been clearly explained to me as per the attached Schedule II which I have read / understood and have agreed to. I have also countersigned the same as a mark of explicit consent.<br />\nI am fully aware of all the charges applicable in case of delayed payments and I agree to service the loan at the agreed interest rates. The Delayed payment charges are levied as per the delay in payment (no. of days) with a higher interest rate not exceeding more than 50 % of the original base rate over and above the base interest rate at which the loan was offered, as per the loan scheme chosen and I am fully aware of the same. e.g. If the base rate was 24 % p.a then the delayed payment interest rate will not exceed 36% p.a. This does not include other applicable charges that may be levied and is only limited to the interest being charged. All other charges will be levied as applicable.<br />\nI have been explained all the applicable charges clearly and I completely accept the same. I accept complete responsibility of ensuring that all the dues are settled before requesting for release of the collateral.I understand that the charges are flexible and can be altered/changed at the discretion of Decimus Financial Ltd as per the repayment track/behavior of the said loan with prior intimation.</p>\n\n<p><strong>Loan Details:&nbsp;&nbsp;&nbsp;</strong>The following are the Details of the Loan that I / we have availed from Decimus Financial Ltd. and according to which all the other related terms and conditions will apply:</p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Loan no.</strong></td>\n			<td>{{loan.accountNo}}</td>\n			<td><strong>Loan Disbursal Date</strong></td>\n			<td>{{loan.timeline.actualDisbursementDate}}</td>\n		</tr>\n		<tr>\n			<td><strong>Scheme Name</strong></td>\n			<td>{{loan.loanProductName}}</td>\n			<td><strong>Interest rate(PA)%</strong></td>\n			<td>{{loan.annualInterestRate}}</td>\n		</tr>\n		<tr>\n			<td><strong>Loan Amount</strong></td>\n			<td>{{loan.repaymentSchedule.totalPrincipalDisbursed}}</td>\n			<td><strong>Interest Payment Frequency</strong></td>\n			<td>{{loan.repaymentEvery}}{{loan.repaymentFrequencyType.value}}</td>\n		</tr>\n		<tr>\n			<td><strong>Loan Maturity Date</strong></td>\n			<td>{{loan.timeline.expectedMaturityDate}}</td>\n			<td><strong>Tenure of Loan(days)</strong></td>\n			<td>{{loan.repaymentSchedule.loanTermInDays}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>The Interest Payment Frequency details are as follows:<br />\n01. Upfront Monthly &ndash; The Interest is paid in advance on a 30 day cycle<br />\n02. Full Upfront &ndash; The Entire Interest Due is paid in advance at the time of Disbursal<br />\n03. Monthly Interest &ndash; The Interest due is paid monthly on a 30 day cycle from date of disbursal<br />\n04. Bullet Payment &ndash; The Entire Interest due is to be repaid at the end of the tenure<br />\n05. EMI Payment &ndash; The Interest Due is amortized over the tenure of the scheme at the frequency of the agreed Equated Installment<br />\nThe Repayment of the Principal Amount is due at the end of the tenure for all schemes except the EMI scheme where the Principal is also amortized over the tenure of the scheme.</p>\n\n<p><strong>(I) Explicit Declaration of Other Associated Terms and Conditions :</strong><br />\nI/We further agree that the commitments and/or assurance made in the present declaration are in addition to our other liabilities, covenants, commitments and /or assurances etc made vide any other agreement or other wise to Decimus Financial Ltd. In case of dispute between the parties regarding Interpretation of the loan agreement, or any other dispute pertaining to the loan &amp; the agreement between the parties including disputes about non payment of dues by me/us, the same shall be governed by Indian Arbitration Act&nbsp; as amended up to date &amp; shall be referred to the&nbsp; Sole arbitrator appointed&nbsp; by Decimus Financial Ltd. and that&nbsp; the&nbsp; Arbitration will be held at Pune ( Maharashtra ) .The decision of the Sole arbitrator shall be final &amp; binding on the parties and the arbitration proceeding would be conducted in English and in no other language.<br />\nI hereby declare that I am in no way related to any employee / director of Decimus Financial Ltd.<br />\nI/We hereby confirm that in case any of the declaration/undertaking or any part thereof is found to be incorrect and /or misleading then I /We shall be liable to be prosecuted under the relevant provisions of the Indian Penal Code. I/We make the aforesaid declaration and statements and give the aforesaid, undertaking .solemnly and sincerely believing the same to be true and knowing fully well that on the faith and strength thereof along with my/other commitments, conversant assurances, representation etc. Decimus Financial Ltd. has agreed to grant the Gold Loan.<br />\nI have been told and explained about the Fair Practices Code being followed called &lsquo;Our Commitment to be Fair &ndash; Decimus Financial Ltd.&rsquo; ; the Policy of Collection/Recovery of Dues including the Auction Policy and that these are available / will be made available to me on request at any time during the tenure or the loan free of cost. I have also been informed of the Complaint/ Grievance handling Process and will follow the same in case of any complaint.<br />\nI agree that in case of any discrepancy / difference in terms / conditions in any other associated document, this declaration and the conditions / terms within this declaration will supersede any other document/communication/notice.</p>\n\n<p><strong>(J) Explicit Declaration of Awareness and Acceptance in case of Vernacular Cases :</strong><br />\nI have also been explained well in the local vernacular language (Hindi/Tamil/Marathi as applicable&nbsp;&nbsp; , have seen, read, understood and accept all these conditions as well in the vernacular language (Hindi/Tamil/Marathi as applicable) available at the branch and hereby agree and give my explicit consent to all of the above. &ndash; (to be used along with the Vernacular Declaration). I am aware that all these terms and conditions are readily available at all Decimus Financial Ltd. branched for review on request.&nbsp;</p>\n\n<p><strong>(K ) Nomination Details&nbsp; :</strong></p>\n\n<p>That I hereby Nominate Mr./ Mrs. ............................&nbsp; who is my ......................................&nbsp; &nbsp;in relation as my nominee The nominee should be duly entitled and authorized to exercise all right and controls over the gold ornaments on my behalf in case of my demise</p>\n\n<p><strong>(L)</strong>&nbsp;<strong>Decimus Financial Ltd</strong>&nbsp;has the right to retain the Pledged Articles offered as security for any other liability due to&nbsp;<strong>Decimus Financial ltd</strong>&nbsp;by me,Whether such liability has been demanded or not untill the repayment of all the dues/outstanding amount of all the loan and liability taken by me from&nbsp;<strong>Decimus Financial Ltd. Decimus Financial Ltd.&nbsp;</strong>shall intimate the same to me;</p>\n\n<p>Signature/s of the Nominee/Guardian in case of nominee being a minor &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; _____________________</p>\n\n<p>Place: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p>\n\n<p>&nbsp;</p>\n\n<p>Applicant Sign: __________________________ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Co-applicant Sign _________________________</p>\n\n<p>{{#loan.collateral}}</p>\n\n<p><strong>Schedule I- Details of Collateral Offered for Loan Against Gold:</strong></p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Ornament ID</strong></td>\n			<td><strong>Ornament Type</strong></td>\n			<td><strong>Gross Weight(gms)</strong></td>\n			<td><strong>Impurity(%)</strong></td>\n			<td><strong>Net Weight(gms)</strong></td>\n			<td><strong>Rate/gm</strong></td>\n			<td><strong>ValuationAccepted</strong></td>\n			<td><strong>Fineness</strong></td>\n		</tr>\n		<tr>\n			<td>{{collateralId}}</td>\n			<td>{{jewellery.name}}</td>\n			<td>{{gross}}</td>\n			<td>{{impurity}}</td>\n			<td>{{net}}</td>\n			<td>&nbsp;</td>\n			<td>{{value}}</td>\n			<td>{{goldfine.name}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>{{/loan.collateral}}</p>\n\n<p>I have been explained all the above Rates of Interest and other charges as applicable to my Loan Account, and I completely agree, fully understand and accept the same.</p>\n\n<p>Applicant Sign. &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Co-borrower Sign.</p>\n\n<p>______________________ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; _________________________</p>\n', 1, 0),
(3, 'Gold Loan Receipt Customer Copy', '<p><a href="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg"><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" /></a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<strong>GOLD LOAN RECEIPT(Customer Copy)</strong></p>\n\n<p>&nbsp;</p>\n\n<p><strong>To,</strong>&nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<strong>Loan A/c no</strong>.{{loan.accountNo}}</p>\n\n<p>{{loan.clientName}} &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<strong>Branch:</strong>{{client.officeName}}</p>\n\n<p>{{#datatable.data}} {{row}}{{/datatable.data}}</p>\n\n<p>{{client.mobileNo}}</p>\n\n<p><strong>Date(yy/mm/dd):&nbsp;</strong>{{loan.timeline.actualDisbursementDate}}</p>\n\n<p>Sir/Madam,</p>\n\n<p>We hereby sanction a loan of Rs.{{loan.summary.principalDisbursed}} against the gold jewellery pledged per the valuation certificate.</p>\n\n<p><strong>Loan Amount:</strong>&nbsp;&nbsp;{{loan.summary.principalDisbursed}}&nbsp;<strong>Tenure:</strong>&nbsp;{{loan.repaymentSchedule.loanTermInDays}}&nbsp;<strong>GLSCHEME:</strong>{{loan.loanProductName}}</p>\n\n<p><strong>ROI(per annum):</strong>{{loan.annualInterestRate}} &nbsp;<strong>Net Disbursal Amount:</strong>{{loan.summary.principalDisbursed}}</p>\n\n<p><strong>Maturity Date:</strong>{{loan.expectedMaturityDate}}&nbsp;<strong>Total interest charged:</strong>{{loan.repaymentSchedule.totalInterestCharged}}</p>\n\n<p><strong>First Due:</strong>{{loan.period.duedate}}<strong>Last Due:</strong>{{loan.expectedMaturityDate}}?<strong>&nbsp;&nbsp;EMI:</strong>{{loan.period.totalOriginalDueForPeriod}}</p>\n\n<p>(Interest Payment or EMI if not done on time, will attract the penalty interest as per our policy)</p>\n\n<p>.This is to confirm that you have deposited the following item/gold ornaments with us to secure the loan granted by us to you. The complete details of the gold ornaments are as follows:</p>\n\n<p>{{#loan.collateral}}</p>\n\n<p><strong>Ornament Details:</strong></p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Ornament ID</strong></td>\n			<td><strong>Ornament Type</strong></td>\n			<td><strong>Gross Weight(gms)</strong></td>\n			<td><strong>Impurity(%)</strong></td>\n			<td><strong>Net Weight(gms)</strong></td>\n			<td><strong>Rate/gm</strong></td>\n			<td><strong>Valuation</strong></td>\n			<td><strong>Fineness</strong></td>\n		</tr>\n		<tr>\n			<td>{{collateralId}}</td>\n			<td>{{jewellery.name}}</td>\n			<td>{{gross}}</td>\n			<td>{{impurity}}</td>\n			<td>{{net}}</td>\n			<td>&nbsp;</td>\n			<td>{{value}}</td>\n			<td>{{goldfine.name}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>{{/loan.collateral}}</p>\n\n<p>&nbsp;</p>\n\n<p>&nbsp;</p>\n\n<p>Authorised Signatory</p>\n\n<p>(For Decimus Financial Ltd)</p>\n\n<p>You are kindly requested to bring this letter at the time of redeeming/taking back the gold ornaments after the closure/complete repayment of the loan granted to you.Please note that the gold ornaments would only be returned on production of this letter in original by all the borrowers.Please ensure that you get a receipt for all the payments you make.Please check all the details(loan no., amount paid,date etc) to ensure that it is accurate as no disputes will be entertained at a later date once the receipt is signed and accepted by you. Please ensure that you have gone through all the documents and schedule of charges very carefully and are well aware of the Terms and conditions and charges applicable to the loan.Please inform us of any change of address/telephone as all important communication is sent to the registered address.</p>\n\n<p>I {{clientName}} hereby acknowledge the receipt of a loan amount of Rs.{{loan.summary.principalDisbursed}} in {{loan.transactions.paymentDetailData.paymentType}} against the gold pledged with your company.</p>\n\n<p>I/we hereby confirm that the above details of the gold ornaments deposited/pledged by me/us with Decimus Financial Limited are complete and accurate and that the same is binding upon me/us.</p>\n\n<p>&nbsp;</p>\n\n<p>Borrower &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; GLO &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Branch Manager</p>\n\n<p>We are a RBI registered NBFC.Decimus Financial Limited, Decimus House,195,M G Road, Pune-411001/info@decimus.in</p>\n', 1, 0),
(4, 'Gold Loan Receipt Branch Copy', '<p><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<strong>GOLD LOAN RECEIPT( Branch Copy)</strong>&nbsp;</p>\n\n<p>&nbsp;</p>\n\n<p><strong>To,</strong>&nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<strong>Loan A/c no</strong>.{{loan.accountNo}}</p>\n\n<p>{{loan.clientName}} &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<strong>Branch:</strong>{{client.officeName}}</p>\n\n<p>{{#datatable.data}} {{row}}{{/datatable.data}}</p>\n\n<p>{{client.mobileNo}}</p>\n\n<p><strong>Date(yy/mm/dd):&nbsp;</strong>{{loan.timeline.actualDisbursementDate}}</p>\n\n<p>Sir/Madam,</p>\n\n<p>We hereby sanction a loan of Rs.{{loan.summary.principalDisbursed}} against the gold jewellery pledged per the valuation certificate.</p>\n\n<p><strong>Loan Amount:</strong>&nbsp;&nbsp;{{loan.summary.principalDisbursed}}&nbsp;<strong>Tenure:</strong>&nbsp;{{loan.repaymentSchedule.loanTermInDays}}&nbsp;<strong>GLSCHEME:</strong>{{loan.loanProductName}}</p>\n\n<p><strong>ROI(per annum):</strong>{{loan.annualInterestRate}} &nbsp;<strong>Net Disbursal Amount:</strong>{{loan.summary.principalDisbursed}}</p>\n\n<p><strong>Maturity Date:</strong>{{loan.expectedMaturityDate}}&nbsp;<strong>Total interest charged:</strong>{{loan.repaymentSchedule.totalInterestCharged}}</p>\n\n<p><strong>First Due:</strong>{{loan.period.duedate}}<strong>Last Due:</strong>{{loan.expectedMaturityDate}}?<strong>&nbsp;&nbsp;EMI:</strong>{{loan.period.totalOriginalDueForPeriod}}</p>\n\n<p>(Interest Payment or EMI if not done on time, will attract the penalty interest as per our policy)</p>\n\n<p>.This is to confirm that you have deposited the following item/gold ornaments with us to secure the loan granted by us to you. The complete details of the gold ornaments are as follows:</p>\n\n<p>{{#loan.collateral}}</p>\n\n<p><strong>Ornament Details:</strong></p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Ornament ID</strong></td>\n			<td><strong>Ornament Type</strong></td>\n			<td><strong>Gross Weight(gms)</strong></td>\n			<td><strong>Impurity(%)</strong></td>\n			<td><strong>Net Weight(gms)</strong></td>\n			<td><strong>Rate/gm</strong></td>\n			<td><strong>Valuation</strong></td>\n			<td><strong>Fineness</strong></td>\n		</tr>\n		<tr>\n			<td>{{collateralId}}</td>\n			<td>{{jewellery.name}}</td>\n			<td>{{gross}}</td>\n			<td>{{impurity}}</td>\n			<td>{{net}}</td>\n			<td>&nbsp;</td>\n			<td>{{value}}</td>\n			<td>{{goldfine.name}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>{{/loan.collateral}}</p>\n\n<p>&nbsp;</p>\n\n<p>&nbsp;</p>\n\n<p>Authorised Signatory</p>\n\n<p>(For Decimus Financial Ltd)</p>\n\n<p>You are kindly requested to bring this letter at the time of redeeming/taking back the gold ornaments after the closure/complete repayment of the loan granted to you.Please note that the gold ornaments would only be returned on production of this letter in original by all the borrowers.Please ensure that you get a receipt for all the payments you make.Please check all the details(loan no., amount paid,date etc) to ensure that it is accurate as no disputes will be entertained at a later date once the receipt is signed and accepted by you. Please ensure that you have gone through all the documents and schedule of charges very carefully and are well aware of the Terms and conditions and charges applicable to the loan.Please inform us of any change of address/telephone as all important communication is sent to the registered address.</p>\n\n<p>I {{clientName}} hereby acknowledge the receipt of a loan amount of Rs.{{loan.summary.principalDisbursed}} in {{loan.transactions.paymentDetailData.paymentType}} against the gold pledged with your company.</p>\n\n<p>I/we hereby confirm that the above details of the gold ornaments deposited/pledged by me/us with Decimus Financial Limited are complete and accurate and that the same is binding upon me/us.</p>\n\n<p>&nbsp;</p>\n\n<p>Borrower &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; GLO &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Branch Manager</p>\n\n<p>We are&nbsp;a RBI&nbsp;registered NBFC.Decimus Financial Limited, Decimus&nbsp;House,195,M&nbsp;G Road, Pune-411001/info@decimus.in</p>\n', 1, 0),
(5, 'Valuation Certificate Branch Copy', '<p><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp; &nbsp;&nbsp;<strong>VALUATION CERTIFICATE(Branch Copy)</strong></p>\n\n<p><strong>Name of Borrower: &nbsp; &nbsp;{{loan.clientName}} &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Date:&nbsp;</strong>{{loan.timeline.actualDisbursementDate}}</p>\n\n<p><strong>Name of the co-borrower: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Branch:</strong>{{client.officeName}}</p>\n\n<p><strong>Name of the appraiser: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Loan no.&nbsp;</strong>{{loan.accountNo}}</p>\n\n<p>{{#loan.collateral}}</p>\n\n<p><strong>Ornament Details:</strong></p>\n\n<table border="1" cellpadding="1" cellspacing="1" style="width:500px">\n	<tbody>\n		<tr>\n			<td><strong>Ornament ID</strong></td>\n			<td><strong>Ornament Type</strong></td>\n			<td><strong>Gross Weight(gms)</strong></td>\n			<td><strong>Impurity(%)</strong></td>\n			<td><strong>Net Weight(gms)</strong></td>\n			<td><strong>Rate/gm</strong></td>\n			<td><strong>Valuation</strong></td>\n			<td><strong>Fineness</strong></td>\n		</tr>\n		<tr>\n			<td>{{collateralId}}</td>\n			<td>{{jewellery.name}}</td>\n			<td>{{gross}}</td>\n			<td>{{impurity}}</td>\n			<td>{{net}}</td>\n			<td>&nbsp;</td>\n			<td>{{value}}</td>\n			<td>{{goldfine.name}}</td>\n		</tr>\n	</tbody>\n</table>\n\n<p>{{/loan.collateral}}</p>\n\n<p>Certified that the above Net Weight in grams and that the jewellery has been valued at the above rates for loan purposes only and is true as is correct to the best of my knowledge. This is lower than actual market value for gold as on this day.</p>\n\n<p>Signature of Borrower:</p>\n\n<p>Signature of Co-borrower: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Signature of Appraiser:</p>\n\n<p>We the undersigned confirm the above valuation and the signatories have signed in our presence.</p>\n\n<p>Name and signature of Joint Custodian1 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Name and signature of Joint Custodian2</p>\n\n<p>_________________________________ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;______________________________________</p>\n\n<hr />\n<p>&nbsp;</p>\n\n<p>&nbsp;</p>\n\n<p><strong>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</strong></p>\n\n<p><strong><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp;DEMAND PROMISORY NOTE</strong></p>\n\n<p><strong>Place:&nbsp;</strong>{{client.officeName}}</p>\n\n<p><strong>Date:&nbsp;&nbsp;</strong>{{loan.timeline.actualDisbursementDate}}</p>\n\n<p>On Demand, I/We&nbsp;<strong>{{loan.clientName}}</strong>&nbsp;the undersigned, hereby unconditionally promise to jointly/severally pay Decimus Financial Limited having its Corporate Office at 195,M G Road, Camp, Pune, Pincode -411001, Maharashtra the Sum of&nbsp;&nbsp;<strong>{{loan.repaymentSchedule.totalPrincipalDisbursed}}</strong>&nbsp;For loan no.&nbsp;<strong>{{loan.accountNo}}&nbsp;</strong>alongwith interest @&nbsp;<strong>{{loan.interestRatePerPeriod}}{{loan.interestRateFrequencyType.value}}&nbsp;</strong>alongwith all other applicable charges towards Lending against Gold as per Decimus Financial Ltd&#39;s Schedule of Charges. &nbsp;&nbsp;</p>\n', 1, 0),
(6, 'Application Form', '<p><img alt="" src="http://decimusfinancial.co.in/wp-content/uploads/2014/08/logo.jpg" style="height:100px; width:252px" />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<strong>APPLICATION FORM</strong></p>\n\n<p><strong>Loan no</strong>&nbsp;{{loan.accountNo}} &nbsp; &nbsp; &nbsp;<strong>Branch:&nbsp;</strong>{{client.officeName}}&nbsp;<strong>Branch Code:</strong>{{client.officeId}}</p>\n\n<p><strong>Date:</strong>{{loan.actualDisbursementDate}} &nbsp; &nbsp;&nbsp;<strong>Scheme:</strong>{{loan.loanProductName}}</p>\n\n<p><strong>PERSONAL DETAILS:</strong></p>\n\n<p><strong>Applicant Name:</strong>{{loan.clientName}} &nbsp; &nbsp; &nbsp;<strong>Date of Birth:</strong>{{client.dateOfBirth}}</p>\n\n<p><strong>Father/Husband Name:&nbsp;</strong>{{client.fatherName}}&nbsp;<strong>Gender:</strong>{{client.gender.name}} &nbsp; &nbsp;<strong>Pan no.:</strong>{{client.externalId}}</p>\n\n<p><strong>Co-applicant Name: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Marital Status:</strong>{{client.marital.name}} &nbsp;&nbsp;<strong>Occupation:</strong>{{client.clientClassification.name}}</p>\n\n<p><strong>Co-applicant DOB: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Residence Since:</strong></p>\n\n<p><strong>Mobile no.:</strong></p>\n\n<p><strong>Residence Address:</strong>{{#datatable.data}} {{row}} {{/datatable.data}}&nbsp;</p>\n\n<p><strong>Permanent Address:</strong>{{#datatable.data}} {{row}} {{/datatable.data}}</p>\n\n<p><strong>Official Address:</strong>{{#datatable.data}} {{row}} {{/datatable.data}}</p>\n\n<p><strong>Landline No.:</strong></p>\n\n<p><strong>Email address:</strong>{{client.emailAddress}} &nbsp; &nbsp;<strong>Education:</strong>{{client.education.name}} R<strong>eligion:</strong>{{client.religion.name}}</p>\n\n<p><strong>Bank Details:</strong></p>\n\n<p><strong>Name of Bank: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Account No. &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Branch IFSC Code:</strong></p>\n\n<p><strong>Loan Details:</strong></p>\n\n<p><strong>Loan Amount:</strong>{{loan.principalDisbursed}} &nbsp;<strong>Loan Term(in days)</strong>:{{loan.repaymentSchedule.loanTermInDays}}&nbsp;<strong>ROI:</strong>{{loan.interestRatePerPeriod}}{{loan.interestRateFrequencyType.value}} &nbsp; &nbsp;</p>\n\n<p><strong>Net Disbursal:</strong>{{loan.repaymentSchedule.totalPrincipalDisbursed}}&nbsp;<strong>Disbursal Date:</strong>{{loan.actualDisbursementDate}} &nbsp;&nbsp;<strong>Scheme:{{loan.loanProductName}}</strong></p>\n\n<p><strong>Maturity Date:</strong>{{loan.expectedMaturityDate}}</p>\n\n<p><strong>Declaration:</strong></p>\n\n<p>I, the Undersigned hereby acknowledge the receipt of the loan amount as mentioned above against Gold Pledged as Collateral with Decimus Financial Ltd.&nbsp;&nbsp;&nbsp; I / We hereby confirm that the above details of the Gold Items deposited / pledged by me / us , are complete and accurate and the same is binding upon me / us. I agree and understand all the schedule of charges&nbsp; and agree to abide by them.</p>\n\n<p>I am fully aware that delayed payments will / may incur additional charges as listed in the schedule of charges policy at Decimus Financial Ltd..I agree and accept all the information listed above as accurate and that I will abide by all the terms and conditions as countersigned by me on this form</p>\n\n<p>Customer Name: &nbsp; {{loan.clientName}} &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Co-applicant Name:</p>\n\n<p>Signature and Date: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Signature and Date:</p>\n\n<p>&nbsp;</p>\n\n<p><strong>Appraiser Declaration and Authorised Signatory- Decimus Financial Ltd(name/code/Signature)</strong></p>\n\n<p>I/We hereby Certify that the Gold Items as listed above as genuine with the specified attributes of quality &amp; quantity and the valuation has been done with prevailing Market Valuations standards and as per Decimus Financial Ltd.&#39;s Internal Valuation Policies. I have also verified all the relevant documents against the Originals.&nbsp;&nbsp; I/ We have followed all the relevant Decimus Financial Ltd. Policies and Processes and all the above information is accurate to the best of my knowledge&quot;</p>\n\n<p>Appraiser Name1:{{loan.timeline.submittedByFirstname}}{{loan.timeline.submittedByLastname}}&nbsp;</p>\n\n<p>Signature:</p>\n\n<p>Appraiser Name2:{{loan.timeline.approvedByFirstname}}{{loan.timeline.approvedByLastname}}&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</p>\n\n<p>Signature:</p>\n', 1, 0);

/*!40000 ALTER TABLE `m_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_template_m_templatemappers`
--

DROP TABLE IF EXISTS `m_template_m_templatemappers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_template_m_templatemappers` (
  `m_template_id` bigint(20) NOT NULL,
  `mappers_id` bigint(20) NOT NULL,
  UNIQUE KEY `mappers_id` (`mappers_id`),
  KEY `mappers_id_2` (`mappers_id`),
  KEY `m_template_id` (`m_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_template_m_templatemappers`
--

LOCK TABLES `m_template_m_templatemappers` WRITE;
/*!40000 ALTER TABLE `m_template_m_templatemappers` DISABLE KEYS */;
INSERT INTO `m_template_m_templatemappers`  VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(3, 6),
(3, 7),
(3, 8),
(4, 12),
(4, 13),
(4, 14),
(5, 15),
(5, 16),
(6, 17),
(6, 18),
(6, 19);
/*!40000 ALTER TABLE `m_template_m_templatemappers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_templatemappers`
--

DROP TABLE IF EXISTS `m_templatemappers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_templatemappers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mapperkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `mapperorder` int(11) DEFAULT NULL,
  `mappervalue` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_templatemappers`
--

LOCK TABLES `m_templatemappers` WRITE;
/*!40000 ALTER TABLE `m_templatemappers` DISABLE KEYS */;
INSERT INTO `m_templatemappers`  VALUES
(1, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(2, 'client', 1, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(3, 'address', 2, 'clients/{{loan.clientId}}/addresses?tenantIdentifier=default&pretty=true'),
(4, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(5, 'client', 1, 'clients/{{loan.clientId}}?associations=all&tenantIdentifier=default&pretty=true'),
(6, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(7, 'datatable1', 1, 'datatables/Present%20Address/{{loan.clientId}}?genericResultSet=true&tenantIdentifier=default&pretty=true'),
(8, 'client', 2, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(9, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(10, 'datatable1', 1, 'datatables/Present%20Address/{{loan.clientId}}?genericResultSet=true&tenantIdentifier=default&pretty=true'),
(11, 'client', 2, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(12, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(13, 'datatable1', 1, 'datatables/Present%20Address/{{loan.clientId}}?genericResultSet=true&tenantIdentifier=default&pretty=true'),
(14, 'client', 2, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(15, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(16, 'client', 1, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(17, 'loan', 0, 'loans/{{loanId}}?associations=all&tenantIdentifier=live'),
(18, 'client', 1, 'clients/{{loan.clientId}}?tenantIdentifier=default&pretty=true'),
(19, 'datatable1', 2, 'datatables/Present%20Address/{{loan.clientId}}?genericResultSet=true&tenantIdentifier=default&pretty=true');

/*!40000 ALTER TABLE `m_templatemappers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `m_working_days`
--

DROP TABLE IF EXISTS `m_working_days`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_working_days` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recurrence` varchar(100) DEFAULT NULL,
  `repayment_rescheduling_enum` smallint(5) DEFAULT NULL,
  `extend_term_daily_repayments` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `m_working_days`
--

LOCK TABLES `m_working_days` WRITE;
/*!40000 ALTER TABLE `m_working_days` DISABLE KEYS */;
INSERT INTO `m_working_days` VALUES (1,'FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU',2,0);
/*!40000 ALTER TABLE `m_working_days` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PDC`
--

CREATE TABLE IF NOT EXISTS `PDC` (
  `loan_id` bigint(20) NOT NULL,
  `Cheque No` int(11) NOT NULL,
  `Cheque Date` date NOT NULL,
  `Bank if other` varchar(25) DEFAULT NULL,
  `Branch` varchar(25) DEFAULT NULL,
  `PDC amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




--
-- Table structure for table `mix_taxonomy`
--

DROP TABLE IF EXISTS `mix_taxonomy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mix_taxonomy` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `namespace_id` int(11) DEFAULT NULL,
  `dimension` varchar(100) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `need_mapping` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mix_taxonomy`
--

LOCK TABLES `mix_taxonomy` WRITE;
/*!40000 ALTER TABLE `mix_taxonomy` DISABLE KEYS */;
INSERT INTO `mix_taxonomy` VALUES (1,'AdministrativeExpense',1,NULL,3,NULL,1),(2,'Assets',3,NULL,1,'All outstanding principals due for all outstanding client loans. This includes current, delinquent, and renegotiated loans, but not loans that have been written off. It does not include interest receivable.',1),(3,'Assets',3,'MaturityDimension:LessThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',1),(4,'Assets',3,'MaturityDimension:MoreThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',1),(5,'CashAndCashEquivalents',1,NULL,1,NULL,1),(6,'Deposits',3,NULL,1,'The total value of funds placed in an account with an MFI that are payable to a depositor. This item includes any current, checking, or savings accounts that are payable on demand. It also includes time deposits which have a fixed maturity date and compulsory deposits.',1),(7,'Deposits',3,'DepositProductsDimension:CompulsoryMember',1,'The value of deposits that an MFI\'s clients are required to  maintain as a condition of an existing or future loan.',NULL),(8,'Deposits',3,'DepositProductsDimension:VoluntaryMember',1,'The value of deposits that an MFI\'s clients are not required to  maintain as a condition of an existing or future loan.',NULL),(9,'Deposits',3,'LocationDimension:RuralMember',1,'Located in rural areas. Segmentation based on location.',NULL),(10,'Deposits',3,'LocationDimension:UrbanMember',1,'Located in urban areas. Segmentation based on location.',NULL),(11,'Deposits',3,'MaturityDimension:LessThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',NULL),(12,'Deposits',3,'MaturityDimension:MoreThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',NULL),(13,'EmployeeBenefitsExpense',1,NULL,3,NULL,NULL),(14,'Equity',1,NULL,1,NULL,NULL),(15,'Expense',1,NULL,3,NULL,NULL),(16,'FinancialExpense',3,NULL,3,'All costs All costs incurred in raising funds from third parties, fee expenses from non-financial services, net gains (losses) due to changes in fair value of financial liabilities, impairment losses net of reversals of financial assets other than loan portfolio and net gains (losses) from restatement of financial statements in terms of the measuring unit current at the end of the reporting period.',NULL),(17,'FinancialRevenueOnLoans',3,NULL,2,'Interest and non-interest income generated by the provision of credit services to the clients. Fees and commissions for late payment are also included.',NULL),(18,'ImpairmentLossAllowanceGrossLoanPortfolio',3,NULL,2,'An allowance for the risk of losses in the gross loan portfolio due to default .',NULL),(19,'Liabilities',1,NULL,1,NULL,NULL),(20,'Liabilities',3,'MaturityDimension:LessThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',NULL),(21,'Liabilities',3,'MaturityDimension:MoreThanOneYearMember',1,'Segmentation based on the life of an asset or liability.',NULL),(22,'LoanPortfolioGross',3,NULL,2,'All outstanding principals due for all outstanding client loans. This includes current, delinquent, and renegotiated loans, but not loans that have been written off. It does not include interest receivable.',NULL),(23,'LoanPortfolioGross',3,'CreditProductsDimension:MicroenterpriseMember',2,'Loans that finance the production or trade of goods and  services for an individual\'s microenterprise, whether or not the microenterprise is legally registered. Segmentation based on loan product.',NULL),(24,'LoanPortfolioGross',3,'DelinquencyDimension:OneMonthOrMoreMember',2,'Segmentation based on the principal balance of all loans outstanding that have one or more installments of principal  past due or renegotiated. Segmentation based on the  principal balance of all loans outstanding that have one or  more installments of principal past due or renegotiated.',NULL),(25,'LoanPortfolioGross',3,'DelinquencyDimension:ThreeMonthsOrMoreMember',2,'Segmentation based on the principal balance of all loans outstanding that have one or more installments of principal  past due or renegotiated.? Segmentation based on the  principal balance of all loans outstanding that have one or  more installments of principal past due or renegotiated.',NULL),(26,'LoanPortfolioGross',3,'LocationDimension:RuralMember',2,'Located in rural areas. Segmentation based on geographic location.',NULL),(27,'LoanPortfolioGross',3,'LocationDimension:UrbanMember',2,'Located in urbal areas. Segmentation based on geographic location.',NULL),(28,'LoanPortfolioGross',3,'MaturityDimension:LessThanOneYearMember',2,'Segmentation based on the life of an asset or liability.',NULL),(29,'LoanPortfolioGross',3,'MaturityDimension:MoreThanOneYearMember',2,'Segmentation based on the life of an asset or liability.',NULL),(30,'NetLoanLoss',3,'',3,'Referred to the value of delinquency loans written off net of any principal recovery.',NULL),(31,'NetLoanLossProvisionExpense',3,NULL,3,'Represent the net value of loan portfolio impairment loss considering any reversal on impairment loss and any recovery on loans written off recognized as a income during the accounting period.',NULL),(32,'NetOperatingIncome',3,NULL,2,'Total operating revenue less all expenses related to the MFI\'s core financial service operation including total financial expense, impairment loss and operating expense. Donations are excluded.',NULL),(33,'NetOperatingIncomeNetOfTaxExpense',3,NULL,3,'Net operating income reported incorporating the effect of taxes. Taxes include all domestic and foreign taxes which are based on taxable profits, other taxes related to personnel, financial transactions or value-added taxes are not considered in calculation of this value.',NULL),(34,'NumberOfActiveBorrowers',3,NULL,0,'The number of individuals who currently have an outstanding loan balance with the MFI or are primarily responsible for repaying any portion of the gross loan portfolio. Individuals who have multiple loans with an MFI should be counted as a single borrower.',NULL),(35,'NumberOfActiveBorrowers',3,'GenderDimension:FemaleMember',0,'The number of individuals who currently have an outstanding loan balance with the MFI or are primarily responsible for repaying any portion of the gross loan portfolio. Individuals who have multiple loans with an MFI should be counted as a single borrower.',NULL),(36,'NumberOfBoardMembers',3,'GenderDimension:FemaleMember',0,'The number of members that comprise the board of directors at the end of the reporting period who are female.',NULL),(37,'NumberOfDepositAccounts',3,NULL,0,'The number of individuals who currently have funds on deposit with the MFI on a voluntary basis; i.e., they are not required to maintain the deposit account to access a loan. This number applies only to deposits held by an MFI, not to those deposits held in other institutions by the MFI\'s clients. The number should be based on the number of individuals rather than the number of groups. A single deposit account may represent multiple depositors.',NULL),(38,'NumberOfDepositors',3,'',0,'The number of deposit accounts, both voluntary and compulsory, opened at the MFI whose balances the institution is liable to repay. The number should be based on the number of individual accounts rather than on the number of groups.',NULL),(39,'NumberOfEmployees',3,NULL,0,'The number of individuals who are actively employed by an entity. This number includes contract employees or advisors who dedicate a substantial portion of their time to the entity, even if they are not on the entity\'s employees roster.',NULL),(40,'NumberOfEmployees',3,'GenderDimension:FemaleMember',0,'The number of individuals who are actively employed by an entity. This number includes contract employees or advisors who dedicate a substantial portion of their time to the entity, even if they are not on the entity\'s employees roster.',NULL),(41,'NumberOfLoanOfficers',3,NULL,0,'The number of employees whose main activity is to manage a portion of the gross loan portfolio. A loan officer is a staff member of record who is directly responsible for arranging and monitoring client loans.',NULL),(42,'NumberOfLoanOfficers',3,'GenderDimension:FemaleMember',0,'The number of employees whose main activity is to manage a portion of the gross loan portfolio. A loan officer is a staff member of record who is directly responsible for arranging and monitoring client loans.',NULL),(43,'NumberOfManagers',3,'GenderDimension:FemaleMember',0,'The number of members that comprise the management of the institution who are female.',NULL),(44,'NumberOfOffices',3,NULL,0,'The number of staffed points of service and administrative sites used to deliver or support the delivery of financial services to microfinance clients.',NULL),(45,'NumberOfOutstandingLoans',3,NULL,0,'The number of loans in the gross loan portfolio. For MFIs using a group lending methodology, the number of loans should refer to the number of individuals receiving loans as part of a group or as part of a group loan.',NULL),(46,'OperatingExpense',3,NULL,3,'Includes expenses not related to financial and credit loss impairment, such as personnel expenses, depreciation, amortization and administrative expenses.',NULL),(47,'OperatingIncome',3,NULL,2,'Includes all financial income and other operating revenue which is generated from non-financial services. Operating income also includes net gains (losses) from holding financial assets (changes on their values during the period and foreign exchange differences). Donations or any revenue not related with an MFI\'s core business of making loans and providing financial services are not considered under this category.',NULL),(48,'WriteOffsOnGrossLoanPortfolio',3,NULL,2,'The value of loans that have been recognized as uncollectible for accounting purposes. A write-off is an accounting procedure that removes the outstanding balance of the loan from the gross loan portfolio and impairment loss allowance. Thus, the write-off does not affect the net loan portfolio, total assets, or any equity account. If the impairment loss allowance is insufficient to cover the amount written off, the excess amount will result in an additional impairment loss on loans recognised in profit or loss of the period.',NULL);
/*!40000 ALTER TABLE `mix_taxonomy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mix_taxonomy_mapping`
--

DROP TABLE IF EXISTS `mix_taxonomy_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mix_taxonomy_mapping` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `identifier` varchar(50) NOT NULL DEFAULT '',
  `config` varchar(200) DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  `currency` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mix_taxonomy_mapping`
--

LOCK TABLES `mix_taxonomy_mapping` WRITE;
/*!40000 ALTER TABLE `mix_taxonomy_mapping` DISABLE KEYS */;
INSERT INTO `mix_taxonomy_mapping` VALUES (1,'default',NULL,NULL,'');
/*!40000 ALTER TABLE `mix_taxonomy_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mix_xbrl_namespace`
--

DROP TABLE IF EXISTS `mix_xbrl_namespace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mix_xbrl_namespace` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `prefix` varchar(20) NOT NULL DEFAULT '',
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQUE` (`prefix`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mix_xbrl_namespace`
--

LOCK TABLES `mix_xbrl_namespace` WRITE;
/*!40000 ALTER TABLE `mix_xbrl_namespace` DISABLE KEYS */;
INSERT INTO `mix_xbrl_namespace` VALUES (1,'ifrs','http://xbrl.iasb.org/taxonomy/2009-04-01/ifrs'),(2,'iso4217','http://www.xbrl.org/2003/iso4217'),(3,'mix','http://www.themix.org/int/fr/ifrs/basi/YYYY-MM-DD/mx-cor'),(4,'xbrldi','http://xbrl.org/2006/xbrldi'),(5,'xbrli','http://www.xbrl.org/2003/instance'),(6,'link','http://www.xbrl.org/2003/linkbase'),(7,'dc-all','http://www.themix.org/int/fr/ifrs/basi/2010-08-31/dc-all');
/*!40000 ALTER TABLE `mix_xbrl_namespace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_likelihoods`
--

DROP TABLE IF EXISTS `ppi_likelihoods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ppi_likelihoods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `name` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ppi_likelihoods`
--

LOCK TABLES `ppi_likelihoods` WRITE;
/*!40000 ALTER TABLE `ppi_likelihoods` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppi_likelihoods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_likelihoods_ppi`
--

DROP TABLE IF EXISTS `ppi_likelihoods_ppi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ppi_likelihoods_ppi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `likelihood_id` bigint(20) NOT NULL,
  `ppi_name` varchar(250) NOT NULL,
  `enabled` int(11) NOT NULL DEFAULT '100',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ppi_likelihoods_ppi`
--

LOCK TABLES `ppi_likelihoods_ppi` WRITE;
/*!40000 ALTER TABLE `ppi_likelihoods_ppi` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppi_likelihoods_ppi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_scores`
--

DROP TABLE IF EXISTS `ppi_scores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ppi_scores` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `score_from` int(11) NOT NULL,
  `score_to` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ppi_scores`
--

LOCK TABLES `ppi_scores` WRITE;
/*!40000 ALTER TABLE `ppi_scores` DISABLE KEYS */;
INSERT INTO `ppi_scores` VALUES (1,0,4),(2,5,9),(3,10,14),(4,15,19),(5,20,24),(6,25,29),(7,30,34),(8,35,39),(9,40,44),(10,45,49),(11,50,54),(12,55,59),(13,60,64),(14,65,69),(15,70,74),(16,75,79),(17,80,84),(18,85,89),(19,90,94),(20,95,100);
/*!40000 ALTER TABLE `ppi_scores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `r_enum_value`
--

DROP TABLE IF EXISTS `r_enum_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_enum_value` (
  `enum_name` varchar(100) NOT NULL,
  `enum_id` int(11) NOT NULL,
  `enum_message_property` varchar(100) NOT NULL,
  `enum_value` varchar(100) NOT NULL,
  `enum_type` tinyint(1) NOT NULL,
  PRIMARY KEY (`enum_name`,`enum_id`),
  UNIQUE KEY `enum_message_property` (`enum_name`,`enum_message_property`),
  UNIQUE KEY `enum_value` (`enum_name`,`enum_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `r_enum_value`
--

LOCK TABLES `r_enum_value` WRITE;
/*!40000 ALTER TABLE `r_enum_value` DISABLE KEYS */;
INSERT INTO `r_enum_value` VALUES ('amortization_method_enum',0,'Equal principle payments','Equal principle payments',0),('amortization_method_enum',1,'Equal installments','Equal installments',0),('interest_calculated_in_period_enum',0,'Daily','Daily',0),('interest_calculated_in_period_enum',1,'Same as repayment period','Same as repayment period',0),('interest_method_enum',0,'Declining Balance','Declining Balance',0),('interest_method_enum',1,'Flat','Flat',0),('interest_period_frequency_enum',2,'Per month','Per month',0),('interest_period_frequency_enum',3,'Per year','Per year',0),('loan_status_id',0,'Invalid','Invalid',0),('loan_status_id',100,'Submitted and awaiting approval','Submitted and awaiting approval',0),('loan_status_id',200,'Approved','Approved',0),('loan_status_id',300,'Active','Active',0),('loan_status_id',400,'Withdrawn by client','Withdrawn by client',0),('loan_status_id',500,'Rejected','Rejected',0),('loan_status_id',600,'Closed','Closed',0),('loan_status_id',601,'Written-Off','Written-Off',0),('loan_status_id',602,'Rescheduled','Rescheduled',0),('loan_status_id',700,'Overpaid','Overpaid',0),('loan_transaction_strategy_id',1,'mifos-standard-strategy','Mifos style',0),('loan_transaction_strategy_id',2,'heavensfamily-strategy','Heavensfamily',0),('loan_transaction_strategy_id',3,'creocore-strategy','Creocore',0),('loan_transaction_strategy_id',4,'rbi-india-strategy','RBI (India)',0),('processing_result_enum',0,'invalid','Invalid',0),('processing_result_enum',1,'processed','Processed',0),('processing_result_enum',2,'awaiting.approval','Awaiting Approval',0),('processing_result_enum',3,'rejected','Rejected',0),('repayment_period_frequency_enum',0,'Days','Days',0),('repayment_period_frequency_enum',1,'Weeks','Weeks',0),('repayment_period_frequency_enum',2,'Months','Months',0),('savings_transaction_type_enum',1,'deposit','deposit',0),('savings_transaction_type_enum',2,'withdrawal','withdrawal',1),('savings_transaction_type_enum',3,'Interest Posting','Interest Posting',0),('savings_transaction_type_enum',4,'Withdrawal Fee','Withdrawal Fee',1),('savings_transaction_type_enum',5,'Annual Fee','Annual Fee',1),('savings_transaction_type_enum',6,'Waive Charge','Waive Charge',0),('savings_transaction_type_enum',7,'Pay Charge','Pay Charge',1),('savings_transaction_type_enum',12,'Initiate Transfer','Initiate Transfer',0),('savings_transaction_type_enum',13,'Approve Transfer','Approve Transfer',0),('savings_transaction_type_enum',14,'Withdraw Transfer','Withdraw Transfer',0),('savings_transaction_type_enum',15,'Reject Transfer','Reject Transfer',0),('savings_transaction_type_enum',16,'Written-Off','Written-Off',0),('savings_transaction_type_enum',17,'Overdraft Interest','Overdraft Interest',0),('status_enum',0,'Invalid','Invalid',0),('status_enum',100,'Pending','Pending',0),('status_enum',300,'Active','Active',0),('status_enum',600,'Closed','Closed',0),('teller_status',300,'Active','Active',0),('teller_status',400,'Inactive','Inactive',0),('teller_status',600,'Closed','Closed',0),('term_period_frequency_enum',0,'Days','Days',0),('term_period_frequency_enum',1,'Weeks','Weeks',0),('term_period_frequency_enum',2,'Months','Months',0),('term_period_frequency_enum',3,'Years','Years',0),('transaction_type_enum',1,'Disbursement','Disbursement',0),('transaction_type_enum',2,'Repayment','Repayment',0),('transaction_type_enum',3,'Contra','Contra',0),('transaction_type_enum',4,'Waive Interest','Waive Interest',0),('transaction_type_enum',5,'Repayment At Disbursement','Repayment At Disbursement',0),('transaction_type_enum',6,'Write-Off','Write-Off',0),('transaction_type_enum',7,'Marked for Rescheduling','Marked for Rescheduling',0),('transaction_type_enum',8,'Recovery Repayment','Recovery Repayment',0),('transaction_type_enum',9,'Waive Charges','Waive Charges',0),('transaction_type_enum',10,'Apply Charges','Apply Charges',0),('transaction_type_enum',11,'Apply Interest','Apply Interest',0);
/*!40000 ALTER TABLE `r_enum_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ref_loan_transaction_processing_strategy`
--

DROP TABLE IF EXISTS `ref_loan_transaction_processing_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ref_loan_transaction_processing_strategy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ltp_strategy_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ref_loan_transaction_processing_strategy`
--

LOCK TABLES `ref_loan_transaction_processing_strategy` WRITE;
/*!40000 ALTER TABLE `ref_loan_transaction_processing_strategy` DISABLE KEYS */;
INSERT INTO `ref_loan_transaction_processing_strategy` VALUES (1,'mifos-standard-strategy','Mifos style'),(2,'heavensfamily-strategy','Heavensfamily'),(3,'creocore-strategy','Creocore'),(4,'rbi-india-strategy','RBI (India)'),(5,'principal-interest-penalties-fees-order-strategy','Principal Interest Penalties Fees Order'),(6,'interest-principal-penalties-fees-order-strategy','Interest Principal Penalties Fees Order'),(7,'early-repayment-strategy','Early Repayment Strategy');
/*!40000 ALTER TABLE `ref_loan_transaction_processing_strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rpt_sequence`
--

DROP TABLE IF EXISTS `rpt_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rpt_sequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rpt_sequence`
--

LOCK TABLES `rpt_sequence` WRITE;
/*!40000 ALTER TABLE `rpt_sequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `rpt_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduler_detail`
--

DROP TABLE IF EXISTS `scheduler_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduler_detail` (
  `id` smallint(2) NOT NULL AUTO_INCREMENT,
  `is_suspended` tinyint(1) NOT NULL DEFAULT '0',
  `execute_misfired_jobs` tinyint(1) NOT NULL DEFAULT '1',
  `reset_scheduler_on_bootup` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduler_detail`
--

LOCK TABLES `scheduler_detail` WRITE;
/*!40000 ALTER TABLE `scheduler_detail` DISABLE KEYS */;
INSERT INTO `scheduler_detail` VALUES (1,0,1,1);
/*!40000 ALTER TABLE `scheduler_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  KEY `schema_version_vr_idx` (`version_rank`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,1,'1','mifosplatform-core-ddl-latest','SQL','V1__mifosplatform-core-ddl-latest.sql',1800446512,'root','2015-06-03 09:56:50',919,1),(10,10,'10','interest-posting-fields-for-savings','SQL','V10__interest-posting-fields-for-savings.sql',921633032,'root','2015-06-03 09:56:51',300,1),(100,100,'100','Group saving summary report','SQL','V100__Group_saving_summary_report.sql',-1635399448,'root','2015-06-03 09:56:58',1,1),(101,101,'101','add mulitplesof to account transfers table','SQL','V101__add_mulitplesof_to_account_transfers_table.sql',693007396,'root','2015-06-03 09:56:58',109,1),(102,102,'102','client attendance tables','SQL','V102__client_attendance_tables.sql',1698025788,'root','2015-06-03 09:56:58',48,1),(103,103,'103','cluster support for batch jobs','SQL','V103__cluster_support_for_batch_jobs.sql',-1566474883,'root','2015-06-03 09:56:58',49,1),(104,104,'104','permissions for transfers','SQL','V104__permissions_for_transfers.sql',1104237193,'root','2015-06-03 09:56:58',2,1),(105,105,'105','track loan transaction against office','SQL','V105__track_loan_transaction_against_office.sql',460401960,'root','2015-06-03 09:56:59',225,1),(106,106,'106','more permissions for transfers','SQL','V106__more_permissions_for_transfers.sql',-1298572248,'root','2015-06-03 09:56:59',4,1),(107,107,'107','datatable code mappings','SQL','V107__datatable_code_mappings.sql',1534043325,'root','2015-06-03 09:56:59',37,1),(108,108,'108','client has transfer office','SQL','V108__client_has_transfer_office.sql',1659150580,'root','2015-06-03 09:56:59',19,1),(109,109,'109','account transfer withdrawal fee configuration','SQL','V109__account_transfer_withdrawal_fee_configuration.sql',1610197449,'root','2015-06-03 09:56:59',80,1),(11,11,'11','add-payment-details','SQL','V11__add-payment-details.sql',662675601,'root','2015-06-03 09:56:52',88,1),(110,110,'110','group center close','SQL','V110__group_center_close.sql',592532718,'root','2015-06-03 09:56:59',38,1),(111,111,'111','disable constraint approach for datatables by default','SQL','V111__disable_constraint_approach_for_datatables_by_default.sql',2058257907,'root','2015-06-03 09:56:59',1,1),(112,112,'111.1','set default transfers in suspense account for existing loan products','SQL','V111_1__set default_transfers_in_suspense_account_for_existing_loan_products.sql',-2022488149,'root','2015-06-03 09:56:59',1,1),(113,113,'112','mixreport sql support','SQL','V112__mixreport_sql_support.sql',1698359442,'root','2015-06-03 09:56:59',84,1),(114,114,'113','track savings transaction against office','SQL','V113__track_savings_transaction_against_office.sql',-443279148,'root','2015-06-03 09:56:59',106,1),(115,115,'114','set default transfers in suspense account for existing savings products - Copy','SQL','V114__set_default_transfers_in_suspense_account_for_existing_savings_products - Copy.sql',-1403120536,'root','2015-06-03 09:56:59',1,1),(116,116,'115','permissions for cache api','SQL','V115__permissions_for_cache_api.sql',-1583242502,'root','2015-06-03 09:56:59',26,1),(117,117,'116','track currency for journal entries','SQL','V116__track_currency_for_journal_entries.sql',-1108820305,'root','2015-06-03 09:56:59',127,1),(118,118,'117','loan charge from savings','SQL','V117__loan_charge_from_savings.sql',294078650,'root','2015-06-03 09:57:00',106,1),(119,119,'118','savings charge','SQL','V118__savings_charge.sql',1334878019,'root','2015-06-03 09:57:00',89,1),(120,120,'118.1','savings charge patch update','SQL','V118_1__savings_charge_patch_update.sql',681062374,'root','2015-06-03 09:57:00',42,1),(121,121,'118.2','product mapping delete duplicate fund source to account mappings','SQL','V118_2__product_mapping_delete_duplicate_fund_source_to_account_mappings.sql',843735115,'root','2015-06-03 09:57:00',0,1),(122,122,'118.3','permissions form propose and accept client transfers','SQL','V118_3__permissions_form_propose_and_accept_client_transfers.sql',-2021972980,'root','2015-06-03 09:57:00',2,1),(123,123,'118.4','reset default transfers in suspense account for existing savings products','SQL','V118_4__reset_default_transfers_in_suspense_account_for_existing_savings_products.sql',-699275732,'root','2015-06-03 09:57:00',1,1),(124,124,'118.5','batch job entry for pay savings charge','SQL','V118_5__batch_job_entry_for_pay_savings_charge.sql',11127915,'root','2015-06-03 09:57:00',1,1),(125,125,'118.6','defaults for income from penalties for savings product','SQL','V118_6__defaults_for_income_from_penalties_for savings_product.sql',-1604260872,'root','2015-06-03 09:57:00',2,1),(126,126,'118.7','move withdrawal annual fee to charges','SQL','V118_7__move_withdrawal_annual_fee_to_charges.sql',486907496,'root','2015-06-03 09:57:00',166,1),(127,127,'118.8','track overpayments seperately in loan transactions','SQL','V118_8__track_overpayments_seperately_in_loan_transactions.sql',-1506771861,'root','2015-06-03 09:57:00',145,1),(128,128,'119','add template table','SQL','V119__add_template_table.sql',-340132249,'root','2015-06-03 09:57:00',45,1),(12,12,'12','add external id to couple of tables','SQL','V12__add_external_id_to_couple_of_tables.sql',1782914953,'root','2015-06-03 09:56:52',214,1),(129,129,'120','accounting running balance','SQL','V120__accounting_running_balance.sql',1553735313,'root','2015-06-03 09:57:00',39,1),(130,130,'121','accounting running balance for organization','SQL','V121__accounting_running_balance_for_organization.sql',-725212393,'root','2015-06-03 09:57:00',63,1),(131,131,'122','recurring fee support for savings','SQL','V122__recurring_fee_support_for_savings.sql',-1243503882,'root','2015-06-03 09:57:01',77,1),(132,132,'123','remove payment mode for savings','SQL','V123__remove_payment_mode_for_savings.sql',-310516979,'root','2015-06-03 09:57:01',52,1),(133,133,'124','added min max cap for charges','SQL','V124__added_min_max_cap_for_charges.sql',1016982354,'root','2015-06-03 09:57:01',71,1),(134,134,'125','added column for actual fee amount or percentage','SQL','V125__added_column_for_actual_fee_amount_or_percentage.sql',-1902751935,'root','2015-06-03 09:57:01',42,1),(135,135,'126','initial database structure for sms outbound','SQL','V126__initial_database_structure_for_sms_outbound.sql',1597367272,'root','2015-06-03 09:57:01',28,1),(136,136,'127','mobile no fields','SQL','V127__mobile_no_fields.sql',-1478167026,'root','2015-06-03 09:57:01',135,1),(137,137,'128','added loan installment charge','SQL','V128__added_loan_installment_charge.sql',-427737636,'root','2015-06-03 09:57:01',24,1),(138,138,'129','client and group timeline','SQL','V129__client_and_group_timeline.sql',-354048349,'root','2015-06-03 09:57:01',114,1),(13,13,'13','add group and client pending configuration','SQL','V13__add_group_and_client_pending_configuration.sql',2043924577,'root','2015-06-03 09:56:52',4,1),(139,139,'130','calendar-history-table','SQL','V130__calendar-history-table.sql',-1068056425,'root','2015-06-03 09:57:01',22,1),(140,140,'131','holiday-status-column-and-permissions','SQL','V131__holiday-status-column-and-permissions.sql',169147264,'root','2015-06-03 09:57:01',89,1),(141,141,'132','borrower cycle changes','SQL','V132__borrower_cycle_changes.sql',-1484124924,'root','2015-06-03 09:57:01',70,1),(142,142,'133','adding payment detail with journal entry','SQL','V133__adding_payment_detail_with_journal_entry.sql',1432448000,'root','2015-06-03 09:57:01',31,1),(143,143,'134','added column value on c configuration','SQL','V134__added_column_value_on_c_configuration.sql',-587477224,'root','2015-06-03 09:57:01',25,1),(144,144,'134.1','submitted date updation for clients','SQL','V134_1__submitted_date_updation_for_clients.sql',-1714823906,'root','2015-06-03 09:57:01',2,1),(145,145,'134.2','permissions spelling correction','SQL','V134_2__permissions_spelling_correction.sql',-1824098129,'root','2015-06-03 09:57:02',1,1),(146,146,'135','added permission for undo written off','SQL','V135__added_permission_for_undo_written_off.sql',426644857,'root','2015-06-03 09:57:02',1,1),(147,147,'136.1','update script strechy parameter','SQL','V136_1__update_script_strechy_parameter.sql',633461657,'root','2015-06-03 09:57:02',1,1),(148,148,'137','added is active column in m staff','SQL','V137__added_is_active_column_in_m_staff.sql',1962782431,'root','2015-06-03 09:57:02',50,1),(149,149,'138','add short name for m product loan and m savings product','SQL','V138__add_short_name_for_m_product_loan_and_m_savings_product.sql',420749751,'root','2015-06-03 09:57:02',265,1),(150,150,'139','default value for is active updated to true in m staff','SQL','V139__default_value_for_is_active_updated_to_true_in_m_staff.sql',-633907272,'root','2015-06-03 09:57:02',45,1),(14,14,'14','rename status id to enum','SQL','V14__rename_status_id_to_enum.sql',-309404445,'root','2015-06-03 09:56:52',55,1),(151,151,'140','added loan charge status','SQL','V140__added_loan_charge_status.sql',701858626,'root','2015-06-03 09:57:02',65,1),(152,152,'140.1','added payment detail id in ac gl journal entry','SQL','V140_1__added_payment_detail_id_in_ac_gl_journal_entry.sql',-2051594288,'root','2015-06-03 09:57:02',33,1),(153,153,'141','add early repayment strategy','SQL','V141__add_early_repayment_strategy.sql',1510094803,'root','2015-06-03 09:57:02',1,1),(154,154,'142','read savingsaccount charge permission','SQL','V142__read_savingsaccount_charge_permission.sql',643820806,'root','2015-06-03 09:57:02',1,1),(155,155,'143','create journalentry checker permission','SQL','V143__create_journalentry_checker_permission.sql',1931469061,'root','2015-06-03 09:57:02',1,1),(156,156,'144','spelling mistake corrections','SQL','V144__spelling_mistake_corrections.sql',196034832,'root','2015-06-03 09:57:02',18,1),(157,157,'145','add force password reset in c configuration','SQL','V145__add_force_password_reset_in_c_configuration.sql',521336058,'root','2015-06-03 09:57:02',164,1),(158,158,'146','tranche loans','SQL','V146__tranche_loans.sql',500763449,'root','2015-06-03 09:57:03',180,1),(159,159,'147','tranche loans column name changes','SQL','V147__tranche_loans_column_name_changes.sql',400468365,'root','2015-06-03 09:57:03',47,1),(160,160,'148','overdraft changes','SQL','V148__overdraft_changes.sql',1529306114,'root','2015-06-03 09:57:03',143,1),(161,161,'149','add created date savings transaction','SQL','V149__add_created_date_savings_transaction.sql',-116162300,'root','2015-06-03 09:57:03',43,1),(15,15,'15','center permissions','SQL','V15__center_permissions.sql',2015498904,'root','2015-06-03 09:56:52',3,1),(162,162,'150','basic savings report','SQL','V150__basic_savings_report.sql',1230058085,'root','2015-06-03 09:57:03',53,1),(163,163,'151','add default savings account to client','SQL','V151__add_default_savings_account_to_client.sql',1421080968,'root','2015-06-03 09:57:03',44,1),(164,164,'152','added grace for over due','SQL','V152__added_grace_for_over_due.sql',-1487653468,'root','2015-06-03 09:57:03',141,1),(165,165,'153','Insert missed permissions','SQL','V153__Insert_missed_permissions.sql',1384787449,'root','2015-06-03 09:57:03',3,1),(166,166,'154','aging details','SQL','V154__aging_details.sql',1117759702,'root','2015-06-03 09:57:03',1,1),(167,167,'155','stretchy into pentaho','SQL','V155__stretchy_into_pentaho.sql',1146009989,'root','2015-06-03 09:57:03',45,1),(168,168,'156','added loan saving txns pentaho','SQL','V156__added_loan_saving_txns_pentaho.sql',1288577073,'root','2015-06-03 09:57:03',4,1),(169,169,'157','overdue charge improvements','SQL','V157__overdue_charge_improvements.sql',-1602717371,'root','2015-06-03 09:57:03',63,1),(170,170,'158','dashboard and navigation queries','SQL','V158__dashboard_and_navigation_queries.sql',555462763,'root','2015-06-03 09:57:03',8,1),(171,171,'159','add transaction id column m portfolio command source','SQL','V159__add_transaction_id_column_m_portfolio_command_source.sql',724056808,'root','2015-06-03 09:57:04',99,1),(16,16,'16','drop min max column on loan table','SQL','V16__drop_min_max_column_on_loan_table.sql',120700748,'root','2015-06-03 09:56:52',83,1),(172,172,'160','standing instruction changes','SQL','V160__standing_instruction_changes.sql',-1634447666,'root','2015-06-03 09:57:04',131,1),(173,173,'160.2','Allow nullValue For principal on lonProduct','SQL','V160_2__Allow_nullValue_For_principal_on_lonProduct.sql',844844635,'root','2015-06-03 09:57:04',103,1),(174,174,'161','added accrual batch job','SQL','V161__added_accrual_batch_job.sql',-760303345,'root','2015-06-03 09:57:04',68,1),(175,175,'162','overdue charge batch job','SQL','V162__overdue_charge_batch_job.sql',-1213828784,'root','2015-06-03 09:57:04',1,1),(176,176,'163','added npa for loans','SQL','V163__added_npa_for_loans.sql',1832640598,'root','2015-06-03 09:57:04',152,1),(177,177,'164','fd and rd deposit tables','SQL','V164__fd_and_rd_deposit_tables.sql',-1202481632,'root','2015-06-03 09:57:04',389,1),(178,178,'165','added permission for disburse to saving account','SQL','V165__added_permission_for_disburse_to_saving_account.sql',-2109143723,'root','2015-06-03 09:57:05',2,1),(179,179,'166','added deposit amount to product term and preclosure','SQL','V166__added_deposit_amount_to_product_term_and_preclosure.sql',-452742507,'root','2015-06-03 09:57:05',81,1),(180,180,'167','added columns for writtenOff loans recovered','SQL','V167__added_columns_for_writtenOff_loans_recovered.sql',384306348,'root','2015-06-03 09:57:05',94,1),(181,181,'168','added transfer fixed deposit interest to linked account','SQL','V168__added_transfer_fixed_deposit_interest_to_linked_account.sql',-246915767,'root','2015-06-03 09:57:05',89,1),(182,182,'169','update dashboard reports to core reports use report to false','SQL','V169__update_dashboard_reports_to_core_reports_use_report_to_false.sql',1910199831,'root','2015-06-03 09:57:05',1,1),(17,17,'17','update stretchy reporting ddl','SQL','V17__update_stretchy_reporting_ddl.sql',-1374690095,'root','2015-06-03 09:56:52',141,1),(183,183,'170','update deposit accounts maturity details job','SQL','V170__update_deposit_accounts_maturity_details_job.sql',348328732,'root','2015-06-03 09:57:05',2,1),(184,184,'171','added mandatory savings and rd changes','SQL','V171__added_mandatory_savings_and_rd_changes.sql',-106401726,'root','2015-06-03 09:57:05',324,1),(185,185,'172','accounting changes for transfers','SQL','V172__accounting_changes_for_transfers.sql',398136509,'root','2015-06-03 09:57:05',58,1),(186,186,'173','ppi','SQL','V173__ppi.sql',-512596643,'root','2015-06-03 09:57:05',107,1),(187,187,'174','remove interest accrual','SQL','V174__remove_interest_accrual.sql',-353161686,'root','2015-06-03 09:57:05',1,1),(188,188,'175','added incentive interest rates','SQL','V175__added_incentive_interest_rates.sql',749853165,'root','2015-06-03 09:57:06',167,1),(189,189,'176','updates to financial activity accounts','SQL','V176__updates_to_financial_activity_accounts.sql',-1274960595,'root','2015-06-03 09:57:06',85,1),(190,190,'177','cleanup for client incentives','SQL','V177__cleanup_for_client_incentives.sql',-1838944707,'root','2015-06-03 09:57:06',2,1),(191,191,'178','updates to financial activity accounts pt2','SQL','V178__updates_to_financial_activity_accounts_pt2.sql',-658545948,'root','2015-06-03 09:57:06',4,1),(192,192,'179','updates to action names for maker checker permissions','SQL','V179__updates_to_action_names_for_maker_checker_permissions.sql',255160379,'root','2015-06-03 09:57:06',4,1),(18,18,'18','update stretchy reporting reportSql','SQL','V18__update_stretchy_reporting_reportSql.sql',1012533433,'root','2015-06-03 09:56:52',4,1),(193,193,'180','update report schemas for disbursed vs awaitingdisbursal and groupnamesbystaff','SQL','V180__update_report_schemas_for_disbursed_vs_awaitingdisbursal_and_groupnamesbystaff.sql',674107071,'root','2015-06-03 09:57:06',2,1),(194,194,'181','standing instruction logging','SQL','V181__standing_instruction_logging.sql',-737719462,'root','2015-06-03 09:57:06',35,1),(195,195,'182','added min required balance to savings product','SQL','V182__added_min_required_balance_to_savings_product.sql',-1648679936,'root','2015-06-03 09:57:06',76,1),(196,196,'183','added min balance for interest calculation','SQL','V183__added_min_balance_for_interest_calculation.sql',1902310539,'root','2015-06-03 09:57:06',105,1),(197,197,'184','update min required balance for savings product','SQL','V184__update_min_required_balance_for_savings_product.sql',313972591,'root','2015-06-03 09:57:06',28,1),(198,198,'185','add accrual till date for periodic accrual','SQL','V185__add_accrual_till_date_for_periodic_accrual.sql',1705918516,'root','2015-06-03 09:57:06',144,1),(199,199,'186','added periodic accrual job','SQL','V186__added_periodic_accrual_job.sql',394599380,'root','2015-06-03 09:57:06',1,1),(200,200,'187','added permission to periodic accrual','SQL','V187__added_permission_to_periodic_accrual.sql',1479836850,'root','2015-06-03 09:57:06',2,1),(201,201,'188','add savingscharge inactivate permissions','SQL','V188__add_savingscharge_inactivate_permissions.sql',-740798972,'root','2015-06-03 09:57:06',41,1),(202,202,'189','m loan interest recalculation tables','SQL','V189__m_loan_interest_recalculation_tables.sql',-873206694,'root','2015-06-03 09:57:07',301,1),(19,19,'19','report maintenance permissions','SQL','V19__report_maintenance_permissions.sql',57066563,'root','2015-06-03 09:56:52',4,1),(203,203,'190','add associategroup disassociategroup permissions','SQL','V190__add_associategroup_disassociategroup_permissions.sql',1517251106,'root','2015-06-03 09:57:07',2,1),(204,204,'191','update gl account increase size of name col','SQL','V191__update_gl_account_increase_size_of_name_col.sql',-1113630867,'root','2015-06-03 09:57:07',17,1),(205,205,'192','interest recalculate job','SQL','V192__interest_recalculate_job.sql',-110230216,'root','2015-06-03 09:57:07',2,1),(206,206,'193','added column joiningDate for staff','SQL','V193__added_column_joiningDate_for_staff.sql',-443555800,'root','2015-06-03 09:57:07',33,1),(207,207,'194','added recalculatedInterestComponent for interest recalculation','SQL','V194__added_recalculatedInterestComponent_for_interest_recalculation.sql',2052912155,'root','2015-06-03 09:57:07',35,1),(208,208,'195','moved rest frequency to product level','SQL','V195__moved_rest_frequency_to_product_level.sql',1459923988,'root','2015-06-03 09:57:07',97,1),(209,209,'196','added loan running balance to transactions','SQL','V196__added_loan_running_balance_to_transactions.sql',342588167,'root','2015-06-03 09:57:07',48,1),(210,210,'197','updated loan running balance of transactions','SQL','V197__updated_loan_running_balance_of_transactions.sql',473184964,'root','2015-06-03 09:57:07',27,1),(211,211,'198','loan rescheduling tables and permissions','SQL','V198__loan_rescheduling_tables_and_permissions.sql',689132282,'root','2015-06-03 09:57:07',128,1),(212,212,'199','removed extra columns from schedule history','SQL','V199__removed_extra_columns_from_schedule_history.sql',1961301885,'root','2015-06-03 09:57:07',105,1),(2,2,'2','mifosx-base-reference-data-utf8','SQL','V2__mifosx-base-reference-data-utf8.sql',2084750372,'root','2015-06-03 09:56:50',30,1),(20,20,'20','report maint perms really configuration','SQL','V20__report_maint_perms_really_configuration.sql',-796088526,'root','2015-06-03 09:56:52',1,1),(213,213,'200','alter savings account for start interest calculation date','SQL','V200__alter_savings_account_for_start_interest_calculation_date.sql',-2046824671,'root','2015-06-03 09:57:07',68,1),(214,214,'201','webhooks','SQL','V201__webhooks.sql',-1852431117,'root','2015-06-03 09:57:08',194,1),(215,215,'202','savings officer history table','SQL','V202__savings_officer_history_table.sql',1515516270,'root','2015-06-03 09:57:08',50,1),(216,216,'203','added subbmittedDate loantransaction','SQL','V203__added_subbmittedDate_loantransaction.sql',762589044,'root','2015-06-03 09:57:08',34,1),(217,217,'204','insert script for charges paid by for accruals','SQL','V204__insert_script_for_charges_paid_by_for_accruals.sql',1126139057,'root','2015-06-03 09:57:08',2,1),(218,218,'205','fix for charge and interest waiver with accruals','SQL','V205__fix_for_charge_and_interest_waiver_with_accruals.sql',1834454603,'root','2015-06-03 09:57:08',91,1),(219,219,'206','interest posting configuration','SQL','V206__interest_posting_configuration.sql',1777902577,'root','2015-06-03 09:57:08',52,1),(220,220,'207','min max clients per group','SQL','V207__min_max_clients_per_group.sql',-1776502977,'root','2015-06-03 09:57:08',46,1),(221,221,'208','min max clients in group redux','SQL','V208__min_max_clients_in_group_redux.sql',-1881405737,'root','2015-06-03 09:57:08',47,1),(222,222,'209','add all report names in m permission table','SQL','V209__add_all_report_names_in_m_permission_table.sql',1034507855,'root','2015-06-03 09:57:08',9,1),(21,21,'21','activation-permissions-for-clients','SQL','V21__activation-permissions-for-clients.sql',1289685589,'root','2015-06-03 09:56:52',45,1),(223,223,'210','track manually adjusted transactions','SQL','V210__track_manually_adjusted_transactions.sql',-1444778976,'root','2015-06-03 09:57:08',47,1),(224,224,'211','minimum days between disbursal and first repayment','SQL','V211__minimum_days_between_disbursal_and_first_repayment.sql',1660532746,'root','2015-06-03 09:57:08',51,1),(225,225,'212','add NthDay and DayOfWeek columns loan','SQL','V212__add_NthDay_and_DayOfWeek_columns_loan.sql',-924123306,'root','2015-06-03 09:57:08',100,1),(226,226,'213','NthDay and DayOfWeek columns should be nullable','SQL','V213__NthDay_and_DayOfWeek_columns_should_be_nullable.sql',-240730886,'root','2015-06-03 09:57:09',135,1),(227,227,'214','alter table add create SI at disbursement','SQL','V214__alter_table_add_create_SI_at_disbursement.sql',-473599398,'root','2015-06-03 09:57:09',272,1),(228,228,'215','guarantee on hold fund changes','SQL','V215__guarantee_on_hold_fund_changes.sql',111795154,'root','2015-06-03 09:57:09',235,1),(229,229,'216','adding loan proposed amount to loan','SQL','V216__adding_loan_proposed_amount_to_loan.sql',2123229215,'root','2015-06-03 09:57:09',109,1),(230,230,'217','client substatus and codevalue description','SQL','V217__client_substatus_and_codevalue_description.sql',1344632615,'root','2015-06-03 09:57:09',88,1),(231,231,'218','add user and datetime for loan savings transactions','SQL','V218__add_user_and_datetime_for_loan_savings_transactions.sql',939524468,'root','2015-06-03 09:57:10',115,1),(232,232,'219','guarantor on hold fund changes for account','SQL','V219__guarantor_on_hold_fund_changes_for_account.sql',-380048619,'root','2015-06-03 09:57:10',327,1),(22,22,'22','alter-group-for-consistency-add-permissions','SQL','V22__alter-group-for-consistency-add-permissions.sql',1509095759,'root','2015-06-03 09:56:52',130,1),(233,233,'220','account number preferences','SQL','V220__account_number_preferences.sql',765825838,'root','2015-06-03 09:57:10',22,1),(234,234,'221','add version for m savings account','SQL','V221__add_version_for_m_savings_account.sql',-2026329127,'root','2015-06-03 09:57:10',49,1),(235,235,'222','guarantor on hold fund changes for transactions','SQL','V222__guarantor_on_hold_fund_changes_for_transactions.sql',-426530719,'root','2015-06-03 09:57:10',193,1),(236,236,'223','add version for m loan account','SQL','V223__add_version_for_m_loan_account.sql',-1129379217,'root','2015-06-03 09:57:10',134,1),(237,237,'224','client lifecycle adding statuses','SQL','V224__client_lifecycle_adding_statuses.sql',43152274,'root','2015-06-03 09:57:11',181,1),(238,238,'225','permissions for updating recurring deposit amount','SQL','V225__permissions_for_updating_recurring_deposit_amount.sql',-564145896,'root','2015-06-03 09:57:11',1,1),(239,239,'226','configuration for enforcing calendars for jlg loans','SQL','V226__configuration_for_enforcing_calendars_for_jlg_loans.sql',-382855919,'root','2015-06-03 09:57:11',1,1),(240,240,'227','loan-refund-permissions','SQL','V227__loan-refund-permissions.sql',-511074400,'root','2015-06-03 09:57:11',2,1),(241,241,'228','entity to entity access','SQL','V228__entity_to_entity_access.sql',-409076299,'root','2015-06-03 09:57:11',108,1),(242,242,'229','teller cash management','SQL','V229__teller_cash_management.sql',2147103896,'root','2015-06-03 09:57:11',83,1),(23,23,'23','remove-enable-disable-configuration-for-client-group-status','SQL','V23__remove-enable-disable-configuration-for-client-group-status.sql',1496208571,'root','2015-06-03 09:56:53',67,1),(243,243,'230','role status and correspoding permissions','SQL','V230__role_status_and_correspoding_permissions.sql',-21174595,'root','2015-06-03 09:57:11',34,1),(244,244,'231','m cashier transaction added currency code','SQL','V231__m_cashier_transaction_added_currency_code.sql',-1593672561,'root','2015-06-03 09:57:11',32,1),(245,245,'232','insert center closure reason','SQL','V232__insert_center_closure_reason.sql',-2049914418,'root','2015-06-03 09:57:11',1,1),(246,246,'233','Savings Transaction Receipt','SQL','V233__Savings_Transaction_Receipt.sql',1836289382,'root','2015-06-03 09:57:11',2,1),(247,247,'234','opening balaces setup','SQL','V234__opening_balaces_setup.sql',1777198314,'root','2015-06-03 09:57:11',23,1),(248,248,'235','add ugd template id m hook','SQL','V235__add_ugd_template_id_m_hook.sql',1120955673,'root','2015-06-03 09:57:11',81,1),(249,249,'236','individual collection sheet permissions','SQL','V236__individual_collection_sheet_permissions.sql',-66130238,'root','2015-06-03 09:57:11',1,1),(250,250,'237','add threshold config for last instalment','SQL','V237__add_threshold_config_for_last_instalment.sql',412873149,'root','2015-06-03 09:57:11',59,1),(251,251,'238','update staff display name length','SQL','V238__update_staff_display_name_length.sql',-1003425306,'root','2015-06-03 09:57:11',47,1),(252,252,'239','Loan Transaction Receipt','SQL','V239__Loan_Transaction_Receipt.sql',-130819179,'root','2015-06-03 09:57:11',2,1),(24,24,'24','add-group-client-foreign-key-constraint-in-loan-table','SQL','V24__add-group-client-foreign-key-constraint-in-loan-table.sql',1077707078,'root','2015-06-03 09:56:53',141,1),(253,253,'240','arrears aging config for interest recalculation','SQL','V240__arrears_aging_config_for_interest_recalculation.sql',674368034,'root','2015-06-03 09:57:12',129,1),(254,254,'241','fixed emi changes','SQL','V241__fixed_emi_changes.sql',1943069939,'root','2015-06-03 09:57:12',53,1),(255,255,'242','entitytoentitymappingrelation','SQL','V242__entitytoentitymappingrelation.sql',-1770973716,'root','2015-06-03 09:57:12',40,1),(256,256,'243','alter loan disbursement details','SQL','V243__alter_loan_disbursement_details.sql',1461060824,'root','2015-06-03 09:57:12',48,1),(257,257,'244','staff assignment history table','SQL','V244__staff_assignment_history_table.sql',-427095856,'root','2015-06-03 09:57:12',33,1),(258,258,'245','open rd changes','SQL','V245__open_rd_changes.sql',2142566381,'root','2015-06-03 09:57:12',1,1),(259,259,'246','drop group client foreign key from m loan','SQL','V246__drop_group_client_foreign_key_from_m_loan.sql',-1721132405,'root','2015-06-03 09:57:12',35,1),(260,260,'247','consistency wrt spelling principalThresholdForLastInstalment','SQL','V247__consistency_wrt_spelling_principalThresholdForLastInstalment.sql',1371980378,'root','2015-06-03 09:57:12',7,1),(261,261,'248','added password never expired to User','SQL','V248__added_password_never_expired_to_User.sql',-1800179163,'root','2015-06-03 09:57:12',43,1),(262,262,'249','workingdays permissions','SQL','V249__workingdays_permissions.sql',-1322891155,'root','2015-06-03 09:57:12',1,1),(25,25,'25','update client reports for status and activation change','SQL','V25__update_client_reports_for_status_and_activation_change.sql',-6310920,'root','2015-06-03 09:56:53',3,1),(263,263,'250','password validation policy','SQL','V250__password_validation_policy.sql',1197290340,'root','2015-06-03 09:57:12',26,1),(264,264,'251','paymentType table','SQL','V251__paymentType_table.sql',-1969329175,'root','2015-06-03 09:57:12',75,1),(265,265,'252','bug fix teller cash management','SQL','V252__bug_fix_teller_cash_management.sql',-736743970,'root','2015-06-03 09:57:12',110,1),(266,266,'253','product loan configurable attributes','SQL','V253__product_loan_configurable_attributes.sql',1787268316,'root','2015-06-03 09:57:12',16,1),(267,267,'254','General Ledger Report','SQL','V254__General_Ledger_Report.sql',-186920768,'root','2015-06-03 09:57:12',4,1),(268,268,'255','pre close interest period config','SQL','V255__pre_close_interest_period_config.sql',1383225707,'root','2015-06-03 09:57:12',26,1),(269,269,'256','Update script for General Ledger report','SQL','V256__Update script for General_Ledger_report.sql',952686971,'root','2015-06-03 09:57:12',3,1),(270,270,'257','staff image association','SQL','V257__staff_image_association.sql',1740118046,'root','2015-06-03 09:57:12',41,1),(271,271,'258','interest compounding changes','SQL','V258__interest_compounding_changes.sql',1484848861,'root','2015-06-03 09:57:13',84,1),(272,272,'259','alter working days','SQL','V259__alter_working_days.sql',1733733251,'root','2015-06-03 09:57:13',28,1),(26,26,'26','add-support-for-withdrawal-fees-on-savings','SQL','V26__add-support-for-withdrawal-fees-on-savings.sql',-755832247,'root','2015-06-03 09:56:53',176,1),(273,273,'260','alter password validation policy','SQL','V260__alter_password_validation_policy.sql',-853716637,'root','2015-06-03 09:57:13',28,1),(27,27,'27','add-loan-type-column-to-loan-table','SQL','V27__add-loan-type-column-to-loan-table.sql',-2130377861,'root','2015-06-03 09:56:53',97,1),(28,28,'28','accounting-abstractions-and-autoposting','SQL','V28__accounting-abstractions-and-autoposting.sql',-626584837,'root','2015-06-03 09:56:53',63,1),(29,29,'29','add-support-for-annual-fees-on-savings','SQL','V29__add-support-for-annual-fees-on-savings.sql',-1595233842,'root','2015-06-03 09:56:53',157,1),(3,3,'3','mifosx-permissions-and-authorisation-utf8','SQL','V3__mifosx-permissions-and-authorisation-utf8.sql',914436650,'root','2015-06-03 09:56:50',14,1),(30,30,'30','add-referenceNumber-to-acc gl journal entry','SQL','V30__add-referenceNumber-to-acc_gl_journal_entry.sql',255130282,'root','2015-06-03 09:56:53',59,1),(31,31,'31','drop-autopostings','SQL','V31__drop-autopostings.sql',-2072166818,'root','2015-06-03 09:56:53',5,1),(32,32,'32','associate-disassociate-clients-from-group-permissions','SQL','V32__associate-disassociate-clients-from-group-permissions.sql',-947369256,'root','2015-06-03 09:56:53',2,1),(33,33,'33','drop unique check on stretchy report parameter','SQL','V33__drop_unique_check_on_stretchy_report_parameter.sql',-1599579296,'root','2015-06-03 09:56:53',23,1),(34,34,'34','add unique check on stretchy report parameter','SQL','V34__add_unique_check_on_stretchy_report_parameter.sql',-1286928230,'root','2015-06-03 09:56:53',22,1),(35,35,'35','add hierarchy column for acc gl account','SQL','V35__add_hierarchy_column_for_acc_gl_account.sql',-1387013309,'root','2015-06-03 09:56:54',49,1),(36,36,'36','add tag id column for acc gl account','SQL','V36__add_tag_id_column_for_acc_gl_account.sql',414916166,'root','2015-06-03 09:56:54',26,1),(37,37,'37','add-center-group-collection-sheet-permissions','SQL','V37__add-center-group-collection-sheet-permissions.sql',-1268924209,'root','2015-06-03 09:56:54',2,1),(38,38,'38','add-group-summary-details-report','SQL','V38__add-group-summary-details-report.sql',-1875404314,'root','2015-06-03 09:56:54',2,1),(39,39,'39','payment-channels-updates','SQL','V39__payment-channels-updates.sql',598090296,'root','2015-06-03 09:56:54',69,1),(4,4,'4','mifosx-core-reports-utf8','SQL','V4__mifosx-core-reports-utf8.sql',-1810870969,'root','2015-06-03 09:56:50',54,1),(40,40,'40','add permissions for accounting rule','SQL','V40__add_permissions_for_accounting_rule.sql',1514233058,'root','2015-06-03 09:56:54',2,1),(41,41,'41','group-summary-reports','SQL','V41__group-summary-reports.sql',1425507637,'root','2015-06-03 09:56:54',4,1),(42,42,'42','Add default value for id for acc accounting rule','SQL','V42__Add_default_value_for_id_for_acc_accounting_rule.sql',2059436790,'root','2015-06-03 09:56:54',25,1),(43,43,'43','accounting-for-savings','SQL','V43__accounting-for-savings.sql',-1542089062,'root','2015-06-03 09:56:54',180,1),(44,44,'44','document-increase-size-of-column-type','SQL','V44__document-increase-size-of-column-type.sql',1059756585,'root','2015-06-03 09:56:54',57,1),(45,45,'45','create acc rule tags table','SQL','V45__create_acc_rule_tags_table.sql',-1290711661,'root','2015-06-03 09:56:54',11,1),(46,46,'46','extend datatables api','SQL','V46__extend_datatables_api.sql',1658150926,'root','2015-06-03 09:56:54',2,1),(47,47,'47','staff-hierarchy-link-to-users','SQL','V47__staff-hierarchy-link-to-users.sql',1571520914,'root','2015-06-03 09:56:54',103,1),(48,48,'48','adding-S3-Support','SQL','V48__adding-S3-Support.sql',-767612266,'root','2015-06-03 09:56:54',164,1),(49,49,'49','track-loan-charge-payment-transactions','SQL','V49__track-loan-charge-payment-transactions.sql',-1735511516,'root','2015-06-03 09:56:54',24,1),(5,5,'5','update-savings-product-and-account-tables','SQL','V5__update-savings-product-and-account-tables.sql',1349701479,'root','2015-06-03 09:56:51',122,1),(50,50,'50','add-grace-settings-to-loan-product','SQL','V50__add-grace-settings-to-loan-product.sql',-1807166173,'root','2015-06-03 09:56:55',140,1),(51,51,'51','track-additional-details-related-to-installment-performance','SQL','V51__track-additional-details-related-to-installment-performance.sql',729891777,'root','2015-06-03 09:56:55',102,1),(52,52,'52','add boolean support cols to acc accounting rule','SQL','V52__add_boolean_support_cols_to_acc_accounting_rule.sql',1853745947,'root','2015-06-03 09:56:55',71,1),(53,53,'53','track-advance-and-late-payments-on-installment','SQL','V53__track-advance-and-late-payments-on-installment.sql',1135041990,'root','2015-06-03 09:56:55',45,1),(54,54,'54','charge-to-income-account-mappings','SQL','V54__charge-to-income-account-mappings.sql',-302610090,'root','2015-06-03 09:56:55',25,1),(55,55,'55','add-additional-transaction-processing-strategies','SQL','V55__add-additional-transaction-processing-strategies.sql',-1366608716,'root','2015-06-03 09:56:55',58,1),(56,56,'56','track-overpaid-amount-on-loans','SQL','V56__track-overpaid-amount-on-loans.sql',-2006962467,'root','2015-06-03 09:56:55',99,1),(57,57,'57','add default values to debit and credit accounts acc accounting rule','SQL','V57__add_default_values_to_debit_and_credit_accounts_acc_accounting_rule.sql',1041459650,'root','2015-06-03 09:56:55',51,1),(58,58,'58','create-holiday-tables changed','SQL','V58__create-holiday-tables_changed.sql',-1395337105,'root','2015-06-03 09:56:55',150,1),(59,59,'59','add group roles schema and permissions','SQL','V59__add_group_roles_schema_and_permissions.sql',-406383935,'root','2015-06-03 09:56:55',55,1),(6,6,'6','add min max principal column to loan','SQL','V6__add_min_max_principal_column_to_loan.sql',-907223871,'root','2015-06-03 09:56:51',140,1),(60,60,'60','quipo dashboard reports','SQL','V60__quipo_dashboard_reports.sql',-1618354471,'root','2015-06-03 09:56:56',13,1),(61,61,'61','txn running balance example','SQL','V61__txn_running_balance_example.sql',-80025043,'root','2015-06-03 09:56:56',4,1),(62,62,'62','add staff id to m client changed','SQL','V62__add_staff_id_to_m_client_changed.sql',1148218006,'root','2015-06-03 09:56:56',18,1),(63,63,'63','add sync disbursement with meeting column to loan','SQL','V63__add_sync_disbursement_with_meeting_column_to_loan.sql',1201879376,'root','2015-06-03 09:56:56',85,1),(64,64,'64','add permission for assign staff','SQL','V64__add_permission_for_assign_staff.sql',-1938102414,'root','2015-06-03 09:56:56',1,1),(65,65,'65','fix rupee symbol issues','SQL','V65__fix_rupee_symbol_issues.sql',1008895069,'root','2015-06-03 09:56:56',1,1),(66,66,'66','client close functionality','SQL','V66__client_close_functionality.sql',-142847690,'root','2015-06-03 09:56:56',38,1),(67,67,'67','loans in advance table','SQL','V67__loans_in_advance_table.sql',1665941254,'root','2015-06-03 09:56:56',21,1),(68,68,'68','quipo dashboard reports updated','SQL','V68__quipo_dashboard_reports_updated.sql',1746719914,'root','2015-06-03 09:56:56',14,1),(69,69,'69','loans in advance initialise','SQL','V69__loans_in_advance_initialise.sql',1518847594,'root','2015-06-03 09:56:56',8,1),(7,7,'7','remove read makerchecker permission','SQL','V7__remove_read_makerchecker_permission.sql',-569619336,'root','2015-06-03 09:56:51',2,1),(70,70,'70','quipo program detail query fix','SQL','V70__quipo_program_detail_query_fix.sql',1892537189,'root','2015-06-03 09:56:56',1,1),(71,71,'71','insert reschedule repayment to configuration','SQL','V71__insert_reschedule_repayment_to_configuration.sql',-1148306529,'root','2015-06-03 09:56:56',1,1),(72,72,'72','add m loan counter changes','SQL','V72__add_m_loan_counter_changes.sql',878990870,'root','2015-06-03 09:56:56',63,1),(73,73,'73','add repayments rescheduled to and processed column to holiday','SQL','V73__add_repayments_rescheduled_to_and_processed_column_to_holiday.sql',-503832337,'root','2015-06-03 09:56:56',154,1),(74,74,'74','alter m loan counter table add group','SQL','V74__alter_m_loan_counter_table_add_group.sql',-2117284805,'root','2015-06-03 09:56:56',94,1),(75,75,'75','add reschedule-repayments-on-holidays to configuration','SQL','V75__add_reschedule-repayments-on-holidays_to_configuration.sql',1328301697,'root','2015-06-03 09:56:56',1,1),(76,76,'76','rename permission grouping','SQL','V76__rename_permission_grouping.sql',782643717,'root','2015-06-03 09:56:56',3,1),(77,77,'77','alter m product loan changes','SQL','V77__alter_m_product_loan_changes.sql',-1168017986,'root','2015-06-03 09:56:56',87,1),(78,78,'78','breakdown portfolio grouping','SQL','V78__breakdown_portfolio_grouping.sql',-1151517023,'root','2015-06-03 09:56:56',2,1),(79,79,'79','schedule jobs tables','SQL','V79__schedule_jobs_tables.sql',-648184231,'root','2015-06-03 09:56:57',81,1),(8,8,'8','deposit-transaction-permissions-if-they-exist','SQL','V8__deposit-transaction-permissions-if-they-exist.sql',1925296214,'root','2015-06-03 09:56:51',1,1),(80,80,'80','schedule jobs tables updates','SQL','V80__schedule_jobs_tables_updates.sql',427508507,'root','2015-06-03 09:56:57',74,1),(81,81,'81','savings related changes','SQL','V81__savings_related_changes.sql',1538092549,'root','2015-06-03 09:56:57',247,1),(82,82,'82','schedule jobs tables updates for running status','SQL','V82__schedule_jobs_tables_updates_for_running_status.sql',-1900600035,'root','2015-06-03 09:56:57',213,1),(83,83,'83','non-working-days-table','SQL','V83__non-working-days-table.sql',7304707,'root','2015-06-03 09:56:57',52,1),(84,84,'84','undo savings transaction permission','SQL','V84__undo_savings_transaction_permission.sql',-1712636214,'root','2015-06-03 09:56:57',1,1),(85,85,'85','product mix related changes','SQL','V85__product_mix_related_changes.sql',-1558734721,'root','2015-06-03 09:56:57',117,1),(86,86,'86','update-working-days','SQL','V86__update-working-days.sql',1751835641,'root','2015-06-03 09:56:57',6,1),(87,87,'87','add permission for scheduler','SQL','V87__add_permission_for_scheduler.sql',-575950289,'root','2015-06-03 09:56:57',1,1),(88,88,'88','added update constrain for scheduler jobs','SQL','V88__added_update_constrain_for_scheduler_jobs.sql',-897794717,'root','2015-06-03 09:56:57',27,1),(89,89,'89','added scheduler group','SQL','V89__added_scheduler_group.sql',-1570560491,'root','2015-06-03 09:56:57',32,1),(9,9,'9','add min max constraint column to loan loanproduct','SQL','V9__add_min_max_constraint_column_to_loan_loanproduct.sql',-709167892,'root','2015-06-03 09:56:51',317,1),(90,90,'90','client performance history reports','SQL','V90__client_performance_history_reports.sql',-358569421,'root','2015-06-03 09:56:58',2,1),(91,91,'91','apply annual fees permission','SQL','V91__apply_annual_fees_permission.sql',1152030995,'root','2015-06-03 09:56:58',2,1),(92,92,'91.1','configuration settings for holiday and non workingday','SQL','V91_1__configuration_settings_for_holiday_and_non_workingday.sql',2070643129,'root','2015-06-03 09:56:58',2,1),(93,93,'92','group center assign staff permission','SQL','V92__group_center_assign_staff_permission.sql',-1280675809,'root','2015-06-03 09:56:58',2,1),(94,94,'93','loan transaction external id','SQL','V93__loan_transaction_external_id.sql',1766682107,'root','2015-06-03 09:56:58',49,1),(95,95,'94','added savings accont type','SQL','V94__added_savings_accont type.sql',-947513684,'root','2015-06-03 09:56:58',68,1),(96,96,'95','batch job postInterest','SQL','V95__batch_job_postInterest.sql',2096051563,'root','2015-06-03 09:56:58',1,1),(97,97,'96','savings accounts transfers table','SQL','V96__savings_accounts_transfers_table.sql',1113386790,'root','2015-06-03 09:56:58',61,1),(98,98,'97','add permission for adjust savings transaction','SQL','V97__add_permission_for_adjust_savings_transaction.sql',-2045732265,'root','2015-06-03 09:56:58',2,1),(99,99,'98','added currency roundof for multipleof','SQL','V98__added_currency_roundof_for_multipleof.sql',-107928515,'root','2015-06-03 09:56:58',325,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_messages_outbound`
--

DROP TABLE IF EXISTS `sms_messages_outbound`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms_messages_outbound` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '100',
  `mobile_no` varchar(50) NOT NULL,
  `message` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKGROUP000000001` (`group_id`),
  KEY `FKCLIENT00000001` (`client_id`),
  KEY `FKSTAFF000000001` (`staff_id`),
  CONSTRAINT `FKCLIENT00000001` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKGROUP000000001` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `FKSTAFF000000001` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_messages_outbound`
--

LOCK TABLES `sms_messages_outbound` WRITE;
/*!40000 ALTER TABLE `sms_messages_outbound` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_messages_outbound` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stretchy_parameter`
--

DROP TABLE IF EXISTS `stretchy_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stretchy_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parameter_name` varchar(45) NOT NULL,
  `parameter_variable` varchar(45) DEFAULT NULL,
  `parameter_label` varchar(45) NOT NULL,
  `parameter_displayType` varchar(45) NOT NULL,
  `parameter_FormatType` varchar(10) NOT NULL,
  `parameter_default` varchar(45) NOT NULL,
  `special` varchar(1) DEFAULT NULL,
  `selectOne` varchar(1) DEFAULT NULL,
  `selectAll` varchar(1) DEFAULT NULL,
  `parameter_sql` text,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`parameter_name`),
  KEY `fk_stretchy_parameter_001_idx` (`parent_id`),
  CONSTRAINT `fk_stretchy_parameter_001` FOREIGN KEY (`parent_id`) REFERENCES `stretchy_parameter` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1009 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stretchy_parameter`
--

LOCK TABLES `stretchy_parameter` WRITE;
/*!40000 ALTER TABLE `stretchy_parameter` DISABLE KEYS */;
INSERT INTO `stretchy_parameter` VALUES (1,'startDateSelect','startDate','startDate','date','date','today',NULL,NULL,NULL,NULL,NULL),(2,'endDateSelect','endDate','endDate','date','date','today',NULL,NULL,NULL,NULL,NULL),(3,'obligDateTypeSelect','obligDateType','obligDateType','select','number','0',NULL,NULL,NULL,'select * from\r\n(select 1 as id, \"Closed\" as `name` union all\r\nselect 2, \"Disbursal\" ) x\r\norder by x.`id`',NULL),(5,'OfficeIdSelectOne','officeId','Office','select','number','0',NULL,'Y',NULL,'select id, \r\nconcat(substring(\"........................................\", 1, \r\n   \n\n((LENGTH(`hierarchy`) - LENGTH(REPLACE(`hierarchy`, \'.\', \'\')) - 1) * 4)), \r\n   `name`) as tc\r\nfrom m_office\r\nwhere hierarchy like concat\n\n(\'${currentUserHierarchy}\', \'%\')\r\norder by hierarchy',NULL),(6,'loanOfficerIdSelectAll','loanOfficerId','Loan Officer','select','number','0',NULL,NULL,'Y','(select lo.id, lo.display_name as `Name` \r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\njoin m_staff lo on lo.office_id = ounder.id\r\nwhere lo.is_loan_officer = true\r\nand o.id = ${officeId})\r\nunion all\r\n(select -10, \'-\')\r\norder by 2',5),(10,'currencyIdSelectAll','currencyId','Currency','select','number','0',NULL,NULL,'Y','select `code`, `name`\r\nfrom m_organisation_currency\r\norder by `code`',NULL),(20,'fundIdSelectAll','fundId','Fund','select','number','0',NULL,NULL,'Y','(select id, `name`\r\nfrom m_fund)\r\nunion all\r\n(select -10, \'-\')\r\norder by 2',NULL),(25,'loanProductIdSelectAll','loanProductId','Product','select','number','0',NULL,NULL,'Y','select p.id, p.`name`\r\nfrom m_product_loan p\r\nwhere (p.currency_code = \'${currencyId}\' or \'-1\'= \'${currencyId}\')\r\norder by 2',10),(26,'loanPurposeIdSelectAll','loanPurposeId','Loan Purpose','select','number','0',NULL,NULL,'Y','select -10 as id, \'-\' as code_value\r\nunion all\r\nselect * from (select v.id, v.code_value\r\nfrom m_code c\r\njoin m_code_value v on v.code_id = c.id\r\nwhere c.code_name = \"loanPurpose\"\r\norder by v.order_position)  x',NULL),(100,'parTypeSelect','parType','parType','select','number','0',NULL,NULL,NULL,'select * from\r\n(select 1 as id, \"Principal Only\" as `name` union all\r\nselect 2, \"Principal + Interest\" union all\r\nselect 3, \"Principal + Interest + Fees\" union all\r\nselect 4, \"Principal + Interest + Fees + Penalties\") x\r\norder by x.`id`',NULL),(1001,'FullReportList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select  r.id as report_id, r.report_name, r.report_type, r.report_subtype, r.report_category,\nrp.id as parameter_id, rp.report_parameter_name, p.parameter_name\n  from stretchy_report r\n  left join stretchy_report_parameter rp on rp.report_id = r.id \n  left join stretchy_parameter p on p.id = rp.parameter_id\n  where r.use_report is true\n  and exists\n  ( select \'f\'\n  from m_appuser_role ur \n  join m_role r on r.id = ur.role_id\n  join m_role_permission rp on rp.role_id = r.id\n  join m_permission p on p.id = rp.permission_id\n  where ur.appuser_id = ${currentUserId}\n  and (p.code in (\'ALL_FUNCTIONS_READ\', \'ALL_FUNCTIONS\') or p.code = concat(\"READ_\", r.report_name)) )\n  order by r.report_category, r.report_name, rp.id',NULL),(1002,'FullParameterList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select sp.parameter_name, sp.parameter_variable, sp.parameter_label, sp.parameter_displayType, \r sp.parameter_FormatType, sp.parameter_default, sp.selectOne,  sp.selectAll, spp.parameter_name as parentParameterName\r from stretchy_parameter sp\r left join stretchy_parameter spp on spp.id = sp.parent_id\r where sp.special is null\r and exists \r 	(select \'f\' \r 	from stretchy_report sr\r 	join stretchy_report_parameter srp on srp.report_id = sr.id\r 	where sr.report_name in(${reportListing})\r 	and srp.parameter_id = sp.id\r 	)\r order by sp.id',NULL),(1003,'reportCategoryList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select  r.id as report_id, r.report_name, r.report_type, r.report_subtype, r.report_category,\n  rp.id as parameter_id, rp.report_parameter_name, p.parameter_name\n  from stretchy_report r\n  left join stretchy_report_parameter rp on rp.report_id = r.id\n  left join stretchy_parameter p on p.id = rp.parameter_id\n  where r.report_category = \'${reportCategory}\'\n  and r.use_report is true\n  and exists\n  (select \'f\'\n  from m_appuser_role ur \n  join m_role r on r.id = ur.role_id\n  join m_role_permission rp on rp.role_id = r.id\n  join m_permission p on p.id = rp.permission_id\n  where ur.appuser_id = ${currentUserId}\n  and (p.code in (\'ALL_FUNCTIONS_READ\', \'ALL_FUNCTIONS\') or p.code = concat(\"READ_\", r.report_name)) )\n  order by r.report_category, r.report_name, rp.id',NULL),(1004,'selectAccount','accountNo','Enter Account No','text','string','n/a',NULL,NULL,NULL,NULL,NULL),(1005,'savingsProductIdSelectAll','savingsProductId','Product','select','number','0',NULL,NULL,'Y','select p.id, p.`name`\r\nfrom m_savings_product p\r\norder by 2',NULL),(1006,'transactionId','transactionId','transactionId','text','string','n/a',NULL,NULL,NULL,NULL,NULL),(1007,'selectCenterId','centerId','Enter Center Id','text','string','n/a',NULL,NULL,NULL,NULL,NULL),(1008,'SelectGLAccountNO','GLAccountNO','GLAccountNO','select','number','0',NULL,NULL,NULL,'select id aid,name aname\r\nfrom acc_gl_account',NULL);
/*!40000 ALTER TABLE `stretchy_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stretchy_report`
--

DROP TABLE IF EXISTS `stretchy_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stretchy_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_name` varchar(100) NOT NULL,
  `report_type` varchar(20) NOT NULL,
  `report_subtype` varchar(20) DEFAULT NULL,
  `report_category` varchar(45) DEFAULT NULL,
  `report_sql` text,
  `description` text,
  `core_report` tinyint(1) DEFAULT '0',
  `use_report` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_name_UNIQUE` (`report_name`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stretchy_report`
--

LOCK TABLES `stretchy_report` WRITE;
/*!40000 ALTER TABLE `stretchy_report` DISABLE KEYS */;
INSERT INTO `stretchy_report` VALUES (1,'Client Listing','Table',NULL,'Client','select\nconcat(repeat(\"..\",\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\n c.account_no as \"Client Account No.\",\nc.display_name as \"Name\",\nr.enum_message_property as \"Status\",\nc.activation_date as \"Activation\", c.external_id as \"External Id\"\nfrom m_office o\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_client c on c.office_id = ounder.id\nleft join r_enum_value r on r.enum_name = \'status_enum\' and r.enum_id = c.status_enum\nwhere o.id = ${officeId}\norder by ounder.hierarchy, c.account_no','Individual Client Report\r\n\r\nLists the small number of defined fields on the client table.  Would expect to copy this \n\nreport and add any \'one to one\' additional data for specific tenant needs.\r\n\r\nCan be run for any size MFI but you\'d expect it only to be run within a branch for \n\nlarger ones.  Depending on how many columns are displayed, there is probably is a limit of about 20/50k clients returned for html display (export to excel doesn\'t \n\nhave that client browser/memory impact).',1,1),(2,'Client Loans Listing','Table',NULL,'Client','select\nconcat(repeat(\"..\",\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\", c.account_no as \"Client Account No.\",\nc.display_name as \"Name\",\nr.enum_message_property as \"Client Status\",\nlo.display_name as \"Loan Officer\", l.account_no as \"Loan Account No.\", l.external_id as \"External Id\", p.name as Loan, st.enum_message_property as \"Status\",\nf.`name` as Fund, purp.code_value as \"Loan Purpose\",\nifnull(cur.display_symbol, l.currency_code) as Currency,\nl.principal_amount, l.arrearstolerance_amount as \"Arrears Tolerance Amount\",\nl.number_of_repayments as \"Expected No. Repayments\",\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\",\nl.nominal_interest_rate_per_period as \"Nominal Interest Rate Per Period\",\nipf.enum_message_property as \"Interest Rate Frequency\",\nim.enum_message_property as \"Interest Method\",\nicp.enum_message_property as \"Interest Calculated in Period\",\nl.term_frequency as \"Term Frequency\",\ntf.enum_message_property as \"Term Frequency Period\",\nl.repay_every as \"Repayment Frequency\",\nrf.enum_message_property as \"Repayment Frequency Period\",\nam.enum_message_property as \"Amortization\",\nl.total_charges_due_at_disbursement_derived as \"Total Charges Due At Disbursement\",\ndate(l.submittedon_date) as Submitted, date(l.approvedon_date) Approved, l.expected_disbursedon_date As \"Expected Disbursal\",\ndate(l.expected_firstrepaymenton_date) as \"Expected First Repayment\",\ndate(l.interest_calculated_from_date) as \"Interest Calculated From\" ,\ndate(l.disbursedon_date) as Disbursed,\ndate(l.expected_maturedon_date) \"Expected Maturity\",\ndate(l.maturedon_date) as \"Matured On\", date(l.closedon_date) as Closed,\ndate(l.rejectedon_date) as Rejected, date(l.rescheduledon_date) as Rescheduled,\ndate(l.withdrawnon_date) as Withdrawn, date(l.writtenoffon_date) \"Written Off\"\nfrom m_office o\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_client c on c.office_id = ounder.id\nleft join r_enum_value r on r.enum_name = \'status_enum\'\n and r.enum_id = c.status_enum\nleft join m_loan l on l.client_id = c.id\nleft join m_staff lo on lo.id = l.loan_officer_id\nleft join m_product_loan p on p.id = l.product_id\nleft join m_fund f on f.id = l.fund_id\nleft join r_enum_value st on st.enum_name = \"loan_status_id\" and st.enum_id = l.loan_status_id\nleft join r_enum_value ipf on ipf.enum_name = \"interest_period_frequency_enum\"\n and ipf.enum_id = l.interest_period_frequency_enum\nleft join r_enum_value im on im.enum_name = \"interest_method_enum\"\n and im.enum_id = l.interest_method_enum\nleft join r_enum_value tf on tf.enum_name = \"term_period_frequency_enum\"\n and tf.enum_id = l.term_period_frequency_enum\nleft join r_enum_value icp on icp.enum_name = \"interest_calculated_in_period_enum\"\n and icp.enum_id = l.interest_calculated_in_period_enum\nleft join r_enum_value rf on rf.enum_name = \"repayment_period_frequency_enum\"\n and rf.enum_id = l.repayment_period_frequency_enum\nleft join r_enum_value am on am.enum_name = \"amortization_method_enum\"\n and am.enum_id = l.amortization_method_enum\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\nleft join m_currency cur on cur.code = l.currency_code\nwhere o.id = ${officeId}\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\norder by ounder.hierarchy, 2 , l.id','Individual Client Report\r\n\r\nPretty \n\nwide report that lists the basic details of client loans.  \r\n\r\nCan be run for any size MFI but you\'d expect it only to be run within a branch for larger ones.  \n\nThere is probably is a limit of about 20/50k clients returned for html display (export to excel doesn\'t have that client browser/memory impact).',1,1),(5,'Loans Awaiting Disbursal','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nc.account_no as \"Client Account No\", c.display_name as \"Name\", l.account_no as \"Loan Account No.\", pl.`name` as \"Product\", \r\nf.`name` as Fund, ifnull(cur.display_symbol, l.currency_code) as Currency,  \r\nl.principal_amount as Principal,  \r\nl.term_frequency as \"Term Frequency\",\n\n\r\ntf.enum_message_property as \"Term Frequency Period\",\r\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\",\r\ndate(l.approvedon_date) \"Approved\",\r\ndatediff(l.expected_disbursedon_date, curdate()) as \"Days to Disbursal\",\r\ndate(l.expected_disbursedon_date) \"Expected Disbursal\",\r\npurp.code_value as \"Loan Purpose\",\r\n lo.display_name as \"Loan Officer\"\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nleft join r_enum_value tf on tf.enum_name = \"term_period_frequency_enum\" and tf.enum_id = l.term_period_frequency_enum\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 200\r\norder by ounder.hierarchy, datediff(l.expected_disbursedon_date, curdate()),  c.account_no','Individual Client Report',1,1),(6,'Loans Awaiting Disbursal Summary','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\npl.`name` as \"Product\", \r\nifnull(cur.display_symbol, l.currency_code) as Currency,  f.`name` as Fund,\r\nsum(l.principal_amount) as Principal\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 200\r\ngroup by ounder.hierarchy, pl.`name`, l.currency_code,  f.`name`\r\norder by ounder.hierarchy, pl.`name`, l.currency_code,  f.`name`','Individual Client Report',1,1),(7,'Loans Awaiting Disbursal Summary by Month','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\npl.`name` as \"Product\", \r\nifnull(cur.display_symbol, l.currency_code) as Currency,  \r\nyear(l.expected_disbursedon_date) as \"Year\", \r\nmonthname(l.expected_disbursedon_date) as \"Month\",\r\nsum(l.principal_amount) as Principal\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 200\r\ngroup by ounder.hierarchy, pl.`name`, l.currency_code, year(l.expected_disbursedon_date), month(l.expected_disbursedon_date)\r\norder by ounder.hierarchy, pl.`name`, l.currency_code, year(l.expected_disbursedon_date), month(l.expected_disbursedon_date)','Individual Client Report',1,1),(8,'Loans Pending Approval','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nc.account_no as \"Client Account No.\", c.display_name as \"Client Name\", \r\nifnull(cur.display_symbol, l.currency_code) as Currency,  pl.`name` as \"Product\", \r\nl.account_no as \"Loan Account No.\", \r\nl.principal_amount as \"Loan Amount\", \r\nl.term_frequency as \"Term Frequency\",\n\n\r\ntf.enum_message_property as \"Term Frequency Period\",\r\nl.annual_nominal_interest_rate as \" Annual \n\nNominal Interest Rate\", \r\ndatediff(curdate(), l.submittedon_date) \"Days Pending Approval\", \r\npurp.code_value as \"Loan Purpose\",\r\nlo.display_name as \"Loan Officer\"\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nleft join r_enum_value tf on tf.enum_name = \"term_period_frequency_enum\" and tf.enum_id = l.term_period_frequency_enum\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 100 /*Submitted and awaiting approval */\r\norder by ounder.hierarchy, l.submittedon_date,  l.account_no','Individual Client Report',1,1),(11,'Active Loans - Summary','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(mo.`hierarchy`) - LENGTH(REPLACE(mo.`hierarchy`, \'.\', \'\')) - 1))), mo.`name`) as \"Office/Branch\", x.currency as Currency,\r\n x.client_count as \"No. of Clients\", x.active_loan_count as \"No. Active Loans\", x. loans_in_arrears_count as \"No. of Loans in Arrears\",\r\nx.principal as \"Total Loans Disbursed\", x.principal_repaid as \"Principal Repaid\", x.principal_outstanding as \"Principal Outstanding\", x.principal_overdue as \"Principal Overdue\",\r\nx.interest as \"Total Interest\", x.interest_repaid as \"Interest Repaid\", x.interest_outstanding as \"Interest Outstanding\", x.interest_overdue as \"Interest Overdue\",\r\nx.fees as \"Total Fees\", x.fees_repaid as \"Fees Repaid\", x.fees_outstanding as \"Fees Outstanding\", x.fees_overdue as \"Fees Overdue\",\r\nx.penalties as \"Total Penalties\", x.penalties_repaid as \"Penalties Repaid\", x.penalties_outstanding as \"Penalties Outstanding\", x.penalties_overdue as \"Penalties Overdue\",\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.principal_overdue * 100) / x.principal_outstanding, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue + x.penalties_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding + x.penalties_overdue), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from m_office mo\r\njoin \r\n(select ounder.id as branch,\r\nifnull(cur.display_symbol, l.currency_code) as currency,\r\ncount(distinct(c.id)) as client_count, \r\ncount(distinct(l.id)) as  active_loan_count,\r\ncount(distinct(if(laa.loan_id is not null,  l.id, null)  )) as loans_in_arrears_count,\r\n\r\nsum(l.principal_disbursed_derived) as principal,\r\nsum(l.principal_repaid_derived) as principal_repaid,\r\nsum(l.principal_outstanding_derived) as principal_outstanding,\r\nsum(laa.principal_overdue_derived) as principal_overdue,\r\n\r\nsum(l.interest_charged_derived) as interest,\r\nsum(l.interest_repaid_derived) as interest_repaid,\r\nsum(l.interest_outstanding_derived) as interest_outstanding,\r\nsum(laa.interest_overdue_derived) as interest_overdue,\r\n\r\nsum(l.fee_charges_charged_derived) as fees,\r\nsum(l.fee_charges_repaid_derived) as fees_repaid,\r\nsum(l.fee_charges_outstanding_derived)  as fees_outstanding,\r\nsum(laa.fee_charges_overdue_derived) as fees_overdue,\r\n\r\nsum(l.penalty_charges_charged_derived) as penalties,\r\nsum(l.penalty_charges_repaid_derived) as penalties_repaid,\r\nsum(l.penalty_charges_outstanding_derived) as penalties_outstanding,\r\nsum(laa.penalty_charges_overdue_derived) as penalties_overdue\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nleft join m_currency cur on cur.code = l.currency_code\r\n\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by ounder.id, l.currency_code) x on x.branch = mo.id\r\norder by mo.hierarchy, x.Currency',NULL,1,1),(12,'Active Loans - Details','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nlo.display_name as \"Loan Officer\", \r\nc.display_name as \"Client\", l.account_no as \"Loan Account No.\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  \r\nl.principal_amount as \"Loan Amount\", \r\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed Date\", \r\ndate(l.expected_maturedon_date) as \"Expected Matured On\",\r\n\r\nl.principal_repaid_derived as \"Principal Repaid\",\r\nl.principal_outstanding_derived as \"Principal Outstanding\",\r\nlaa.principal_overdue_derived as \"Principal Overdue\",\r\n\r\nl.interest_repaid_derived as \"Interest Repaid\",\r\nl.interest_outstanding_derived as \"Interest Outstanding\",\r\nlaa.interest_overdue_derived as \"Interest Overdue\",\r\n\r\nl.fee_charges_repaid_derived as \"Fees Repaid\",\r\nl.fee_charges_outstanding_derived  as \"Fees Outstanding\",\r\nlaa.fee_charges_overdue_derived as \"Fees Overdue\",\r\n\r\nl.penalty_charges_repaid_derived as \"Penalties Repaid\",\r\nl.penalty_charges_outstanding_derived as \"Penalties Outstanding\",\r\npenalty_charges_overdue_derived as \"Penalties Overdue\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by l.id\r\norder by ounder.hierarchy, l.currency_code, c.account_no, l.account_no','Individual Client \n\nReport',1,1),(13,'Obligation Met Loans Details','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nc.account_no as \"Client Account No.\", c.display_name as \"Client\",\r\nl.account_no as \"Loan Account No.\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  \r\nl.principal_amount as \"Loan Amount\", \r\nl.total_repayment_derived  as \"Total Repaid\", \r\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed\", \r\ndate(l.closedon_date) as \"Closed\",\r\n\r\nl.principal_repaid_derived as \"Principal Repaid\",\r\nl.interest_repaid_derived as \"Interest Repaid\",\r\nl.fee_charges_repaid_derived as \"Fees Repaid\",\r\nl.penalty_charges_repaid_derived as \"Penalties Repaid\",\r\nlo.display_name as \"Loan Officer\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand (case\r\n	when ${obligDateType} = 1 then\r\n    l.closedon_date between \'${startDate}\' and \'${endDate}\'\r\n	when ${obligDateType} = 2 then\r\n    l.disbursedon_date between \'${startDate}\' and \'${endDate}\'\r\n	else 1 = 1\r\n	end)\r\nand l.loan_status_id = 600\r\ngroup by l.id\r\norder by ounder.hierarchy, l.currency_code, c.account_no, l.account_no','Individual Client \n\nReport',1,1),(14,'Obligation Met Loans Summary','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\ncount(distinct(c.id)) as \"No. of Clients\",\r\ncount(distinct(l.id)) as \"No. of Loans\",\r\nsum(l.principal_amount) as \"Total Loan Amount\", \r\nsum(l.principal_repaid_derived) as \"Total Principal Repaid\",\r\nsum(l.interest_repaid_derived) as \"Total Interest Repaid\",\r\nsum(l.fee_charges_repaid_derived) as \"Total Fees Repaid\",\r\nsum(l.penalty_charges_repaid_derived) as \"Total Penalties Repaid\",\r\nsum(l.interest_waived_derived) as \"Total Interest Waived\",\r\nsum(l.fee_charges_waived_derived) as \"Total Fees Waived\",\r\nsum(l.penalty_charges_waived_derived) as \"Total Penalties Waived\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand (case\r\n	when ${obligDateType} = 1 then\r\n    l.closedon_date between \'${startDate}\' and \'${endDate}\'\r\n	when ${obligDateType} = 2 then\r\n    l.disbursedon_date between \'${startDate}\' and \'${endDate}\'\r\n	else 1 = 1\r\n	end)\r\nand l.loan_status_id = 600\r\ngroup by ounder.hierarchy, l.currency_code\r\norder by ounder.hierarchy, l.currency_code','Individual Client \n\nReport',1,1),(15,'Portfolio at Risk','Table',NULL,'Loan','select x.Currency, x.`Principal Outstanding`, x.`Principal Overdue`, x.`Interest Outstanding`, x.`Interest Overdue`, \r\nx.`Fees Outstanding`, x.`Fees Overdue`, x.`Penalties Outstanding`, x.`Penalties Overdue`,\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.`Principal Overdue` * 100) / x.`Principal Outstanding`, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding`), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding`), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue` + x.`Penalties Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding` + x.`Penalties Overdue`), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from \r\n(select  ifnull(cur.display_symbol, l.currency_code) as Currency,  \r\nsum(l.principal_outstanding_derived) as \"Principal Outstanding\",\r\nsum(laa.principal_overdue_derived) as \"Principal Overdue\",\r\n\r\nsum(l.interest_outstanding_derived) as \"Interest Outstanding\",\r\nsum(laa.interest_overdue_derived) as \"Interest Overdue\",\r\n\r\nsum(l.fee_charges_outstanding_derived)  as \"Fees Outstanding\",\r\nsum(laa.fee_charges_overdue_derived) as \"Fees Overdue\",\r\n\r\nsum(penalty_charges_outstanding_derived) as \"Penalties Outstanding\",\r\nsum(laa.penalty_charges_overdue_derived) as \"Penalties Overdue\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin  m_loan l on l.client_id = c.id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nleft join m_product_loan p on p.id = l.product_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by l.currency_code\r\norder by l.currency_code) x','Covers all loans.\r\n\r\nFor larger MFIs … we should add some derived fields on loan (or a 1:1 loan related table like mifos 2.x does)\r\nPrinciple, Interest, Fees, Penalties Outstanding and Overdue (possibly waived and written off too)',1,1),(16,'Portfolio at Risk by Branch','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(mo.`hierarchy`) - LENGTH(REPLACE(mo.`hierarchy`, \'.\', \'\')) - 1))), mo.`name`) as \"Office/Branch\",\r\nx.Currency, x.`Principal Outstanding`, x.`Principal Overdue`, x.`Interest Outstanding`, x.`Interest Overdue`, \r\nx.`Fees Outstanding`, x.`Fees Overdue`, x.`Penalties Outstanding`, x.`Penalties Overdue`,\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.`Principal Overdue` * 100) / x.`Principal Outstanding`, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding`), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding`), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue` + x.`Penalties Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding` + x.`Penalties Overdue`), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from m_office mo\r\njoin \r\n(select  ounder.id as \"branch\", ifnull(cur.display_symbol, l.currency_code) as Currency,  \r\n\r\nsum(l.principal_outstanding_derived) as \"Principal Outstanding\",\r\nsum(laa.principal_overdue_derived) as \"Principal Overdue\",\r\n\r\nsum(l.interest_outstanding_derived) as \"Interest Outstanding\",\r\nsum(laa.interest_overdue_derived) as \"Interest Overdue\",\r\n\r\nsum(l.fee_charges_outstanding_derived)  as \"Fees Outstanding\",\r\nsum(laa.fee_charges_overdue_derived) as \"Fees Overdue\",\r\n\r\nsum(penalty_charges_outstanding_derived) as \"Penalties Outstanding\",\r\nsum(laa.penalty_charges_overdue_derived) as \"Penalties Overdue\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin  m_loan l on l.client_id = c.id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nleft join m_product_loan p on p.id = l.product_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by ounder.id, l.currency_code) x on x.branch = mo.id\r\norder by mo.hierarchy, x.Currency','Covers all loans.\r\n\r\nFor larger MFIs … we should add some derived fields on loan (or a 1:1 loan related table like mifos 2.x does)\r\nPrinciple, Interest, Fees, Penalties Outstanding and Overdue (possibly waived and written off too)',1,1),(20,'Funds Disbursed Between Dates Summary','Table',NULL,'Fund','select ifnull(f.`name`, \'-\') as Fund,  ifnull(cur.display_symbol, l.currency_code) as Currency, \r\nround(sum(l.principal_amount), 4) as disbursed_amount\r\nfrom m_office ounder \r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_currency cur on cur.`code` = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nwhere disbursedon_date between \'${startDate}\' and \'${endDate}\'\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (l.currency_code = \'${currencyId}\' or \'-1\' = \'${currencyId}\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\ngroup by ifnull(f.`name`, \'-\') , ifnull(cur.display_symbol, l.currency_code)\r\norder by ifnull(f.`name`, \'-\') , ifnull(cur.display_symbol, l.currency_code)',NULL,1,1),(21,'Funds Disbursed Between Dates Summary by Office','Table',NULL,'Fund','select \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\n \n\nifnull(f.`name`, \'-\') as Fund,  ifnull(cur.display_symbol, l.currency_code) as Currency, round(sum(l.principal_amount), 4) as disbursed_amount\r\nfrom m_office o\r\n\n\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c \n\non c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_currency cur on cur.`code` = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\n\n\nwhere disbursedon_date between \'${startDate}\' and \'${endDate}\'\r\nand o.id = ${officeId}\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand \n\n(l.currency_code = \'${currencyId}\' or \'-1\' = \'${currencyId}\')\r\ngroup by ounder.`name`,  ifnull(f.`name`, \'-\') , ifnull(cur.display_symbol, \n\nl.currency_code)\r\norder by ounder.`name`,  ifnull(f.`name`, \'-\') , ifnull(cur.display_symbol, l.currency_code)',NULL,1,1),(48,'Balance Sheet','Pentaho',NULL,'Accounting',NULL,'Balance Sheet',1,1),(49,'Income Statement','Pentaho',NULL,'Accounting',NULL,'Profit and Loss Statement',1,1),(50,'Trial Balance','Pentaho',NULL,'Accounting',NULL,'Trial Balance Report',1,1),(51,'Written-Off Loans','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, ml.currency_code) as Currency,  \r\nc.account_no as \"Client Account No.\",\r\nc.display_name AS \'Client Name\',\r\nml.account_no AS \'Loan Account No.\',\r\nmpl.name AS \'Product Name\',\r\nml.disbursedon_date AS \'Disbursed Date\',\r\nlt.transaction_date AS \'Written Off date\',\r\nml.principal_amount as \"Loan Amount\",\r\nifnull(lt.principal_portion_derived, 0) AS \'Written-Off Principal\',\r\nifnull(lt.interest_portion_derived, 0) AS \'Written-Off Interest\',\r\nifnull(lt.fee_charges_portion_derived,0) AS \'Written-Off Fees\',\r\nifnull(lt.penalty_charges_portion_derived,0) AS \'Written-Off Penalties\',\r\nn.note AS \'Reason For Write-Off\',\r\nIFNULL(ms.display_name,\'-\') AS \'Loan Officer Name\'\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nAND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_loan ml ON ml.client_id = c.id\r\nJOIN m_product_loan mpl ON mpl.id=ml.product_id\r\nLEFT JOIN m_staff ms ON ms.id=ml.loan_officer_id\r\nJOIN m_loan_transaction lt ON lt.loan_id = ml.id\r\nLEFT JOIN m_note n ON n.loan_transaction_id = lt.id\r\nLEFT JOIN m_currency cur on cur.code = ml.currency_code\r\nWHERE lt.transaction_type_enum = 6 /*write-off */\r\nAND lt.is_reversed is false \r\nAND ml.loan_status_id=601\r\nAND o.id=${officeId}\r\nAND (mpl.id=${loanProductId} OR ${loanProductId}=-1)\r\nAND lt.transaction_date BETWEEN \'${startDate}\' AND \'${endDate}\'\r\nAND (ml.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\") \r\nORDER BY ounder.hierarchy, ifnull(cur.display_symbol, ml.currency_code), ml.account_no','Individual Lending Report. Written Off Loans',1,1),(52,'Aging Detail','Table',NULL,'Loan','\r\nSELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, ml.currency_code) as Currency,  \r\nmc.account_no as \"Client Account No.\",\r\n 	mc.display_name AS \"Client Name\",\r\n 	ml.account_no AS \"Account Number\",\r\n 	ml.principal_amount AS \"Loan Amount\",\r\n ml.principal_disbursed_derived AS \"Original Principal\",\r\n ml.interest_charged_derived AS \"Original Interest\",\r\n ml.principal_repaid_derived AS \"Principal Paid\",\r\n ml.interest_repaid_derived AS \"Interest Paid\",\r\n laa.principal_overdue_derived AS \"Principal Overdue\",\r\n laa.interest_overdue_derived AS \"Interest Overdue\",\r\nDATEDIFF(CURDATE(), laa.overdue_since_date_derived) as \"Days in Arrears\",\r\n\r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<7, \'<1\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<8, \' 1\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<15,  \'2\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<22, \' 3\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<29, \' 4\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<36, \' 5\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<43, \' 6\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<50, \' 7\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<57, \' 8\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<64, \' 9\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<71, \'10\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<78, \'11\', \r\n 	IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived)<85, \'12\', \'12+\')))))))))))) )AS \"Weeks In Arrears Band\",\r\n\r\n		IF(DATEDIFF(CURDATE(),  laa.overdue_since_date_derived)<31, \'0 - 30\', \r\n		IF(DATEDIFF(CURDATE(),  laa.overdue_since_date_derived)<61, \'30 - 60\', \r\n		IF(DATEDIFF(CURDATE(),  laa.overdue_since_date_derived)<91, \'60 - 90\', \r\n		IF(DATEDIFF(CURDATE(),  laa.overdue_since_date_derived)<181, \'90 - 180\', \r\n		IF(DATEDIFF(CURDATE(),  laa.overdue_since_date_derived)<361, \'180 - 360\', \r\n				 \'> 360\'))))) AS \"Days in Arrears Band\"\r\n\r\n	FROM m_office mo \r\n    JOIN m_office ounder ON ounder.hierarchy like concat(mo.hierarchy, \'%\')\r\n	        AND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n    INNER JOIN m_client mc ON mc.office_id=ounder.id\r\n	    INNER JOIN m_loan ml ON ml.client_id = mc.id\r\n	    INNER JOIN r_enum_value rev ON rev.enum_id=ml.loan_status_id AND rev.enum_name = \'loan_status_id\'\r\n    INNER JOIN m_loan_arrears_aging laa ON laa.loan_id=ml.id\r\n    left join m_currency cur on cur.code = ml.currency_code\r\n	WHERE ml.loan_status_id=300\r\n    AND mo.id=${officeId}\r\nORDER BY ounder.hierarchy, ifnull(cur.display_symbol, ml.currency_code), ml.account_no\r\n','Loan arrears aging (Weeks)',1,1),(53,'Aging Summary (Arrears in Weeks)','Table',NULL,'Loan','SELECT \r\n  IFNULL(periods.currencyName, periods.currency) as currency, \r\n  periods.period_no \'Weeks In Arrears (Up To)\', \r\n  IFNULL(ars.loanId, 0) \'No Of Loans\', \r\n  IFNULL(ars.principal,0.0) \'Original Principal\', \r\n  IFNULL(ars.interest,0.0) \'Original Interest\', \r\n  IFNULL(ars.prinPaid,0.0) \'Principal Paid\', \r\n  IFNULL(ars.intPaid,0.0) \'Interest Paid\', \r\n  IFNULL(ars.prinOverdue,0.0) \'Principal Overdue\', \r\n  IFNULL(ars.intOverdue,0.0)\'Interest Overdue\'\r\nFROM \r\n	/* full table of aging periods/currencies used combo to ensure each line represented */\r\n  (SELECT curs.code as currency, curs.name as currencyName, pers.* from\r\n	(SELECT \'On Schedule\' period_no,1 pid UNION\r\n		SELECT \'1\',2 UNION\r\n		SELECT \'2\',3 UNION\r\n		SELECT \'3\',4 UNION\r\n		SELECT \'4\',5 UNION\r\n		SELECT \'5\',6 UNION\r\n		SELECT \'6\',7 UNION\r\n		SELECT \'7\',8 UNION\r\n		SELECT \'8\',9 UNION\r\n		SELECT \'9\',10 UNION\r\n		SELECT \'10\',11 UNION\r\n		SELECT \'11\',12 UNION\r\n		SELECT \'12\',13 UNION\r\n		SELECT \'12+\',14) pers,\r\n	(SELECT distinctrow moc.code, moc.name\r\n  	FROM m_office mo2\r\n   	INNER JOIN m_office ounder2 ON ounder2.hierarchy \r\n				LIKE CONCAT(mo2.hierarchy, \'%\')\r\nAND ounder2.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n   	INNER JOIN m_client mc2 ON mc2.office_id=ounder2.id\r\n   	INNER JOIN m_loan ml2 ON ml2.client_id = mc2.id\r\n	INNER JOIN m_organisation_currency moc ON moc.code = ml2.currency_code\r\n	WHERE ml2.loan_status_id=300 /* active */\r\n	AND mo2.id=${officeId}\r\nAND (ml2.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")) curs) periods\r\n\r\n\r\nLEFT JOIN /* table of aging periods per currency with gaps if no applicable loans */\r\n(SELECT \r\n  	z.currency, z.arrPeriod, \r\n	COUNT(z.loanId) as loanId, SUM(z.principal) as principal, SUM(z.interest) as interest, \r\n	SUM(z.prinPaid) as prinPaid, SUM(z.intPaid) as intPaid, \r\n	SUM(z.prinOverdue) as prinOverdue, SUM(z.intOverdue) as intOverdue\r\nFROM\r\n	/*derived table just used to get arrPeriod value (was much slower to\r\n	duplicate calc of minOverdueDate in inner query)\r\nmight not be now with derived fields but didn’t check */\r\n	(SELECT x.loanId, x.currency, x.principal, x.interest, x.prinPaid, x.intPaid, x.prinOverdue, x.intOverdue,\r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<1, \'On Schedule\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<8, \'1\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<15, \'2\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<22, \'3\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<29, \'4\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<36, \'5\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<43, \'6\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<50, \'7\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<57, \'8\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<64, \'9\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<71, \'10\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<78, \'11\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<85, \'12\',\r\n				 \'12+\'))))))))))))) AS arrPeriod\r\n\r\n	FROM /* get the individual loan details */\r\n		(SELECT ml.id AS loanId, ml.currency_code as currency,\r\n   			ml.principal_disbursed_derived as principal, \r\n			   ml.interest_charged_derived as interest, \r\n   			ml.principal_repaid_derived as prinPaid, \r\n			   ml.interest_repaid_derived intPaid,\r\n\r\n			   laa.principal_overdue_derived as prinOverdue,\r\n			   laa.interest_overdue_derived as intOverdue,\r\n\r\n			   IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate\r\n			  \r\n  		FROM m_office mo\r\n   		INNER JOIN m_office ounder ON ounder.hierarchy \r\n				LIKE CONCAT(mo.hierarchy, \'%\')\r\nAND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n   		INNER JOIN m_client mc ON mc.office_id=ounder.id\r\n   		INNER JOIN m_loan ml ON ml.client_id = mc.id\r\n		   LEFT JOIN m_loan_arrears_aging laa on laa.loan_id = ml.id\r\n		WHERE ml.loan_status_id=300 /* active */\r\n     		AND mo.id=${officeId}\r\n     AND (ml.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\n  		GROUP BY ml.id) x\r\n	) z \r\nGROUP BY z.currency, z.arrPeriod ) ars ON ars.arrPeriod=periods.period_no and ars.currency = periods.currency\r\nORDER BY periods.currency, periods.pid','Loan amount in arrears by branch',1,1),(54,'Rescheduled Loans','Table',NULL,'Loan','SELECT \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, ml.currency_code) as Currency,  \r\nc.account_no as \"Client Account No.\",\r\nc.display_name AS \'Client Name\',\r\nml.account_no AS \'Loan Account No.\',\r\nmpl.name AS \'Product Name\',\r\nml.disbursedon_date AS \'Disbursed Date\',\r\nlt.transaction_date AS \'Written Off date\',\r\nml.principal_amount as \"Loan Amount\",\r\nifnull(lt.principal_portion_derived, 0) AS \'Rescheduled Principal\',\r\nifnull(lt.interest_portion_derived, 0) AS \'Rescheduled Interest\',\r\nifnull(lt.fee_charges_portion_derived,0) AS \'Rescheduled Fees\',\r\nifnull(lt.penalty_charges_portion_derived,0) AS \'Rescheduled Penalties\',\r\nn.note AS \'Reason For Rescheduling\',\r\nIFNULL(ms.display_name,\'-\') AS \'Loan Officer Name\'\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nAND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_loan ml ON ml.client_id = c.id\r\nJOIN m_product_loan mpl ON mpl.id=ml.product_id\r\nLEFT JOIN m_staff ms ON ms.id=ml.loan_officer_id\r\nJOIN m_loan_transaction lt ON lt.loan_id = ml.id\r\nLEFT JOIN m_note n ON n.loan_transaction_id = lt.id\r\nLEFT JOIN m_currency cur on cur.code = ml.currency_code\r\nWHERE lt.transaction_type_enum = 7 /*marked for rescheduling */\r\nAND lt.is_reversed is false \r\nAND ml.loan_status_id=602\r\nAND o.id=${officeId}\r\nAND (mpl.id=${loanProductId} OR ${loanProductId}=-1)\r\nAND lt.transaction_date BETWEEN \'${startDate}\' AND \'${endDate}\'\r\nAND (ml.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nORDER BY ounder.hierarchy, ifnull(cur.display_symbol, ml.currency_code), ml.account_no','Individual Lending Report. Rescheduled Loans.  The ability to reschedule (or mark that you have rescheduled the loan elsewhere) is a legacy of the older Mifos product.  Needed for migration.',1,1),(55,'Active Loans Passed Final Maturity','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nlo.display_name as \"Loan Officer\", \r\nc.display_name as \"Client\", l.account_no as \"Loan Account No.\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  \r\nl.principal_amount as \"Loan Amount\", \r\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed Date\", \r\ndate(l.expected_maturedon_date) as \"Expected Matured On\",\r\n\r\nl.principal_repaid_derived as \"Principal Repaid\",\r\nl.principal_outstanding_derived as \"Principal Outstanding\",\r\nlaa.principal_overdue_derived as \"Principal Overdue\",\r\n\r\nl.interest_repaid_derived as \"Interest Repaid\",\r\nl.interest_outstanding_derived as \"Interest Outstanding\",\r\nlaa.interest_overdue_derived as \"Interest Overdue\",\r\n\r\nl.fee_charges_repaid_derived as \"Fees Repaid\",\r\nl.fee_charges_outstanding_derived  as \"Fees Outstanding\",\r\nlaa.fee_charges_overdue_derived as \"Fees Overdue\",\r\n\r\nl.penalty_charges_repaid_derived as \"Penalties Repaid\",\r\nl.penalty_charges_outstanding_derived as \"Penalties Outstanding\",\r\nlaa.penalty_charges_overdue_derived as \"Penalties Overdue\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\nand l.expected_maturedon_date < curdate()\r\ngroup by l.id\r\norder by ounder.hierarchy, l.currency_code, c.account_no, l.account_no','Individual Client \n\nReport',1,1),(56,'Active Loans Passed Final Maturity Summary','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(mo.`hierarchy`) - LENGTH(REPLACE(mo.`hierarchy`, \'.\', \'\')) - 1))), mo.`name`) as \"Office/Branch\", x.currency as Currency,\r\n x.client_count as \"No. of Clients\", x.active_loan_count as \"No. Active Loans\", x. arrears_loan_count as \"No. of Loans in Arrears\",\r\nx.principal as \"Total Loans Disbursed\", x.principal_repaid as \"Principal Repaid\", x.principal_outstanding as \"Principal Outstanding\", x.principal_overdue as \"Principal Overdue\",\r\nx.interest as \"Total Interest\", x.interest_repaid as \"Interest Repaid\", x.interest_outstanding as \"Interest Outstanding\", x.interest_overdue as \"Interest Overdue\",\r\nx.fees as \"Total Fees\", x.fees_repaid as \"Fees Repaid\", x.fees_outstanding as \"Fees Outstanding\", x.fees_overdue as \"Fees Overdue\",\r\nx.penalties as \"Total Penalties\", x.penalties_repaid as \"Penalties Repaid\", x.penalties_outstanding as \"Penalties Outstanding\", x.penalties_overdue as \"Penalties Overdue\",\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.principal_overdue * 100) / x.principal_outstanding, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue + x.penalties_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding + x.penalties_overdue), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from m_office mo\r\njoin \r\n(select ounder.id as branch,\r\nifnull(cur.display_symbol, l.currency_code) as currency,\r\ncount(distinct(c.id)) as client_count, \r\ncount(distinct(l.id)) as  active_loan_count,\r\ncount(distinct(laa.loan_id)  ) as arrears_loan_count,\r\n\r\nsum(l.principal_disbursed_derived) as principal,\r\nsum(l.principal_repaid_derived) as principal_repaid,\r\nsum(l.principal_outstanding_derived) as principal_outstanding,\r\nsum(ifnull(laa.principal_overdue_derived,0)) as principal_overdue,\r\n\r\nsum(l.interest_charged_derived) as interest,\r\nsum(l.interest_repaid_derived) as interest_repaid,\r\nsum(l.interest_outstanding_derived) as interest_outstanding,\r\nsum(ifnull(laa.interest_overdue_derived,0)) as interest_overdue,\r\n\r\nsum(l.fee_charges_charged_derived) as fees,\r\nsum(l.fee_charges_repaid_derived) as fees_repaid,\r\nsum(l.fee_charges_outstanding_derived)  as fees_outstanding,\r\nsum(ifnull(laa.fee_charges_overdue_derived,0)) as fees_overdue,\r\n\r\nsum(l.penalty_charges_charged_derived) as penalties,\r\nsum(l.penalty_charges_repaid_derived) as penalties_repaid,\r\nsum(l.penalty_charges_outstanding_derived) as penalties_outstanding,\r\nsum(ifnull(laa.penalty_charges_overdue_derived,0)) as penalties_overdue\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\n\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\nand l.expected_maturedon_date < curdate()\r\ngroup by ounder.id, l.currency_code) x on x.branch = mo.id\r\norder by mo.hierarchy, x.Currency',NULL,1,1),(57,'Active Loans in last installment','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(lastInstallment.`hierarchy`) - LENGTH(REPLACE(lastInstallment.`hierarchy`, \'.\', \'\')) - 1))), lastInstallment.branch) as \"Office/Branch\",\r\nlastInstallment.Currency,\r\nlastInstallment.`Loan Officer`, \r\nlastInstallment.`Client Account No`, lastInstallment.`Client`, \r\nlastInstallment.`Loan Account No`, lastInstallment.`Product`, \r\nlastInstallment.`Fund`,  lastInstallment.`Loan Amount`, \r\nlastInstallment.`Annual Nominal Interest Rate`, \r\nlastInstallment.`Disbursed`, lastInstallment.`Expected Matured On` ,\r\n\r\nl.principal_repaid_derived as \"Principal Repaid\",\r\nl.principal_outstanding_derived as \"Principal Outstanding\",\r\nlaa.principal_overdue_derived as \"Principal Overdue\",\r\n\r\nl.interest_repaid_derived as \"Interest Repaid\",\r\nl.interest_outstanding_derived as \"Interest Outstanding\",\r\nlaa.interest_overdue_derived as \"Interest Overdue\",\r\n\r\nl.fee_charges_repaid_derived as \"Fees Repaid\",\r\nl.fee_charges_outstanding_derived  as \"Fees Outstanding\",\r\nlaa.fee_charges_overdue_derived as \"Fees Overdue\",\r\n\r\nl.penalty_charges_repaid_derived as \"Penalties Repaid\",\r\nl.penalty_charges_outstanding_derived as \"Penalties Outstanding\",\r\nlaa.penalty_charges_overdue_derived as \"Penalties Overdue\"\r\n\r\nfrom \r\n(select l.id as loanId, l.number_of_repayments, min(r.installment), \r\nounder.id, ounder.hierarchy, ounder.`name` as branch, \r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nlo.display_name as \"Loan Officer\", c.account_no as \"Client Account No\",\r\nc.display_name as \"Client\", l.account_no as \"Loan Account No\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  l.principal_amount as \"Loan Amount\", \r\nl.annual_nominal_interest_rate as \"Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed\", date(l.expected_maturedon_date) as \"Expected Matured On\"\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_repayment_schedule r on r.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\nand r.completed_derived is false\r\nand r.duedate >= curdate()\r\ngroup by l.id\r\nhaving l.number_of_repayments = min(r.installment)) lastInstallment\r\njoin m_loan l on l.id = lastInstallment.loanId\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\norder by lastInstallment.hierarchy, lastInstallment.Currency, lastInstallment.`Client Account No`, lastInstallment.`Loan Account No`','Individual Client \n\nReport',1,1),(58,'Active Loans in last installment Summary','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(mo.`hierarchy`) - LENGTH(REPLACE(mo.`hierarchy`, \'.\', \'\')) - 1))), mo.`name`) as \"Office/Branch\", x.currency as Currency,\r\n x.client_count as \"No. of Clients\", x.active_loan_count as \"No. Active Loans\", x. arrears_loan_count as \"No. of Loans in Arrears\",\r\nx.principal as \"Total Loans Disbursed\", x.principal_repaid as \"Principal Repaid\", x.principal_outstanding as \"Principal Outstanding\", x.principal_overdue as \"Principal Overdue\",\r\nx.interest as \"Total Interest\", x.interest_repaid as \"Interest Repaid\", x.interest_outstanding as \"Interest Outstanding\", x.interest_overdue as \"Interest Overdue\",\r\nx.fees as \"Total Fees\", x.fees_repaid as \"Fees Repaid\", x.fees_outstanding as \"Fees Outstanding\", x.fees_overdue as \"Fees Overdue\",\r\nx.penalties as \"Total Penalties\", x.penalties_repaid as \"Penalties Repaid\", x.penalties_outstanding as \"Penalties Outstanding\", x.penalties_overdue as \"Penalties Overdue\",\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.principal_overdue * 100) / x.principal_outstanding, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.principal_overdue + x.interest_overdue + x.fees_overdue + x.penalties_overdue) * 100) / (x.principal_outstanding + x.interest_outstanding + x.fees_outstanding + x.penalties_overdue), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from m_office mo\r\njoin \r\n(select lastInstallment.branchId as branchId,\r\nlastInstallment.Currency,\r\ncount(distinct(lastInstallment.clientId)) as client_count, \r\ncount(distinct(lastInstallment.loanId)) as  active_loan_count,\r\ncount(distinct(laa.loan_id)  ) as arrears_loan_count,\r\n\r\nsum(l.principal_disbursed_derived) as principal,\r\nsum(l.principal_repaid_derived) as principal_repaid,\r\nsum(l.principal_outstanding_derived) as principal_outstanding,\r\nsum(ifnull(laa.principal_overdue_derived,0)) as principal_overdue,\r\n\r\nsum(l.interest_charged_derived) as interest,\r\nsum(l.interest_repaid_derived) as interest_repaid,\r\nsum(l.interest_outstanding_derived) as interest_outstanding,\r\nsum(ifnull(laa.interest_overdue_derived,0)) as interest_overdue,\r\n\r\nsum(l.fee_charges_charged_derived) as fees,\r\nsum(l.fee_charges_repaid_derived) as fees_repaid,\r\nsum(l.fee_charges_outstanding_derived)  as fees_outstanding,\r\nsum(ifnull(laa.fee_charges_overdue_derived,0)) as fees_overdue,\r\n\r\nsum(l.penalty_charges_charged_derived) as penalties,\r\nsum(l.penalty_charges_repaid_derived) as penalties_repaid,\r\nsum(l.penalty_charges_outstanding_derived) as penalties_outstanding,\r\nsum(ifnull(laa.penalty_charges_overdue_derived,0)) as penalties_overdue\r\n\r\nfrom \r\n(select l.id as loanId, l.number_of_repayments, min(r.installment), \r\nounder.id as branchId, ounder.hierarchy, ounder.`name` as branch, \r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nlo.display_name as \"Loan Officer\", c.id as clientId, c.account_no as \"Client Account No\",\r\nc.display_name as \"Client\", l.account_no as \"Loan Account No\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  l.principal_amount as \"Loan Amount\", \r\nl.annual_nominal_interest_rate as \"Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed\", date(l.expected_maturedon_date) as \"Expected Matured On\"\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_repayment_schedule r on r.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\nand r.completed_derived is false\r\nand r.duedate >= curdate()\r\ngroup by l.id\r\nhaving l.number_of_repayments = min(r.installment)) lastInstallment\r\njoin m_loan l on l.id = lastInstallment.loanId\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\ngroup by lastInstallment.branchId) x on x.branchId = mo.id\r\norder by mo.hierarchy, x.Currency','Individual Client \n\nReport',1,1),(59,'Active Loans by Disbursal Period','Table',NULL,'Loan','select concat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nc.account_no as \"Client Account No\", c.display_name as \"Client\", l.account_no as \"Loan Account No\", pl.`name` as \"Product\", \r\nf.`name` as Fund,  \r\nl.principal_amount as \"Loan Principal Amount\", \r\nl.annual_nominal_interest_rate as \" Annual Nominal Interest Rate\", \r\ndate(l.disbursedon_date) as \"Disbursed Date\", \r\n\r\nl.total_expected_repayment_derived as \"Total Loan (P+I+F+Pen)\",\r\nl.total_repayment_derived as \"Total Repaid (P+I+F+Pen)\",\r\nlo.display_name as \"Loan Officer\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.disbursedon_date between \'${startDate}\' and \'${endDate}\'\r\nand l.loan_status_id = 300\r\ngroup by l.id\r\norder by ounder.hierarchy, l.currency_code, c.account_no, l.account_no','Individual Client \n\nReport',1,1),(61,'Aging Summary (Arrears in Months)','Table',NULL,'Loan','SELECT \r\n  IFNULL(periods.currencyName, periods.currency) as currency, \r\n  periods.period_no \'Days In Arrears\', \r\n  IFNULL(ars.loanId, 0) \'No Of Loans\', \r\n  IFNULL(ars.principal,0.0) \'Original Principal\', \r\n  IFNULL(ars.interest,0.0) \'Original Interest\', \r\n  IFNULL(ars.prinPaid,0.0) \'Principal Paid\', \r\n  IFNULL(ars.intPaid,0.0) \'Interest Paid\', \r\n  IFNULL(ars.prinOverdue,0.0) \'Principal Overdue\', \r\n  IFNULL(ars.intOverdue,0.0)\'Interest Overdue\'\r\nFROM \r\n	/* full table of aging periods/currencies used combo to ensure each line represented */\r\n  (SELECT curs.code as currency, curs.name as currencyName, pers.* from\r\n	(SELECT \'On Schedule\' period_no,1 pid UNION\r\n		SELECT \'0 - 30\',2 UNION\r\n		SELECT \'30 - 60\',3 UNION\r\n		SELECT \'60 - 90\',4 UNION\r\n		SELECT \'90 - 180\',5 UNION\r\n		SELECT \'180 - 360\',6 UNION\r\n		SELECT \'> 360\',7 ) pers,\r\n	(SELECT distinctrow moc.code, moc.name\r\n  	FROM m_office mo2\r\n   	INNER JOIN m_office ounder2 ON ounder2.hierarchy \r\n				LIKE CONCAT(mo2.hierarchy, \'%\')\r\nAND ounder2.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n   	INNER JOIN m_client mc2 ON mc2.office_id=ounder2.id\r\n   	INNER JOIN m_loan ml2 ON ml2.client_id = mc2.id\r\n	INNER JOIN m_organisation_currency moc ON moc.code = ml2.currency_code\r\n	WHERE ml2.loan_status_id=300 /* active */\r\n	AND mo2.id=${officeId}\r\nAND (ml2.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")) curs) periods\r\n\r\n\r\nLEFT JOIN /* table of aging periods per currency with gaps if no applicable loans */\r\n(SELECT \r\n  	z.currency, z.arrPeriod, \r\n	COUNT(z.loanId) as loanId, SUM(z.principal) as principal, SUM(z.interest) as interest, \r\n	SUM(z.prinPaid) as prinPaid, SUM(z.intPaid) as intPaid, \r\n	SUM(z.prinOverdue) as prinOverdue, SUM(z.intOverdue) as intOverdue\r\nFROM\r\n	/*derived table just used to get arrPeriod value (was much slower to\r\n	duplicate calc of minOverdueDate in inner query)\r\nmight not be now with derived fields but didn’t check */\r\n	(SELECT x.loanId, x.currency, x.principal, x.interest, x.prinPaid, x.intPaid, x.prinOverdue, x.intOverdue,\r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<1, \'On Schedule\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<31, \'0 - 30\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<61, \'30 - 60\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<91, \'60 - 90\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<181, \'90 - 180\', \r\n		IF(DATEDIFF(CURDATE(), minOverdueDate)<361, \'180 - 360\', \r\n				 \'> 360\')))))) AS arrPeriod\r\n\r\n	FROM /* get the individual loan details */\r\n		(SELECT ml.id AS loanId, ml.currency_code as currency,\r\n   			ml.principal_disbursed_derived as principal, \r\n			   ml.interest_charged_derived as interest, \r\n   			ml.principal_repaid_derived as prinPaid, \r\n			   ml.interest_repaid_derived intPaid,\r\n\r\n			   laa.principal_overdue_derived as prinOverdue,\r\n			   laa.interest_overdue_derived as intOverdue,\r\n\r\n			   IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate\r\n			  \r\n  		FROM m_office mo\r\n   		INNER JOIN m_office ounder ON ounder.hierarchy \r\n				LIKE CONCAT(mo.hierarchy, \'%\')\r\nAND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n   		INNER JOIN m_client mc ON mc.office_id=ounder.id\r\n   		INNER JOIN m_loan ml ON ml.client_id = mc.id\r\n		   LEFT JOIN m_loan_arrears_aging laa on laa.loan_id = ml.id\r\n		WHERE ml.loan_status_id=300 /* active */\r\n     		AND mo.id=${officeId}\r\n     AND (ml.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\n  		GROUP BY ml.id) x\r\n	) z \r\nGROUP BY z.currency, z.arrPeriod ) ars ON ars.arrPeriod=periods.period_no and ars.currency = periods.currency\r\nORDER BY periods.currency, periods.pid','Loan amount in arrears by branch',1,1),(91,'Loan Account Schedule','Pentaho',NULL,'Loan',NULL,NULL,1,0),(92,'Branch Expected Cash Flow','Pentaho',NULL,'Loan',NULL,NULL,1,1),(93,'Expected Payments By Date - Basic','Table',NULL,'Loan','SELECT \r\n      ounder.name \'Office\', \r\n      IFNULL(ms.display_name,\'-\') \'Loan Officer\',\r\n	  mc.account_no \'Client Account Number\',\r\n	  mc.display_name \'Name\',\r\n	  mp.name \'Product\',\r\n	  ml.account_no \'Loan Account Number\',\r\n	  mr.duedate \'Due Date\',\r\n	  mr.installment \'Installment\',\r\n	  cu.display_symbol \'Currency\',\r\n	  mr.principal_amount- IFNULL(mr.principal_completed_derived,0) \'Principal Due\',\r\n	  mr.interest_amount- IFNULL(IFNULL(mr.interest_completed_derived,mr.interest_waived_derived),0) \'Interest Due\', \r\n	  IFNULL(mr.fee_charges_amount,0)- IFNULL(IFNULL(mr.fee_charges_completed_derived,mr.fee_charges_waived_derived),0) \'Fees Due\', \r\n	  IFNULL(mr.penalty_charges_amount,0)- IFNULL(IFNULL(mr.penalty_charges_completed_derived,mr.penalty_charges_waived_derived),0) \'Penalty Due\',\r\n      (mr.principal_amount- IFNULL(mr.principal_completed_derived,0)) +\r\n       (mr.interest_amount- IFNULL(IFNULL(mr.interest_completed_derived,mr.interest_waived_derived),0)) + \r\n       (IFNULL(mr.fee_charges_amount,0)- IFNULL(IFNULL(mr.fee_charges_completed_derived,mr.fee_charges_waived_derived),0)) + \r\n       (IFNULL(mr.penalty_charges_amount,0)- IFNULL(IFNULL(mr.penalty_charges_completed_derived,mr.penalty_charges_waived_derived),0)) \'Total Due\', \r\n     mlaa.total_overdue_derived \'Total Overdue\'\r\n										 \r\n FROM m_office mo\r\n  JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\r\n  \r\n  AND ounder.hierarchy like CONCAT(\'${currentUserHierarchy}\', \'%\')\r\n	\r\n  LEFT JOIN m_client mc ON mc.office_id=ounder.id\r\n  LEFT JOIN m_loan ml ON ml.client_id=mc.id AND ml.loan_status_id=300\r\n  LEFT JOIN m_loan_arrears_aging mlaa ON mlaa.loan_id=ml.id\r\n  LEFT JOIN m_loan_repayment_schedule mr ON mr.loan_id=ml.id AND mr.completed_derived=0\r\n  LEFT JOIN m_product_loan mp ON mp.id=ml.product_id\r\n  LEFT JOIN m_staff ms ON ms.id=ml.loan_officer_id\r\n  LEFT JOIN m_currency cu ON cu.code=ml.currency_code\r\n WHERE mo.id=${officeId}\r\n AND (IFNULL(ml.loan_officer_id, -10) = \"${loanOfficerId}\" OR \"-1\" = \"${loanOfficerId}\")\r\n AND mr.duedate BETWEEN \'${startDate}\' AND \'${endDate}\'\r\n ORDER BY ounder.id,mr.duedate,ml.account_no','Test',1,1),(94,'Expected Payments By Date - Formatted','Pentaho',NULL,'Loan',NULL,NULL,1,1),(96,'GroupSummaryCounts','Table',NULL,NULL,'\n/*\nActive Client is a client linked to the \'group\' via m_group_client\nand with an active \'status_enum\'.)\nActive Borrowers - Borrower may be a client or a \'group\'\n*/\nselect x.*\nfrom m_office o,\nm_group g,\n\n(select a.activeClients,\n(b.activeClientLoans + c.activeGroupLoans) as activeLoans,\nb.activeClientLoans, c.activeGroupLoans,\n(b.activeClientBorrowers + c.activeGroupBorrowers) as activeBorrowers,\nb.activeClientBorrowers, c.activeGroupBorrowers,\n(b.overdueClientLoans +  c.overdueGroupLoans) as overdueLoans,\nb.overdueClientLoans, c.overdueGroupLoans\nfrom\n(select count(*) as activeClients\nfrom m_group topgroup\njoin m_group g on g.hierarchy like concat(topgroup.hierarchy, \'%\')\njoin m_group_client gc on gc.group_id = g.id\njoin m_client c on c.id = gc.client_id\nwhere topgroup.id = ${groupId}\nand c.status_enum = 300) a,\n\n(select count(*) as activeClientLoans,\ncount(distinct(l.client_id)) as activeClientBorrowers,\nifnull(sum(if(laa.loan_id is not null, 1, 0)), 0) as overdueClientLoans\nfrom m_group topgroup\njoin m_group g on g.hierarchy like concat(topgroup.hierarchy, \'%\')\njoin m_loan l on l.group_id = g.id and l.client_id is not null\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nwhere topgroup.id = ${groupId}\nand l.loan_status_id = 300) b,\n\n(select count(*) as activeGroupLoans,\ncount(distinct(l.group_id)) as activeGroupBorrowers,\nifnull(sum(if(laa.loan_id is not null, 1, 0)), 0) as overdueGroupLoans\nfrom m_group topgroup\njoin m_group g on g.hierarchy like concat(topgroup.hierarchy, \'%\')\njoin m_loan l on l.group_id = g.id and l.client_id is null\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nwhere topgroup.id = ${groupId}\nand l.loan_status_id = 300) c\n) x\n\nwhere g.id = ${groupId}\nand o.id = g.office_id\nand o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\n','Utility query for getting group summary count details for a group_id',1,0),(97,'GroupSummaryAmounts','Table',NULL,NULL,'\nselect ifnull(cur.display_symbol, l.currency_code) as currency,\nifnull(sum(l.principal_disbursed_derived),0) as totalDisbursedAmount,\nifnull(sum(l.principal_outstanding_derived),0) as totalLoanOutstandingAmount,\ncount(laa.loan_id) as overdueLoans, ifnull(sum(laa.total_overdue_derived), 0) as totalLoanOverdueAmount\nfrom m_group topgroup\njoin m_office o on o.id = topgroup.office_id and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group g on g.hierarchy like concat(topgroup.hierarchy, \'%\')\njoin m_loan l on l.group_id = g.id\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_currency cur on cur.code = l.currency_code\nwhere topgroup.id = ${groupId}\nand l.disbursedon_date is not null\ngroup by l.currency_code\n','Utility query for getting group summary currency amount details for a group_id',1,0),(106,'TxnRunningBalances','Table',NULL,'Transaction','\nselect date(\'${startDate}\') as \'Transaction Date\', \'Opening Balance\' as `Transaction Type`, null as Office,\n	null as \'Loan Officer\', null as `Loan Account No`, null as `Loan Product`, null as `Currency`,\n	null as `Client Account No`, null as Client,\n	null as Amount, null as Principal, null as Interest,\n@totalOutstandingPrincipal :=\nifnull(round(sum(\n	if (txn.transaction_type_enum = 1 /* disbursement */,\n		ifnull(txn.amount,0.00),\n		ifnull(txn.principal_portion_derived,0.00) * -1))\n			,2),0.00)  as \'Outstanding Principal\',\n\n@totalInterestIncome :=\nifnull(round(sum(\n	if (txn.transaction_type_enum in (2,5,8) /* repayment, repayment at disbursal, recovery repayment */,\n		ifnull(txn.interest_portion_derived,0.00),\n		0))\n			,2),0.00) as \'Interest Income\',\n\n@totalWriteOff :=\nifnull(round(sum(\n	if (txn.transaction_type_enum = 6 /* write-off */,\n		ifnull(txn.principal_portion_derived,0.00),\n		0))\n			,2),0.00) as \'Principal Write Off\'\nfrom m_office o\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\n                          and ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_client c on c.office_id = ounder.id\njoin m_loan l on l.client_id = c.id\njoin m_product_loan lp on lp.id = l.product_id\njoin m_loan_transaction txn on txn.loan_id = l.id\nleft join m_currency cur on cur.code = l.currency_code\nwhere txn.is_reversed = false\nand txn.transaction_type_enum not in (10,11)\nand o.id = ${officeId}\nand txn.transaction_date < date(\'${startDate}\')\n\nunion all\n\nselect x.`Transaction Date`, x.`Transaction Type`, x.Office, x.`Loan Officer`, x.`Loan Account No`, x.`Loan Product`, x.`Currency`,\n	x.`Client Account No`, x.Client, x.Amount, x.Principal, x.Interest,\ncast(round(\n	if (x.transaction_type_enum = 1 /* disbursement */,\n		@totalOutstandingPrincipal := @totalOutstandingPrincipal + x.`Amount`,\n		@totalOutstandingPrincipal := @totalOutstandingPrincipal - x.`Principal`)\n			,2) as decimal(19,2)) as \'Outstanding Principal\',\ncast(round(\n	if (x.transaction_type_enum in (2,5,8) /* repayment, repayment at disbursal, recovery repayment */,\n		@totalInterestIncome := @totalInterestIncome + x.`Interest`,\n		@totalInterestIncome)\n			,2) as decimal(19,2)) as \'Interest Income\',\ncast(round(\n	if (x.transaction_type_enum = 6 /* write-off */,\n		@totalWriteOff := @totalWriteOff + x.`Principal`,\n		@totalWriteOff)\n			,2) as decimal(19,2)) as \'Principal Write Off\'\nfrom\n(select txn.transaction_type_enum, txn.id as txn_id, txn.transaction_date as \'Transaction Date\',\ncast(\n	ifnull(re.enum_message_property, concat(\'Unknown Transaction Type Value: \' , txn.transaction_type_enum))\n	as char) as \'Transaction Type\',\nounder.`name` as Office, lo.display_name as \'Loan Officer\',\nl.account_no  as \'Loan Account No\', lp.`name` as \'Loan Product\',\nifnull(cur.display_symbol, l.currency_code) as Currency,\nc.account_no as \'Client Account No\', c.display_name as \'Client\',\nifnull(txn.amount,0.00) as Amount,\nifnull(txn.principal_portion_derived,0.00) as Principal,\nifnull(txn.interest_portion_derived,0.00) as Interest\nfrom m_office o\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\n                          and ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_client c on c.office_id = ounder.id\njoin m_loan l on l.client_id = c.id\nleft join m_staff lo on lo.id = l.loan_officer_id\njoin m_product_loan lp on lp.id = l.product_id\njoin m_loan_transaction txn on txn.loan_id = l.id\nleft join m_currency cur on cur.code = l.currency_code\nleft join r_enum_value re on re.enum_name = \'transaction_type_enum\'\n						and re.enum_id = txn.transaction_type_enum\nwhere txn.is_reversed = false\nand txn.transaction_type_enum not in (10,11)\nand (ifnull(l.loan_officer_id, -10) = \'${loanOfficerId}\' or \'-1\' = \'${loanOfficerId}\')\nand o.id = ${officeId}\nand txn.transaction_date >= date(\'${startDate}\')\nand txn.transaction_date <= date(\'${endDate}\')\norder by txn.transaction_date, txn.id) x\n','Running Balance Txn report for Individual Lending.\nSuitable for small MFI\'s.  Larger could use it using the branch or other parameters.\nBasically, suck it and see if its quick enough for you out-of-te box or whether it needs performance work in your situation.\n',0,0),(107,'FieldAgentStats','Table',NULL,'Quipo','\nselect ifnull(cur.display_symbol, l.currency_code) as Currency,\n/*This query will return more than one entry if more than one currency is used */\ncount(distinct(c.id)) as activeClients, count(*) as activeLoans,\nsum(l.principal_disbursed_derived) as disbursedAmount,\nsum(l.principal_outstanding_derived) as loanOutstandingAmount,\nround((sum(l.principal_outstanding_derived) * 100) /  sum(l.principal_disbursed_derived),2) as loanOutstandingPC,\nsum(ifnull(lpa.principal_in_advance_derived,0.0)) as LoanPaidInAdvance,\nsum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) as portfolioAtRisk,\n\nround((sum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) * 100) / sum(l.principal_outstanding_derived), 2) as portfolioAtRiskPC,\n\ncount(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) as clientsInDefault,\nround((count(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) * 100) / count(distinct(c.id)),2) as clientsInDefaultPC,\n(sum(l.principal_disbursed_derived) / count(*))  as averageLoanAmount\nfrom m_staff fa\njoin m_office o on o.id = fa.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group pgm on pgm.staff_id = fa.id\njoin m_loan l on l.group_id = pgm.id and l.client_id is not null\nleft join m_currency cur on cur.code = l.currency_code\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\njoin m_client c on c.id = l.client_id\nwhere fa.id = ${staffId}\nand l.loan_status_id = 300\ngroup  by l.currency_code\n','Field Agent Statistics',0,0),(108,'FieldAgentPrograms','Table',NULL,'Quipo','\nselect pgm.id, pgm.display_name as `name`, sts.enum_message_property as status\n from m_group pgm\n join m_office o on o.id = pgm.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\n left join r_enum_value sts on sts.enum_name = \'status_enum\' and sts.enum_id = pgm.status_enum\n where pgm.staff_id = ${staffId}\n','List of Field Agent Programs',0,0),(109,'ProgramDetails','Table',NULL,'Quipo','\n select l.id as loanId, l.account_no as loanAccountNo, c.id as clientId, c.account_no as clientAccountNo,\n pgm.display_name as programName,\n\n(select count(*)\nfrom m_loan cy\nwhere cy.group_id = pgm.id and cy.client_id =c.id\nand cy.disbursedon_date <= l.disbursedon_date) as loanCycleNo,\n\nc.display_name as clientDisplayName,\n ifnull(cur.display_symbol, l.currency_code) as Currency,\nifnull(l.principal_repaid_derived,0.0) as loanRepaidAmount,\nifnull(l.principal_outstanding_derived, 0.0) as loanOutstandingAmount,\nifnull(lpa.principal_in_advance_derived,0.0) as LoanPaidInAdvance,\n\nifnull(laa.principal_overdue_derived, 0.0) as loanInArrearsAmount,\nif(ifnull(laa.principal_overdue_derived, 0.00) > 0, \'Yes\', \'No\') as inDefault,\n\nif(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)  as portfolioAtRisk\n\n from m_group pgm\n join m_office o on o.id = pgm.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\n join m_loan l on l.group_id = pgm.id and l.client_id is not null\n left join m_currency cur on cur.code = l.currency_code\n join m_client c on c.id = l.client_id\n left join m_loan_arrears_aging laa on laa.loan_id = l.id\n left join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\n where pgm.id = ${programId}\n and l.loan_status_id = 300\norder by c.display_name, l.account_no\n\n','List of Loans in a Program',0,0),(110,'ChildrenStaffList','Table',NULL,'Quipo','\n select s.id, s.display_name,\ns.firstname, s.lastname, s.organisational_role_enum,\ns.organisational_role_parent_staff_id,\nsp.display_name as `organisational_role_parent_staff_display_name`\nfrom m_staff s\njoin m_staff sp on s.organisational_role_parent_staff_id = sp.id\nwhere s.organisational_role_parent_staff_id = ${staffId}\n','Get Next Level Down Staff',0,0),(111,'CoordinatorStats','Table',NULL,'Quipo','\nselect ifnull(cur.display_symbol, l.currency_code) as Currency,\n/*This query will return more than one entry if more than one currency is used */\ncount(distinct(c.id)) as activeClients, count(*) as activeLoans,\nsum(l.principal_disbursed_derived) as disbursedAmount,\nsum(l.principal_outstanding_derived) as loanOutstandingAmount,\nround((sum(l.principal_outstanding_derived) * 100) /  sum(l.principal_disbursed_derived),2) as loanOutstandingPC,\nsum(ifnull(lpa.principal_in_advance_derived,0.0)) as LoanPaidInAdvance,\nsum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) as portfolioAtRisk,\n\nround((sum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) * 100) / sum(l.principal_outstanding_derived), 2) as portfolioAtRiskPC,\n\ncount(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) as clientsInDefault,\nround((count(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) * 100) / count(distinct(c.id)),2) as clientsInDefaultPC,\n(sum(l.principal_disbursed_derived) / count(*))  as averageLoanAmount\nfrom m_staff coord\njoin m_staff fa on fa.organisational_role_parent_staff_id = coord.id\njoin m_office o on o.id = fa.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group pgm on pgm.staff_id = fa.id\njoin m_loan l on l.group_id = pgm.id and l.client_id is not null\nleft join m_currency cur on cur.code = l.currency_code\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\njoin m_client c on c.id = l.client_id\nwhere coord.id = ${staffId}\nand l.loan_status_id = 300\ngroup  by l.currency_code\n','Coordinator Statistics',0,0),(112,'BranchManagerStats','Table',NULL,'Quipo','\nselect ifnull(cur.display_symbol, l.currency_code) as Currency,\n/*This query will return more than one entry if more than one currency is used */\ncount(distinct(c.id)) as activeClients, count(*) as activeLoans,\nsum(l.principal_disbursed_derived) as disbursedAmount,\nsum(l.principal_outstanding_derived) as loanOutstandingAmount,\nround((sum(l.principal_outstanding_derived) * 100) /  sum(l.principal_disbursed_derived),2) as loanOutstandingPC,\nsum(ifnull(lpa.principal_in_advance_derived,0.0)) as LoanPaidInAdvance,\nsum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) as portfolioAtRisk,\n\nround((sum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) * 100) / sum(l.principal_outstanding_derived), 2) as portfolioAtRiskPC,\n\ncount(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) as clientsInDefault,\nround((count(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) * 100) / count(distinct(c.id)),2) as clientsInDefaultPC,\n(sum(l.principal_disbursed_derived) / count(*))  as averageLoanAmount\nfrom m_staff bm\njoin m_staff coord on coord.organisational_role_parent_staff_id = bm.id\njoin m_staff fa on fa.organisational_role_parent_staff_id = coord.id\njoin m_office o on o.id = fa.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group pgm on pgm.staff_id = fa.id\njoin m_loan l on l.group_id = pgm.id and l.client_id is not null\nleft join m_currency cur on cur.code = l.currency_code\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\njoin m_client c on c.id = l.client_id\nwhere bm.id = ${staffId}\nand l.loan_status_id = 300\ngroup  by l.currency_code\n','Branch Manager Statistics',0,0),(113,'ProgramDirectorStats','Table',NULL,'Quipo','\nselect ifnull(cur.display_symbol, l.currency_code) as Currency,\n/*This query will return more than one entry if more than one currency is used */\ncount(distinct(c.id)) as activeClients, count(*) as activeLoans,\nsum(l.principal_disbursed_derived) as disbursedAmount,\nsum(l.principal_outstanding_derived) as loanOutstandingAmount,\nround((sum(l.principal_outstanding_derived) * 100) /  sum(l.principal_disbursed_derived),2) as loanOutstandingPC,\nsum(ifnull(lpa.principal_in_advance_derived,0.0)) as LoanPaidInAdvance,\nsum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) as portfolioAtRisk,\n\nround((sum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) * 100) / sum(l.principal_outstanding_derived), 2) as portfolioAtRiskPC,\n\ncount(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) as clientsInDefault,\nround((count(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) * 100) / count(distinct(c.id)),2) as clientsInDefaultPC,\n(sum(l.principal_disbursed_derived) / count(*))  as averageLoanAmount\nfrom m_staff pd\njoin m_staff bm on bm.organisational_role_parent_staff_id = pd.id\njoin m_staff coord on coord.organisational_role_parent_staff_id = bm.id\njoin m_staff fa on fa.organisational_role_parent_staff_id = coord.id\njoin m_office o on o.id = fa.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group pgm on pgm.staff_id = fa.id\njoin m_loan l on l.group_id = pgm.id and l.client_id is not null\nleft join m_currency cur on cur.code = l.currency_code\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\njoin m_client c on c.id = l.client_id\nwhere pd.id = ${staffId}\nand l.loan_status_id = 300\ngroup  by l.currency_code\n','Program DirectorStatistics',0,0),(114,'ProgramStats','Table',NULL,'Quipo','\nselect ifnull(cur.display_symbol, l.currency_code) as Currency,\n/*This query will return more than one entry if more than one currency is used */\ncount(distinct(c.id)) as activeClients, count(*) as activeLoans,\nsum(l.principal_disbursed_derived) as disbursedAmount,\nsum(l.principal_outstanding_derived) as loanOutstandingAmount,\nround((sum(l.principal_outstanding_derived) * 100) /  sum(l.principal_disbursed_derived),2) as loanOutstandingPC,\nsum(ifnull(lpa.principal_in_advance_derived,0.0)) as LoanPaidInAdvance,\nsum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) as portfolioAtRisk,\n\nround((sum(\n	if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n		l.principal_outstanding_derived,0)) * 100) / sum(l.principal_outstanding_derived), 2) as portfolioAtRiskPC,\n\ncount(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) as clientsInDefault,\nround((count(distinct(\n		if(date_sub(curdate(), interval 28 day) > ifnull(laa.overdue_since_date_derived, curdate()),\n			c.id,null))) * 100) / count(distinct(c.id)),2) as clientsInDefaultPC,\n(sum(l.principal_disbursed_derived) / count(*))  as averageLoanAmount\nfrom m_group pgm\njoin m_office o on o.id = pgm.office_id\n			and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_loan l on l.group_id = pgm.id and l.client_id is not null\nleft join m_currency cur on cur.code = l.currency_code\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\nleft join m_loan_paid_in_advance lpa on lpa.loan_id = l.id\njoin m_client c on c.id = l.client_id\nwhere pgm.id = ${programId}\nand l.loan_status_id = 300\ngroup  by l.currency_code\n','Program Statistics',0,0),(115,'ClientSummary ','Table',NULL,NULL,'SELECT x.* FROM m_client c, m_office o, \n(\n       SELECT a.loanCycle, a.activeLoans, b.lastLoanAmount, d.activeSavings, d.totalSavings FROM \n	(SELECT IFNULL(MAX(l.loan_counter),0) AS loanCycle, COUNT(l.id) AS activeLoans FROM m_loan l WHERE l.loan_status_id=300 AND l.client_id=${clientId}) a, \n	(SELECT count(l.id), IFNULL(l.principal_amount,0) AS \'lastLoanAmount\' FROM m_loan l WHERE l.client_id=${clientId} AND l.disbursedon_date = (SELECT IFNULL(MAX(disbursedon_date),NOW()) FROM m_loan where client_id=${clientId} and loan_status_id=300)) b, \n	(SELECT COUNT(s.id) AS \'activeSavings\', IFNULL(SUM(s.account_balance_derived),0) AS \'totalSavings\' FROM m_savings_account s WHERE s.status_enum=300 AND s.client_id=${clientId}) d\n) x\nWHERE c.id=${clientId} AND o.id = c.office_id AND o.hierarchy LIKE CONCAT(\'${currentUserHierarchy}\', \'%\')','Utility query for getting the client summary details',1,0),(116,'LoanCyclePerProduct','Table',NULL,NULL,'SELECT lp.name AS \'productName\', MAX(l.loan_product_counter) AS \'loanProductCycle\' FROM m_loan l JOIN m_product_loan lp ON l.product_id=lp.id WHERE lp.include_in_borrower_cycle=1 AND l.loan_product_counter IS NOT NULL AND l.client_id=${clientId} GROUP BY l.product_id','Utility query for getting the client loan cycle details',1,0),(117,'GroupSavingSummary','Table',NULL,NULL,'select ifnull(cur.display_symbol, sa.currency_code) as currency,\ncount(sa.id) as totalSavingAccounts, ifnull(sum(sa.account_balance_derived),0) as totalSavings\nfrom m_group topgroup\njoin m_office o on o.id = topgroup.office_id and o.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_group g on g.hierarchy like concat(topgroup.hierarchy, \'%\')\njoin m_savings_account sa on sa.group_id = g.id\nleft join m_currency cur on cur.code = sa.currency_code\nwhere topgroup.id = ${groupId}\nand sa.activatedon_date is not null\ngroup by sa.currency_code','Utility query for getting group or center saving summary details for a group_id',1,0),(118,'Savings Transactions','Pentaho',NULL,'Savings',NULL,NULL,0,1),(119,'Client Savings Summary','Pentaho',NULL,'Savings',NULL,NULL,0,1),(120,'Active Loans - Details(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(121,'Active Loans - Summary(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(122,'Active Loans by Disbursal Period(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(123,'Active Loans in last installment Summary(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(124,'Active Loans in last installment(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(125,'Active Loans Passed Final Maturity Summary(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(126,'Active Loans Passed Final Maturity(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(127,'Aging Detail(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(128,'Aging Summary (Arrears in Months)(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(129,'Aging Summary (Arrears in Weeks)(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(130,'Client Listing(Pentaho)','Pentaho',NULL,'Client','(NULL)','(NULL)',1,1),(131,'Client Loans Listing(Pentaho)','Pentaho',NULL,'Client','(NULL)','(NULL)',1,1),(132,'Expected Payments By Date - Basic(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(133,'Funds Disbursed Between Dates Summary by Office(Pentaho)','Pentaho',NULL,'Fund','(NULL)','(NULL)',1,1),(134,'Funds Disbursed Between Dates Summary(Pentaho)','Pentaho',NULL,'Fund','(NULL)','(NULL)',1,1),(135,'Loans Awaiting Disbursal Summary by Month(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(136,'Loans Awaiting Disbursal Summary(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(137,'Loans Awaiting Disbursal(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(138,'Loans Pending Approval(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(139,'Obligation Met Loans Details(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(140,'Obligation Met Loans Summary(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(141,'Portfolio at Risk by Branch(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(142,'Portfolio at Risk(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(143,'Rescheduled Loans(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(144,'TxnRunningBalances(Pentaho)','Pentaho',NULL,'Transaction','(NULL)','(NULL)',1,1),(145,'Written-Off Loans(Pentaho)','Pentaho',NULL,'Loan','(NULL)','(NULL)',1,1),(146,'Client Saving Transactions','Pentaho',NULL,'Savings',NULL,NULL,0,0),(147,'Client Loan Account Schedule','Pentaho',NULL,'Loans',NULL,NULL,0,0),(148,'GroupNamesByStaff','Table','','','Select gr.id as id, gr.display_name as name from m_group gr where gr.level_id=1 and gr.staff_id = ${staffId}','',1,0),(149,'ClientTrendsByDay','Table','','Client','SELECT 	COUNT(cl.id) AS count, \n		cl.activation_date AS days\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (cl.activation_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 DAY) AND DATE(NOW()- INTERVAL 1 DAY))\nGROUP BY days','Retrieves the number of clients joined in last 12 days',1,0),(150,'ClientTrendsByWeek','Table','','Client','SELECT 	COUNT(cl.id) AS count, \n		WEEK(cl.activation_date) AS Weeks\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (cl.activation_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 WEEK) AND DATE(NOW()))\nGROUP BY Weeks','',1,0),(151,'ClientTrendsByMonth','Table','','Client','SELECT 	COUNT(cl.id) AS count, \n		MONTHNAME(cl.activation_date) AS Months\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (cl.activation_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) AND DATE(NOW()))\nGROUP BY Months','',1,0),(152,'LoanTrendsByDay','Table','','Loan','SELECT 	COUNT(ln.id) AS lcount, \n		ln.disbursedon_date AS days\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\n	LEFT JOIN m_loan ln on cl.id = ln.client_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (ln.disbursedon_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 DAY) AND DATE(NOW()- INTERVAL 1 DAY))\nGROUP BY days','Retrieves Number of loans disbursed for last 12 days',1,0),(153,'LoanTrendsByWeek','Table','','Loan','SELECT 	COUNT(ln.id) AS lcount, \n		WEEK(ln.disbursedon_date) AS Weeks\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\n	LEFT JOIN m_loan ln on cl.id = ln.client_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (ln.disbursedon_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 WEEK) AND DATE(NOW()))\nGROUP BY Weeks','',1,0),(154,'LoanTrendsByMonth','Table','','Loan','SELECT 	COUNT(ln.id) AS lcount, \n		MONTHNAME(ln.disbursedon_date) AS Months\nFROM m_office of \n	LEFT JOIN m_client cl on of.id = cl.office_id\n	LEFT JOIN m_loan ln on cl.id = ln.client_id\nWHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n	AND (ln.disbursedon_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) AND DATE(NOW()))\nGROUP BY Months','',1,0),(155,'Demand_Vs_Collection','Table','','Loan','select amount.AmountDue-amount.AmountPaid as AmountDue, amount.AmountPaid as AmountPaid from\n(SELECT \n(IFNULL(SUM(ls.principal_amount),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0)\n + IFNULL(SUM(ls.interest_amount),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) \n - IFNULL(SUM(ls.interest_waived_derived),0)\n + IFNULL(SUM(ls.fee_charges_amount),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) \n - IFNULL(SUM(ls.fee_charges_waived_derived),0)\n + IFNULL(SUM(ls.penalty_charges_amount),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) \n - IFNULL(SUM(ls.penalty_charges_waived_derived),0)\n) AS AmountDue, \n\n(IFNULL(SUM(ls.principal_completed_derived),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0) + IFNULL(SUM(ls.interest_completed_derived),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) \n - IFNULL(SUM(ls.interest_waived_derived),0)\n + IFNULL(SUM(ls.fee_charges_completed_derived),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) \n - IFNULL(SUM(ls.fee_charges_waived_derived),0)\n + IFNULL(SUM(ls.penalty_charges_completed_derived),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) \n - IFNULL(SUM(ls.penalty_charges_waived_derived),0)\n) AS AmountPaid\nFROM m_office of\nLEFT JOIN m_client cl ON of.id = cl.office_id\nLEFT JOIN m_loan ln ON cl.id = ln.client_id\nLEFT JOIN m_loan_repayment_schedule ls ON ln.id = ls.loan_id\nWHERE ls.duedate = DATE(NOW()) AND \n (of.hierarchy LIKE CONCAT((\nSELECT ino.hierarchy\nFROM m_office ino\nWHERE ino.id = ${officeId}),\"%\"))) as amount','Demand Vs Collection',1,0),(156,'Disbursal_Vs_Awaitingdisbursal','Table','','Loan','select awaitinddisbursal.amount-disbursedAmount.amount as amountToBeDisburse, disbursedAmount.amount as disbursedAmount from \n(\nSELECT 	COUNT(ln.id) AS noOfLoans, \n			IFNULL(SUM(ln.principal_amount),0) AS amount\nFROM \nm_office of\nLEFT JOIN m_client cl ON cl.office_id = of.id\nLEFT JOIN m_loan ln ON cl.id = ln.client_id\nWHERE \nln.expected_disbursedon_date = DATE(NOW()) AND \n(ln.loan_status_id=200 OR ln.loan_status_id=300) AND\n of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" )\n) awaitinddisbursal,\n(\nSELECT 	COUNT(ltrxn.id) as count, \n			IFNULL(SUM(ltrxn.amount),0) as amount \nFROM \nm_office of\nLEFT JOIN m_client cl ON cl.office_id = of.id\nLEFT JOIN m_loan ln ON cl.id = ln.client_id\nLEFT JOIN m_loan_transaction ltrxn ON ln.id = ltrxn.loan_id\nWHERE \nltrxn.transaction_date = DATE(NOW()) AND \nltrxn.is_reversed = 0 AND\nltrxn.transaction_type_enum=1 AND\n of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),\"%\" ) \n) disbursedAmount','Disbursal_Vs_Awaitingdisbursal',1,0),(157,'Savings Transaction Receipt','Pentaho',NULL,NULL,NULL,NULL,0,1),(158,'Loan Transaction Receipt','Pentaho',NULL,NULL,NULL,NULL,0,1),(159,'Staff Assignment History','Pentaho',NULL,NULL,NULL,NULL,0,1),(160,'GeneralLedgerReport','Pentaho',NULL,'Accounting',NULL,NULL,0,1);
/*!40000 ALTER TABLE `stretchy_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stretchy_report_parameter`
--

DROP TABLE IF EXISTS `stretchy_report_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stretchy_report_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) NOT NULL,
  `parameter_id` int(11) NOT NULL,
  `report_parameter_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_parameter_unique` (`report_id`,`parameter_id`),
  KEY `fk_report_parameter_001_idx` (`report_id`),
  KEY `fk_report_parameter_002_idx` (`parameter_id`),
  CONSTRAINT `fk_report_parameter_001` FOREIGN KEY (`report_id`) REFERENCES `stretchy_report` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_report_parameter_002` FOREIGN KEY (`parameter_id`) REFERENCES `stretchy_parameter` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=433 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stretchy_report_parameter`
--

LOCK TABLES `stretchy_report_parameter` WRITE;
/*!40000 ALTER TABLE `stretchy_report_parameter` DISABLE KEYS */;
INSERT INTO `stretchy_report_parameter` VALUES (1,1,5,NULL),(2,2,5,NULL),(3,2,6,NULL),(4,2,10,NULL),(5,2,20,NULL),(6,2,25,NULL),(7,2,26,NULL),(8,5,5,NULL),(9,5,6,NULL),(10,5,10,NULL),(11,5,20,NULL),(12,5,25,NULL),(13,5,26,NULL),(14,6,5,NULL),(15,6,6,NULL),(16,6,10,NULL),(17,6,20,NULL),(18,6,25,NULL),(19,6,26,NULL),(20,7,5,NULL),(21,7,6,NULL),(22,7,10,NULL),(23,7,20,NULL),(24,7,25,NULL),(25,7,26,NULL),(26,8,5,NULL),(27,8,6,NULL),(28,8,10,NULL),(29,8,25,NULL),(30,8,26,NULL),(31,11,5,NULL),(32,11,6,NULL),(33,11,10,NULL),(34,11,20,NULL),(35,11,25,NULL),(36,11,26,NULL),(37,11,100,NULL),(38,12,5,NULL),(39,12,6,NULL),(40,12,10,NULL),(41,12,20,NULL),(42,12,25,NULL),(43,12,26,NULL),(44,13,1,NULL),(45,13,2,NULL),(46,13,3,NULL),(47,13,5,NULL),(48,13,6,NULL),(49,13,10,NULL),(50,13,20,NULL),(51,13,25,NULL),(52,13,26,NULL),(53,14,1,NULL),(54,14,2,NULL),(55,14,3,NULL),(56,14,5,NULL),(57,14,6,NULL),(58,14,10,NULL),(59,14,20,NULL),(60,14,25,NULL),(61,14,26,NULL),(62,15,5,NULL),(63,15,6,NULL),(64,15,10,NULL),(65,15,20,NULL),(66,15,25,NULL),(67,15,26,NULL),(68,15,100,NULL),(69,16,5,NULL),(70,16,6,NULL),(71,16,10,NULL),(72,16,20,NULL),(73,16,25,NULL),(74,16,26,NULL),(75,16,100,NULL),(76,20,1,NULL),(77,20,2,NULL),(78,20,10,NULL),(79,20,20,NULL),(80,21,1,NULL),(81,21,2,NULL),(82,21,5,NULL),(83,21,10,NULL),(84,21,20,NULL),(85,48,5,'branch'),(86,48,2,'date'),(87,49,5,'branch'),(88,49,1,'fromDate'),(89,49,2,'toDate'),(90,50,5,'branch'),(91,50,1,'fromDate'),(92,50,2,'toDate'),(93,51,1,NULL),(94,51,2,NULL),(95,51,5,NULL),(96,51,10,NULL),(97,51,25,NULL),(98,52,5,NULL),(99,53,5,NULL),(100,53,10,NULL),(101,54,1,NULL),(102,54,2,NULL),(103,54,5,NULL),(104,54,10,NULL),(105,54,25,NULL),(106,55,5,NULL),(107,55,6,NULL),(108,55,10,NULL),(109,55,20,NULL),(110,55,25,NULL),(111,55,26,NULL),(112,56,5,NULL),(113,56,6,NULL),(114,56,10,NULL),(115,56,20,NULL),(116,56,25,NULL),(117,56,26,NULL),(118,56,100,NULL),(119,57,5,NULL),(120,57,6,NULL),(121,57,10,NULL),(122,57,20,NULL),(123,57,25,NULL),(124,57,26,NULL),(125,58,5,NULL),(126,58,6,NULL),(127,58,10,NULL),(128,58,20,NULL),(129,58,25,NULL),(130,58,26,NULL),(131,58,100,NULL),(132,59,1,NULL),(133,59,2,NULL),(134,59,5,NULL),(135,59,6,NULL),(136,59,10,NULL),(137,59,20,NULL),(138,59,25,NULL),(139,59,26,NULL),(140,61,5,NULL),(141,61,10,NULL),(142,92,1,'fromDate'),(143,92,5,'selectOffice'),(144,92,2,'toDate'),(145,93,1,NULL),(146,93,2,NULL),(147,93,5,NULL),(148,93,6,NULL),(149,94,2,'endDate'),(150,94,6,'loanOfficerId'),(151,94,5,'officeId'),(152,94,1,'startDate'),(256,106,2,NULL),(257,106,6,NULL),(258,106,5,NULL),(259,106,1,NULL),(263,118,1,'fromDate'),(264,118,2,'toDate'),(265,118,1004,'accountNo'),(266,119,1,'fromDate'),(267,119,2,'toDate'),(268,119,5,'selectOffice'),(269,119,1005,'selectProduct'),(270,120,5,'branch'),(271,120,6,'loanOfficer'),(272,120,10,'currencyId'),(273,120,20,'fundId'),(274,120,25,'loanProductId'),(275,120,26,'loanPurposeId'),(276,121,5,'Branch'),(277,121,6,'loanOfficer'),(278,121,10,'CurrencyId'),(279,121,20,'fundId'),(280,121,25,'loanProductId'),(281,121,26,'loanPurposeId'),(282,121,100,'parType'),(283,122,5,'Branch'),(284,122,6,'loanOfficer'),(285,122,10,'CurrencyId'),(286,122,20,'fundId'),(287,122,25,'loanProductId'),(288,122,26,'loanPurposeId'),(289,122,1,'startDate'),(290,122,2,'endDate'),(291,123,5,'Branch'),(292,123,6,'Loan Officer'),(293,123,10,'CurrencyId'),(294,123,20,'fundId'),(295,123,25,'loanProductId'),(296,123,26,'loanPurposeId'),(297,123,100,'parType'),(298,124,5,'Branch'),(299,124,6,'Loan Officer'),(300,124,10,'CurrencyId'),(301,124,20,'fundId'),(302,124,25,'loanProductId'),(303,124,26,'loanPurposeId'),(304,125,5,'Branch'),(305,125,6,'Loan Officer'),(306,125,10,'CurrencyId'),(307,125,20,'fundId'),(308,125,25,'loanProductId'),(309,125,26,'loanPurposeId'),(310,125,100,'parType'),(311,126,5,'Branch'),(312,126,6,'Loan Officer'),(313,126,10,'CurrencyId'),(314,126,20,'fundId'),(315,126,25,'loanProductId'),(316,126,26,'loanPurposeId'),(317,127,5,'Branch'),(318,128,5,'Branch'),(319,128,10,'CurrencyId'),(320,129,5,'Branch'),(321,129,10,'CurrencyId'),(322,130,5,'selectOffice'),(323,131,5,'Branch'),(324,131,6,'Loan Officer'),(325,131,10,'CurrencyId'),(326,131,20,'fundId'),(327,131,25,'loanProductId'),(328,131,26,'loanPurposeId'),(329,132,5,'Branch'),(330,132,6,'Loan Officer'),(331,132,1,'startDate'),(332,132,2,'endDate'),(333,133,5,'Branch'),(334,133,10,'CurrencyId'),(335,133,20,'fundId'),(336,133,1,'startDate'),(337,133,2,'endDate'),(338,134,10,'CurrencyId'),(339,134,20,'fundId'),(340,134,1,'startDate'),(341,134,2,'endDate'),(342,135,5,'Branch'),(343,135,6,'Loan Officer'),(344,135,10,'CurrencyId'),(345,135,20,'fundId'),(346,135,25,'loanProductId'),(347,135,26,'loanPurposeId'),(348,136,5,'Branch'),(349,136,6,'Loan Officer'),(350,136,10,'CurrencyId'),(351,136,20,'fundId'),(352,136,25,'loanProductId'),(353,136,26,'loanPurposeId'),(354,137,5,'Branch'),(355,137,6,'Loan Officer'),(356,137,10,'CurrencyId'),(357,137,20,'fundId'),(358,137,25,'loanProductId'),(359,137,26,'loanPurposeId'),(360,138,5,'Branch'),(361,138,6,'Loan Officer'),(362,138,10,'CurrencyId'),(363,138,20,'fundId'),(364,138,25,'loanProductId'),(365,138,26,'loanPurposeId'),(366,139,5,'Branch'),(367,139,6,'Loan Officer'),(368,139,10,'CurrencyId'),(369,139,20,'fundId'),(370,139,25,'loanProductId'),(371,139,26,'loanPurposeId'),(372,139,1,'startDate'),(373,139,2,'endDate'),(374,139,3,'obligDateType'),(375,140,5,'Branch'),(376,140,6,'Loan Officer'),(377,140,10,'CurrencyId'),(378,140,20,'fundId'),(379,140,25,'loanProductId'),(380,140,26,'loanPurposeId'),(381,140,1,'Startdate'),(382,140,2,'Enddate'),(383,140,3,'obligDateType'),(384,141,5,'Branch'),(385,141,6,'Loan Officer'),(386,141,10,'CurrencyId'),(387,141,20,'fundId'),(388,141,25,'loanProductId'),(389,141,26,'loanPurposeId'),(390,141,100,'parType'),(391,142,5,'Branch'),(392,142,6,'loanOfficer'),(393,142,10,'CurrencyId'),(394,142,20,'fundId'),(395,142,25,'loanProductId'),(396,142,26,'loanPurposeId'),(397,142,100,'parType'),(398,143,5,'Branch'),(399,143,10,'CurrencyId'),(400,143,25,'loanProductId'),(401,143,1,'startDate'),(402,143,2,'endDate'),(403,144,5,'Branch'),(404,144,6,'Loan Officer'),(405,144,1,'startDate'),(406,144,2,'endDate'),(407,145,5,'Branch'),(408,145,10,'CurrencyId'),(409,145,25,'loanProductId'),(410,145,1,'startDate'),(411,145,2,'endDate'),(412,146,1,'startDate'),(413,146,2,'endDate'),(414,146,1004,'accountNo'),(415,147,1,'startDate'),(416,147,2,'endDate'),(417,147,1004,'accountNo'),(418,149,5,''),(419,150,5,''),(420,151,5,''),(421,152,5,''),(422,153,5,''),(423,154,5,''),(424,155,5,''),(425,156,5,''),(426,157,1006,'transactionId'),(427,158,1006,'transactionId'),(428,159,1007,'centerId'),(429,160,1008,'account'),(430,160,1,'fromDate'),(431,160,2,'toDate'),(432,160,5,'branch');
/*!40000 ALTER TABLE `stretchy_report_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `x_registered_table`
--

DROP TABLE IF EXISTS `x_registered_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `x_registered_table` (
  `registered_table_name` varchar(50) NOT NULL,
  `application_table_name` varchar(50) NOT NULL,
  `category` int(11) NOT NULL DEFAULT '100',
  PRIMARY KEY (`registered_table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `x_registered_table`
--

LOCK TABLES `x_registered_table` WRITE;
/*!40000 ALTER TABLE `x_registered_table` DISABLE KEYS */;
INSERT INTO `x_registered_table` VALUES ('Banking', 'm_client', 100),
('CPV', 'm_loan', 100),
('Gold Packet', 'm_loan', 100),
('INSURANCE', 'm_loan', 100),
('PDC', 'm_loan', 100),
('TVR', 'm_loan', 100),
('TW specs', 'm_loan', 100);

/*!40000 ALTER TABLE `x_registered_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PDC`
--

CREATE TABLE IF NOT EXISTS `PDC` (
  `loan_id` bigint(20) NOT NULL,
  `Cheque No` int(11) NOT NULL,
  `Cheque Date` date NOT NULL,
  `Bank if other` varchar(25) DEFAULT NULL,
  `Branch` varchar(25) DEFAULT NULL,
  `PDC amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `TVR`
--

CREATE TABLE IF NOT EXISTS `TVR` (
  `loan_id` bigint(20) NOT NULL,
  `ClientType_cd_Customer Category` int(11) NOT NULL,
  `Current Residential Address` varchar(60) NOT NULL,
  `Year since_cd_No of years at current residence` int(11) NOT NULL,
  `Spoken to` text NOT NULL,
  `Relationship_cd_Relationship with applicant` int(11) NOT NULL,
  `Loan Amount applied` int(11) NOT NULL,
  `LoanPurpose_cd_Loan purpose` int(11) NOT NULL,
  `Asset Owned_cd_Asset owned` int(11) NOT NULL,
  `Other Loans_cd_Running loans from other institution` int(11) NOT NULL,
  `DOB` date NOT NULL,
  `Number_cd_Earning family members` int(11) NOT NULL,
  `Remarks` text NOT NULL,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `TW specs`
--

CREATE TABLE IF NOT EXISTS `TW specs` (
  `loan_id` bigint(20) NOT NULL,
  `Reg date` date DEFAULT NULL,
  `Dealer Name` varchar(60) DEFAULT NULL,
  `Engine No` varchar(25) DEFAULT NULL,
  `Chassis No` varchar(25) DEFAULT NULL,
  `Vehicle Reg No` varchar(20) DEFAULT NULL,
  `Invoice No` varchar(25) DEFAULT NULL,
  `Invoice Date` date DEFAULT NULL,
  `Ex showroom price` int(11) DEFAULT NULL,
  `RTO fee` int(11) DEFAULT NULL,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------


--
-- Table structure for table `Banking`
--

CREATE TABLE IF NOT EXISTS `Banking` (
  `client_id` bigint(20) NOT NULL,
  `Bank Name` text NOT NULL,
  `IFSC Code` varchar(11) DEFAULT NULL,
  `Account No` varchar(16) NOT NULL,
  `Number_cd_No of outward return` int(11) DEFAULT NULL,
  `Number_cd_No of times min bal chgs` int(11) DEFAULT NULL,
  `Number_cd_No of EMI running` int(11) DEFAULT NULL,
  `Total EMI amount` int(11) DEFAULT NULL,
  `Low CR bal` int(11) DEFAULT NULL,
  `High CR bal` int(11) DEFAULT NULL,
  `Avg quaterly bal` int(11) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `CPV`
--

CREATE TABLE IF NOT EXISTS `CPV` (
  `loan_id` bigint(20) NOT NULL,
  `Remarks` text,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `Gold Packet`
--

CREATE TABLE IF NOT EXISTS `Gold Packet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `Number_cd_No of item` int(11) NOT NULL,
  `Packet Location_cd_Location` int(11) NOT NULL,
  `Time stamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_loan_id` (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `INSURANCE`
--

CREATE TABLE IF NOT EXISTS `INSURANCE` (
  `loan_id` bigint(20) NOT NULL,
  `Insurance Company List_cd_Insurance Company` int(11) NOT NULL,
  `Insurance Expiry` datetime NOT NULL,
  `Policy No` varchar(30) DEFAULT NULL,
  `Premium Amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `x_table_column_code_mappings`
--

DROP TABLE IF EXISTS `x_table_column_code_mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `x_table_column_code_mappings` (
  `column_alias_name` varchar(50) NOT NULL,
  `code_id` int(10) NOT NULL,
  PRIMARY KEY (`column_alias_name`),
  KEY `FK_x_code_id` (`code_id`),
  CONSTRAINT `FK_x_code_id` FOREIGN KEY (`code_id`) REFERENCES `m_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `x_table_column_code_mappings`
--

LOCK TABLES `x_table_column_code_mappings` WRITE;
/*!40000 ALTER TABLE `x_table_column_code_mappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `x_table_column_code_mappings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-03  3:02:20
