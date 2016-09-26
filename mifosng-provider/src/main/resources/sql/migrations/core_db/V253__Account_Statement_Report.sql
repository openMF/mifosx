INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Saving Account Statement', 'Pentaho', NULL, 'Savings', NULL, NULL, 0, 1);

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Select Saving Account NO', 'Saving Account NO', 'Saving Account NO', 'select', 'number', '0', NULL, NULL, NULL, 'select msa.id,msa.account_no\r\nfrom m_savings_account msa\r\n  union all\r\nselect  -1,\'ALL\'\r\norder by 2', NULL);

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Saving Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='Select Saving Account NO'), 
  'Account');
  
   INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Saving Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='startDateselect'),
  'ondate');
  
    INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Saving Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='endDateselect'), 
  'todate');
  

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Saving Account Statement', 'Saving Account Statement', 'READ', 0);



INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan Account Statement', 'Pentaho', NULL, 'Loans', NULL, NULL, 0, 1);


 INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Select Loan Account NO', 'Loan Account NO', 'Loan Account NO', 'select', 'number', '0', NULL, NULL, NULL, 'select ml.id,mc.account_no\r\nfrom m_loan ml\r\nleft join m_client mc\r\non ml.client_id=mc.id\r\n  union all\r\nselect  -1,\'ALL\'\r\norder by 2', NULL);

 
 INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loan Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='Select Loan Account NO'),
  'Account');
  
  
   INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loan Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='startDateselect'),
  'ondate');
  
    INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loan Account Statement'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='endDateselect'),
  'todate');
  
  INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Loan Account Statement', 'Loan Account Statement', 'READ', 0);
