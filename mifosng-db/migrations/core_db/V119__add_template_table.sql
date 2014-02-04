DROP TABLE IF EXISTS `m_template`;
CREATE TABLE `m_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `text` longtext COLLATE utf8_unicode_ci NOT NULL,
  `entity` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

DROP TABLE IF EXISTS `m_templatemappers`;
CREATE TABLE `m_templatemappers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mapperkey` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mapperorder` int(11) DEFAULT NULL,
  `mappervalue` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `m_template_m_templatemappers`;
CREATE TABLE `m_template_m_templatemappers` (
  `m_template_id` bigint(20) NOT NULL,
  `mappers_id` bigint(20) NOT NULL,
  UNIQUE KEY `mappers_id` (`mappers_id`),
  KEY (`mappers_id`),
  KEY (`m_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organistion', 'DELETE_TEMPLATE', 'TEMPLATE', 'DELETE', 0),
	   ('organistion', 'CREATE_TEMPLATE', 'TEMPLATE', 'CREATE', 0),
	   ('organistion', 'UPDATE_TEMPLATE', 'TEMPLATE', 'UPDATE', 0),
	   ('organistion', 'READ_TEMPLATE', 'TEMPLATE', 'READ', 0);