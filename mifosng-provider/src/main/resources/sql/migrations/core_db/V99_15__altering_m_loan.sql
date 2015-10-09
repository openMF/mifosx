ALTER TABLE m_product_loan
	ADD COLUMN `flat_interest_rate_per_period` decimal(19,6) DEFAULT NULL,
	ADD COLUMN `annual_flat_interest_rate` decimal(19,6) DEFAULT NULL;