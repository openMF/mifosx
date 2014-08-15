INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Client_number', 'Table', 'Number of Clients', 'Portal', 'SELECT COUNT(DISTINCT client_id) AS ''Number of Clients'' FROM (SELECT client_id FROM m_savings_account WHERE m_savings_account.status_enum = 300 UNION SELECT client_id FROM m_loan WHERE m_loan.loan_status_id = 300) clients', 'Report that returns total number of clients who has loan or savings', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Loan_amount', 'Table', 'Outstanding Loans', 'Portal', 'SELECT sum(m_loan.approved_principal) AS ''Outstanding loans(principal)'', m_loan.currency_code as ''currency'' from m_loan where m_loan.loan_status_id=300 group by m_loan.currency_code', 'Report that returns total loan amount of the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Loan_number', 'Table', 'Loan Number', 'Portal', 'select count(m_loan.id) as ''number of active loans'' from m_loan where m_loan.loan_status_id =300', 'Report that returns total number of loans associates with the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Portal_Savings_amount', 'Table', 'Savings Amount', 'Portal', 'select sum(m_savings_account.account_balance_derived) as ''savings ammount'', m_savings_account.currency_code as ''currency'' from m_savings_account where m_savings_account.status_enum=300 group by m_savings_account.currency_code', 'Report that returns total savings amount associates with the tenant', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('PAR_1', 'Table', 'PAR1', 'Portal', 'select (o.overdue_total/l.total_loans)*100 as ''PAR1(%)'' from (select sum(total_overdue_derived) as ''overdue_total'' from m_loan_arrears_aging where datediff(now(), m_loan_arrears_aging.overdue_since_date_derived)>= 1) as o,(SELECT sum(m_loan.approved_principal) AS ''total_loans'' from m_loan where m_loan.loan_status_id=300) as l ','Report that returns PAR1', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('PAR_30', 'Table', 'PAR30', 'Portal', 'select (o.overdue_total/l.total_loans)*100 as ''PAR30(%)'' from (select sum(total_overdue_derived) as ''overdue_total'' from m_loan_arrears_aging where datediff(now(), m_loan_arrears_aging.overdue_since_date_derived)>= 30) as o,(SELECT sum(m_loan.approved_principal) AS ''total_loans'' from m_loan where m_loan.loan_status_id=300) as l ','Report that returns PAR30', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Active_Clients', 'Table', 'Active Clients', 'Portal', 'select  ''Head office'' as name,
  SUM(CASE WHEN IFNULL((Select COUNT(client_id) FROM m_loan WHERE client_id = mc.id and loan_status_id = 300),0) > 0 THEN 1 AND @curClient := mc.id ELSE 0 END) as ''Active Clients'',
  SUM(CASE WHEN IFNULL((Select COUNT(client_id) FROM m_loan WHERE client_id = mc.id and loan_status_id >= 300 AND client_id != @curClient),0) > 0 THEN 1 ELSE 0 END) as ''Non Funded'',
  SUM(CASE WHEN IFNULL((Select COUNT(client_id) FROM m_loan WHERE client_id = mc.id),0) = 0 THEN 1 ELSE 0 END) as ''Prospects''
FROM (SELECT @curClient := 0) r, m_client mc 
LEFT JOIN m_office as mo ON mc.office_id=mo.id LIMIT 0,10 ','Report that returns no of active clients', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Savings_Balance_By_Office', 'Table', 'Savings By Office', 'Portal', 'select o.name,sum(sa.account_balance_derived) as balance
from m_savings_account as sa
inner join m_client c on c.id = sa.client_id
inner join m_office o on o.id = c.office_id
group by o.name
order by balance','Report that returns savings balance by office', 0, 1);


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

