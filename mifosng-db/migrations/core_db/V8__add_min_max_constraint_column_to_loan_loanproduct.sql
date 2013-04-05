ALTER TABLE `m_product_loan`
	ADD COLUMN `min_nominal_interest_rate_per_period` DECIMAL(19,6) NOT NULL AFTER `nominal_interest_rate_per_period`,
	ADD COLUMN `max_nominal_interest_rate_per_period` DECIMAL(19,6) NOT NULL AFTER `min_nominal_interest_rate_per_period`;
	
ALTER TABLE `m_product_loan`
	ADD COLUMN `min_number_of_repayments` SMALLINT(5) NOT NULL AFTER `number_of_repayments`,
	ADD COLUMN `max_number_of_repayments` SMALLINT(5) NOT NULL AFTER `min_number_of_repayments`;
	
ALTER TABLE `m_loan`
	ADD COLUMN `min_nominal_interest_rate_per_period` DECIMAL(19,6) NOT NULL AFTER `nominal_interest_rate_per_period`,
	ADD COLUMN `max_nominal_interest_rate_per_period` DECIMAL(19,6) NOT NULL AFTER `min_nominal_interest_rate_per_period`,
	ADD COLUMN `min_number_of_repayments` SMALLINT(5) NOT NULL AFTER `number_of_repayments`,
	ADD COLUMN `max_number_of_repayments` SMALLINT(5) NOT NULL AFTER `min_number_of_repayments`;	