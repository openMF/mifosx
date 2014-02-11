INSERT INTO `m_code` (`id`, `code_name`, `is_system_defined`) VALUES (30, 'FundType', 1);

ALTER TABLE `m_fund` DROP INDEX `fund_name_org`, ADD UNIQUE INDEX `fund_name_org` (`name`, `fund_type_cv_id`);
	
ALTER TABLE `m_fund` ADD COLUMN `fund_type_cv_id` INT NULL DEFAULT NULL AFTER `external_id`;