CREATE TABLE `m_dsa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) DEFAULT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `lastname` varchar(50) DEFAULT NULL,
  `display_name` varchar(102) NOT NULL DEFAULT '',
  `mobile_no` varchar(10) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `display_name` (`display_name`),
  UNIQUE KEY `mobile_no_UNIQUE` (`mobile_no`),
  KEY `FK_m_dsa_m_office` (`office_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
