CREATE TABLE `m_product_loan_office` (
	`product_id` BIGINT(20) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`product_id`, `office_id`),
	INDEX `m_product_id_id_ibfk_1` (`product_id`),
	INDEX `m_product_office_id_ibfk_2` (`office_id`),
	CONSTRAINT `m_product_id_id_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`),
	CONSTRAINT `m_product_office_id_ibfk_2` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)