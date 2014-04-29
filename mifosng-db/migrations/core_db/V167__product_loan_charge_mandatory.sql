ALTER TABLE `m_product_loan_charge`
	ADD COLUMN `id` BIGINT(20) NOT NULL AUTO_INCREMENT FIRST,
	ADD INDEX `id` (`id`);
	
ALTER TABLE `m_product_loan_charge`
	ADD COLUMN `is_mandatory` TINYINT(1) NOT NULL DEFAULT '0' AFTER `charge_id`;