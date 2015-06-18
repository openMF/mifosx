ALTER TABLE `m_code`
  ADD `parent_id` INT(11) NULL DEFAULT NULL;
  
ALTER TABLE `m_code_value`
  ADD `parent_id` INT(11) NULL DEFAULT NULL;

ALTER TABLE `m_code`
	ADD CONSTRAINT `FK_m_code_m_code` FOREIGN KEY (`parent_id`) REFERENCES `m_code` (`id`);
	
ALTER TABLE `m_code_value`
	ADD CONSTRAINT `FK_m_code_value_m_code_value` FOREIGN KEY (`parent_id`) REFERENCES `m_code_value` (`id`);