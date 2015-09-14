insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'ACTIVATE_CLIENTCHARGE', 'CLIENTCHARGE', 'ACTIVATE', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'ACTIVATE_CLIENTCHARGE_CHECKER', 'CLIENTCHARGE', 'ACTIVATE_CHECKER', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'READ_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'READ', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'CREATE_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'CREATE', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'UPDATE_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'UPDATE', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'READ_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTRECURRINGCHARGE', 'READ_CHECKER', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'CREATE_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTRECURRINGCHARGE', 'CREATE_CHECKER', '0');

insert into `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) values ('portfolio', 'UPDATE_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTRECURRINGCHARGE', 'UPDATE_CHECKER', '0');

---Adding batch job Client Recurring charge
INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES ('Apply Recurring Charge On Client', 'Apply Recurring Charge On Client', '0 1 0 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL, 1, 0, 1, 0, 0);

CREATE TABLE `m_client_recurring_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`name` VARCHAR(100) NULL DEFAULT NULL,
	`charge_due_date` DATE NULL DEFAULT NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	`charge_applies_to_enum` SMALLINT(5) NOT NULL,
	`charge_time_enum` SMALLINT(5) NOT NULL,
	`charge_calculation_enum` SMALLINT(5) NOT NULL,
	`charge_payment_mode_enum` SMALLINT(5) NULL DEFAULT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`fee_on_day` SMALLINT(5) NULL DEFAULT NULL,
	`fee_interval` SMALLINT(5) NULL DEFAULT NULL,
	`fee_on_month` SMALLINT(5) NULL DEFAULT NULL,
	`is_penalty` TINYINT(1) NOT NULL DEFAULT '0',
	`is_active` TINYINT(1) NOT NULL,
	`min_cap` DECIMAL(19,6) NULL DEFAULT NULL,
	`max_cap` DECIMAL(19,6) NULL DEFAULT NULL,
	`fee_frequency` SMALLINT(5) NULL DEFAULT NULL,
	`inactivated_on_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_m_client_recurring_charge_m_client` (`client_id`),
	INDEX `FK_m_client_recurring_charge_m_charge` (`charge_id`),
	CONSTRAINT `FK_m_client_recurring_charge_m_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `FK_m_client_recurring_charge_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;