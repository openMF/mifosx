ALTER TABLE `m_product_loan_recalculation_details`
	ADD COLUMN `arrears_based_on_original_schedule` TINYINT(1) NOT NULL DEFAULT '0';