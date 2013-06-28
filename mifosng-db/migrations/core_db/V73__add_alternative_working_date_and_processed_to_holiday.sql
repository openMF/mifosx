ALTER TABLE `m_holiday`
	ADD COLUMN `alternative_working_date` DATETIME NOT NULL AFTER `to_date`,
	ADD COLUMN `processed` TINYINT(1) NOT NULL DEFAULT '0' AFTER `alternative_working_date`;