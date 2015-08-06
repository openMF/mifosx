ALTER TABLE `m_client` 
	ADD COLUMN `fathername` varchar(100) DEFAULT NULL,
 	ADD COLUMN `emailaddress` varchar(50) DEFAULT NULL,
  	ADD COLUMN `code` varchar(50) DEFAULT NULL,
  	ADD COLUMN `password` varchar(15) DEFAULT NULL,
  	ADD COLUMN `marital_cv_id` int(11) DEFAULT NULL,
  	ADD COLUMN `religion_cv_id` int(11) DEFAULT NULL,
  	ADD COLUMN `dependent_cv_id` int(11) DEFAULT NULL,
  	ADD COLUMN `education_cv_id` int(11) DEFAULT NULL;
