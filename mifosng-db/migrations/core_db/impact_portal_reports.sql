INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Client_number', 'Table', 'Number of Clients', 'Portal', 'SELECT COUNT(DISTINCT client_id) AS ''Number of Clients'' FROM (SELECT client_id FROM m_savings_account WHERE m_savings_account.status_enum = 300 UNION SELECT client_id FROM m_loan WHERE m_loan.loan_status_id = 300) clients', 'Report that returns total number of clients who has loan or savings', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Loan_amount', 'Table', 'Outstanding Loans', 'Portal', 'SELECT sum(m_loan.approved_principal) AS ''Outstanding loans(principal)'', m_loan.currency_code as ''currency'' from m_loan where m_loan.loan_status_id=300 group by m_loan.currency_code', 'Report that returns total loan amount of the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Loan_number', 'Table', 'Loan Number', 'Portal', 'select count(m_loan.id) as ''number of active loans'' from m_loan where m_loan.loan_status_id =300', 'Report that returns total number of loans associates with the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Savings_amount', 'Table', 'Savings Amount', 'Portal', 'select sum(m_savings_account.account_balance_derived) as ''savings ammount'', m_savings_account.currency_code as ''currency'' from m_savings_account where m_savings_account.status_enum=300 group by m_savings_account.currency_code', 'Report that returns total savings amount associates with the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('PAR_1', 'Table', 'PAR1', 'Portal', 'select (o.overdue_total/l.total_loans)*100 as ''PAR1(%)'' from (select sum(total_overdue_derived) as ''overdue_total'' from m_loan_arrears_aging where datediff(now(), m_loan_arrears_aging.overdue_since_date_derived)>= 1) as o,(SELECT sum(m_loan.approved_principal) AS ''total_loans'' from m_loan where m_loan.loan_status_id=300) as l ','Report that returns PAR1', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('PAR_30', 'Table', 'PAR30', 'Portal', 'select (o.overdue_total/l.total_loans)*100 as ''PAR30(%)'' from (select sum(total_overdue_derived) as ''overdue_total'' from m_loan_arrears_aging where datediff(now(), m_loan_arrears_aging.overdue_since_date_derived)>= 30) as o,(SELECT sum(m_loan.approved_principal) AS ''total_loans'' from m_loan where m_loan.loan_status_id=300) as l ','Report that returns PAR30', 0, 1);

#create table for db caching
CREATE TABLE `impact_portal_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_captured` varchar(45) DEFAULT NULL,
  `datapoint` varchar(45) DEFAULT NULL,
  `datapoint_label` varchar(45) DEFAULT NULL,
  `value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

#adding new job
INSERT INTO `job`(`name`,`display_name`,`cron_expression`,`create_time`,`task_priority`,`job_key`,`is_active`,`currently_running`,`updates_allowed`,`scheduler_group`)
VALUES('Update impact portal cache', 'Update impact portal cache', '0 0 22 1/1 * ? *',Now(),5,1,0,1,0,0);

