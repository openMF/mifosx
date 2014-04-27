ALTER TABLE `m_charge` 
ADD COLUMN `is_mandatory` TINYINT(1) NOT NULL DEFAULT '0' AFTER `fee_frequency`;