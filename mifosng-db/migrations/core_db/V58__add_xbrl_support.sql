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
  `namespace` varchar(100) DEFAULT NULL,
  `dimension` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `need_mapping` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_taxonomy` WRITE;
/*!40000 ALTER TABLE `m_taxonomy` DISABLE KEYS */;

INSERT INTO `m_taxonomy` (`id`, `name`, `namespace`, `dimension`, `description`, `type`, `need_mapping`)
VALUES
	(1,'AdministrativeExpense','ifrs',NULL,NULL,2,1),
	(2,'Assets','mix',NULL,'All outstanding principals due for all outstanding client loans. This includes current, delinquent, and renegotiated loans, but not loans that have been written off. It does not include interest recei',1,1),
	(3,'Assets','mix','MaturityDimension:LessThanOneYearMember','Segmentation based on the life of an asset or liability.',1,1),
	(4,'Assets','mix','MaturityDimension:MoreThanOneYearMember','Segmentation based on the life of an asset or liability.',1,1),
	(5,'CashAndCashEquivalents','ifrs',NULL,NULL,1,1),
	(6,'Deposits','mix',NULL,'The total value of funds placed in an account with an MFI that are payable to a depositor. This item includes any current, checking, or savings accounts that are payable on demand. It also includes ti',1,1);

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_taxonomy_mapping` WRITE;
/*!40000 ALTER TABLE `m_taxonomy_mapping` DISABLE KEYS */;

INSERT INTO `m_taxonomy_mapping` (`id`, `identifier`, `config`, `last_update_date`)
VALUES
	(1,'default','{1=1001+10002}',NULL);

/*!40000 ALTER TABLE `m_taxonomy_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table m_xbrl_report
# ------------------------------------------------------------

DROP TABLE IF EXISTS `m_xbrl_report`;

CREATE TABLE `m_xbrl_report` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `generate_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
