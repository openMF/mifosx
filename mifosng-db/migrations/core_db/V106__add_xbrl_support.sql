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

DROP TABLE IF EXISTS `m_taxonomy_mapping`;

CREATE TABLE `m_taxonomy_mapping` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `identifier` varchar(50) NOT NULL DEFAULT '',
  `config` varchar(200) DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  `currency` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `m_xbrl_namespace`;

CREATE TABLE `m_xbrl_namespace` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `prefix` varchar(20) NOT NULL DEFAULT '',
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQUE` (`prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `m_xbrl_namespace` WRITE;

INSERT INTO `m_xbrl_namespace` (`id`, `prefix`, `url`)
VALUES
	(1,'ifrs','http://xbrl.iasb.org/taxonomy/2009-04-01/ifrs'),
	(2,'iso4217','http://www.xbrl.org/2003/iso4217'),
	(3,'mix','http://www.themix.org/int/fr/ifrs/basi/YYYY-MM-DD/mx-cor'),
	(4,'xbrldi','http://xbrl.org/2006/xbrldi'),
	(5,'xbrli','http://www.xbrl.org/2003/instance'),
	(6,'link','http://www.xbrl.org/2003/linkbase'),
	(7,'dc-all','http://www.themix.org/int/fr/ifrs/basi/2010-08-31/dc-all');

UNLOCK TABLES;


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

