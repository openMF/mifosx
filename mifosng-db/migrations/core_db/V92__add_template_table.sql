CREATE TABLE `m_template` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT, 
	`name` VARCHAR(45) NOT NULL, 
	`text` LONGTEXT NOT NULL,
	PRIMARY KEY (`id`)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
CREATE TABLE `m_template_mappers` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`mapper_id` bigint(20) NOT NULL, 
	`resource` VARCHAR(100) NOT NULL, 
	`address` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
	
INSERT INTO `m_permission` 
	(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organistion', 'DELETE_TEMPLATE', 'TEMPLATE', 'DELETE', 0),
	   ('organistion', 'CREATE_TEMPLATE', 'TEMPLATE', 'CREATE', 0),
	   ('organistion', 'UPDATE_TEMPLATE', 'TEMPLATE', 'UPDATE', 0),
	   ('organistion', 'READ_TEMPLATE', 'TEMPLATE', 'READ', 0);
