ALTER TABLE `m_loan_collateral` 
   ADD COLUMN `gold_fine_cv_id` int(20) DEFAULT NULL,
   ADD COLUMN `jewellery_cv_id` int(11) DEFAULT NULL,
   ADD COLUMN `two_cv_id` int(11) DEFAULT NULL,
   ADD COLUMN `actualcost` decimal(19,2) DEFAULT NULL,
   ADD COLUMN `gross` decimal(19,2) DEFAULT NULL,
   ADD COLUMN `impurity` decimal(19,2) DEFAULT NULL,
   ADD COLUMN `net` decimal(19,2) DEFAULT NULL,
   ADD COLUMN `stone` decimal(19,2) DEFAULT NULL;