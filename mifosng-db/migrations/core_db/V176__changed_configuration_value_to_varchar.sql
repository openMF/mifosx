ALTER TABLE `c_configuration`
CHANGE COLUMN `value` `value` VARCHAR(256) NULL DEFAULT NULL AFTER `name`;
