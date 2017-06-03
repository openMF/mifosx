CREATE TABLE `m_adhoc` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NULL DEFAULT NULL,
	`query` VARCHAR(2000) NULL DEFAULT NULL,
	`table_name` VARCHAR(100) NULL DEFAULT NULL,
	`table_fields` VARCHAR(1000) NULL DEFAULT NULL,
	`IsActive` TINYINT(1) NOT NULL DEFAULT '0',
	`created_date` DATETIME NULL DEFAULT NULL,
	`createdby_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `Created_by` FOREIGN KEY (`Created_by`) REFERENCES `m_appuser` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;
