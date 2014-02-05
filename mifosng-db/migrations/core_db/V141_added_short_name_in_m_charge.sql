ALTER TABLE `m_charge`
	ADD COLUMN `short_name` VARCHAR(4) NOT NULL AFTER `max_cap`,
	ADD UNIQUE INDEX `short_name` (`short_name`);