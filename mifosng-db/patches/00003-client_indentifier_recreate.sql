drop table `m_client_identifier`;

CREATE TABLE `m_client_identifier` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`document_type_id` INT(11) NOT NULL,
	`document_key` VARCHAR(50) NOT NULL,
	`description` VARCHAR(1000) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_identifier_key` (`document_type_id`, `document_key`),
	UNIQUE INDEX `unique_client_identifier` (`client_id`, `document_type_id`),
	INDEX `FK_m_client_document_m_client` (`client_id`),
	INDEX `FK_m_client_document_m_code_value` (`document_type_id`),
	CONSTRAINT `FK_m_client_document_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_m_client_document_m_code_value` FOREIGN KEY (`document_type_id`) REFERENCES `m_code_value` (`id`)
)
ENGINE=InnoDB;
