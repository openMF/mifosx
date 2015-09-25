INSERT INTO `m_role`
(`name`,`description`,`is_disabled`)
VALUES
('Level 1','client data entry,loan application,transactions,loan collection, repayment, interface between NBFC and client',0),
('Verification','verification of data entry by level 1',0),
('Approval','Level 2 approval of case by going through client and loan application and verification reports',0),
('Disbursal authority','disbursal department',0),
('Auditing and reporting','for audit purposes both internal as well as external',0),
('Reporting','Reporting for sales purposes',0),
('Operations','admin tasks such as holidays, officials etc, loan products configurations etc.',0),
('Disbursal and reporting','disbursal and reports',0),
('Accounting and Reporting','accounting and reporting',0);
