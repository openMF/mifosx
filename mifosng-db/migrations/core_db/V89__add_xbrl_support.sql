# ************************************************************
# Sequel Pro SQL dump
# Version 4004
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.29)
# Database: mifostenant-default
# Generation Time: 2013-07-24 14:14:12 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table m_taxonomy
# ------------------------------------------------------------

DROP TABLE IF EXISTS `m_taxonomy`;

CREATE TABLE `m_taxonomy` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `namespace_id` int(11) DEFAULT NULL,
  `dimension` varchar(100) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `period` tinyint(1) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `need_mapping` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_taxonomy` WRITE;
/*!40000 ALTER TABLE `m_taxonomy` DISABLE KEYS */;

INSERT INTO `m_taxonomy` (`id`, `name`, `namespace_id`, `dimension`, `type`, `unit`, `period`, `description`, `need_mapping`)
VALUES
	(1,'AdministrativeExpense',1,NULL,2,NULL,NULL,NULL,1),
	(2,'Assets',3,NULL,1,NULL,NULL,'All outstanding principals due for all outstanding client loans. This includes current, delinquent, and renegotiated loans, but not loans that have been written off. It does not include interest recei',1),
	(3,'Assets',3,'MaturityDimension:LessThanOneYearMember',1,NULL,NULL,'Segmentation based on the life of an asset or liability.',1),
	(4,'Assets',3,'MaturityDimension:MoreThanOneYearMember',1,NULL,NULL,'Segmentation based on the life of an asset or liability.',1),
	(5,'CashAndCashEquivalents',1,NULL,1,NULL,NULL,NULL,1),
	(6,'Deposits',3,NULL,1,NULL,NULL,'The total value of funds placed in an account with an MFI that are payable to a depositor. This item includes any current, checking, or savings accounts that are payable on demand. It also includes ti',1);

/*!40000 ALTER TABLE `m_taxonomy` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table m_taxonomy_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `m_taxonomy_mapping`;

CREATE TABLE `m_taxonomy_mapping` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `identifier` varchar(50) NOT NULL DEFAULT '',
  `config` varchar(200) DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  `currency` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_taxonomy_mapping` WRITE;
/*!40000 ALTER TABLE `m_taxonomy_mapping` DISABLE KEYS */;

INSERT INTO `m_taxonomy_mapping` (`id`, `identifier`, `config`, `last_update_date`, `currency`)
VALUES
	(1,'default','{\"1\":\"{11000}+{12000}\",\"2\":\"\"}',NULL,NULL);

/*!40000 ALTER TABLE `m_taxonomy_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table m_xbrl_namespace
# ------------------------------------------------------------

DROP TABLE IF EXISTS `m_xbrl_namespace`;

CREATE TABLE `m_xbrl_namespace` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `prefix` varchar(20) NOT NULL DEFAULT '',
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQUE` (`prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_xbrl_namespace` WRITE;
/*!40000 ALTER TABLE `m_xbrl_namespace` DISABLE KEYS */;

INSERT INTO `m_xbrl_namespace` (`id`, `prefix`, `url`)
VALUES
	(1,'ifrs','http://xbrl.iasb.org/taxonomy/2009-04-01/ifrs'),
	(2,'iso4217','http://www.xbrl.org/2003/iso4217'),
	(3,'mix','http://www.themix.org/int/fr/ifrs/basi/YYYY-MM-DD/mx-cor'),
	(4,'xbrldi','http://xbrl.org/2006/xbrldi'),
	(5,'xbrli','http://www.xbrl.org/2003/instance'),
	(6,'link','http://www.xbrl.org/2003/linkbase'),
	(7,'dc-all','http://www.themix.org/int/fr/ifrs/basi/2010-08-31/dc-all');

/*!40000 ALTER TABLE `m_xbrl_namespace` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table m_xbrl_report
# ------------------------------------------------------------

DROP TABLE IF EXISTS `m_xbrl_report`;

CREATE TABLE `m_xbrl_report` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `report_name` int(11) DEFAULT NULL,
  `result` varchar(500) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `currency` varchar(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
