CREATE TABLE `m_fund_mapping_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_id` BIGINT(20) NOT NULL,
	`fund_type_cv_id` INT(20) NOT NULL,
	`start_date` DATE NULL DEFAULT NULL,
	`end_date` DATE NULL DEFAULT NULL,
	`locked` TINYINT(1) NOT NULL DEFAULT '0',
	`create_date` DATE NULL DEFAULT NULL,
	`update_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_m_loan_fund_map` (`loan_id`),
	INDEX `FK_m_code_value_fund_map` (`fund_type_cv_id`),
	CONSTRAINT `FK_m_loan_fund_map` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK_m_code_value_fund_map` FOREIGN KEY (`fund_type_cv_id`) REFERENCES `m_code_value` (`id`),
	UNIQUE INDEX `fund_loan_id_fundtype` (`loan_id`, `fund_type_cv_id`)
)
