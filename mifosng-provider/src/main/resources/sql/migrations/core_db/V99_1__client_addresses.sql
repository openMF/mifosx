CREATE TABLE `m_client_address` (
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
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;