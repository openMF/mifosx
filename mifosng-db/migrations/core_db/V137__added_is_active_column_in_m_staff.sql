ALTER TABLE `m_staff`
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1' AFTER `organisational_role_parent_staff_id`;