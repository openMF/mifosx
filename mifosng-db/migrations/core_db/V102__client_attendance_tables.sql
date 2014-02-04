CREATE TABLE `m_meeting` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`calendar_instance_id` BIGINT(20) NOT NULL,
	`meeting_date` DATE NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_calendar_instance_id_meeting_date` (`calendar_instance_id`, `meeting_date`),
	CONSTRAINT `FK_m_calendar_instance_m_meeting` FOREIGN KEY (`calendar_instance_id`) REFERENCES `m_calendar_instance` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

CREATE TABLE `m_client_attendance` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL DEFAULT '0',
	`meeting_id` BIGINT(20) NOT NULL,
	`attendance_type_enum` SMALLINT(5) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_client_meeting_attendance` (`client_id`, `meeting_id`),
	INDEX `FK_m_meeting_m_client_attendance` (`meeting_id`),
	CONSTRAINT `FK_m_client_m_client_attendance` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_m_meeting_m_client_attendance` FOREIGN KEY (`meeting_id`) REFERENCES `m_meeting` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_MEETING', 'MEETING', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_MEETING', 'MEETING', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_MEETING', 'MEETING', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'SAVEORUPDATEATTENDANCE_MEETING', 'MEETING', 'SAVEORUPDATEATTENDANCE', 0);
