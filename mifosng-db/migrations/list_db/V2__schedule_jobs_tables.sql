CREATE TABLE `jobrunhistory` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`job_tenant_id` BIGINT(20) NOT NULL,
	`version` BIGINT(20) NOT NULL,
	`start_time` DATETIME NOT NULL,
	`end_time` DATETIME NOT NULL,
	`status` VARCHAR(10) NOT NULL,
	`errormessage` VARCHAR(500) NULL DEFAULT NULL,
	`triggertype` VARCHAR(25) NOT NULL,
	`errorlog` TEXT NULL,
	PRIMARY KEY (`id`),
	INDEX `jobstenantFK` (`job_tenant_id`),
	CONSTRAINT `jobstenantFK` FOREIGN KEY (`job_tenant_id`) REFERENCES `jobs_tenants` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `jobs_tenants` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`job_id` INT(11) NOT NULL,
	`tenant_id` BIGINT(20) NOT NULL,
	`cron_expression` VARCHAR(20) NOT NULL,
	`create_time` DATETIME NOT NULL,
	`task_priority` SMALLINT(6) NOT NULL DEFAULT '5',
	`group_name` VARCHAR(50) NULL DEFAULT NULL,
	`previous_run_start_time` DATETIME NULL DEFAULT NULL,
	`next_run_time` DATETIME NULL DEFAULT NULL,
	`trigger_key` VARCHAR(500) NULL DEFAULT NULL,
	`job_initializing_errorlog` TEXT NULL,
	PRIMARY KEY (`id`),
	INDEX `jobtenantmapping` (`job_id`),
	INDEX `tenantjobmappig` (`tenant_id`),
	CONSTRAINT `jobtenantmapping` FOREIGN KEY (`job_id`) REFERENCES `schedulable_jobs` (`id`),
	CONSTRAINT `tenantjobmappig` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `schedulable_jobs` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`job_name` VARCHAR(50) NOT NULL,
	`job_instance_name` VARCHAR(50) NOT NULL,
	`desc` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO `schedulable_jobs` (`job_name`, `job_instance_name`, `desc`) VALUES ('Update loan Summary', 'updateLoanSummariesScheduledJob', 'Summary');
INSERT INTO `schedulable_jobs` ( `job_name`, `job_instance_name`, `desc`) VALUES ('Update Loan Arrears Ageing', 'updateLoanArrearsAgeingScheduledJob', 'Update Loan Arrears Ageing');
INSERT INTO `schedulable_jobs` (`job_name`, `job_instance_name`, `desc`) VALUES ('Update Loan Paid In Advance', 'updateLoanPaidInAdvanceScheduledJob', 'Update Loan Paid In Advance');
INSERT INTO `schedulable_jobs` (`job_name`, `job_instance_name`, `desc`) VALUES ('Apply Annual Fee For Savings', 'applyAnnualFeeForSavingsScheduledJob', 'Apply Annual Fee For Savings');
INSERT INTO `schedulable_jobs` (`job_name`, `job_instance_name`, `desc`) VALUES ('ApplyHolidaysToLoansScheduledJob', 'applyHolidaysToLoansScheduledJob', 'ApplyHolidaysToLoansScheduledJob');

INSERT INTO `jobs_tenants` (`job_id`, `tenant_id`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `trigger_key`, `job_initializing_errorlog`) VALUES (1, 1, '0 0 22 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `jobs_tenants` (`job_id`, `tenant_id`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `trigger_key`, `job_initializing_errorlog`) VALUES (2, 1, '0 1 0 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `jobs_tenants` (`job_id`, `tenant_id`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `trigger_key`, `job_initializing_errorlog`) VALUES (3, 1, '0 5 0 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `jobs_tenants` (`job_id`, `tenant_id`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `trigger_key`, `job_initializing_errorlog`) VALUES (4, 1, '0 20 22 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `jobs_tenants` (`job_id`, `tenant_id`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `trigger_key`, `job_initializing_errorlog`) VALUES (5, 1, '0 0 12 * * ?', now(), 5, NULL, NULL, NULL, NULL, NULL);
