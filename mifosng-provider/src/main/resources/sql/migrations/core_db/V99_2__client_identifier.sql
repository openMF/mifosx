ALTER TABLE `m_client_identifier` 
	ADD COLUMN `proof_type_id` int(11) NOT NULL AFTER `document_type_id`,
	ADD COLUMN `validity` date DEFAULT NULL,
	ADD COLUMN `is_life_time` tinyint(1) DEFAULT NULL;
	