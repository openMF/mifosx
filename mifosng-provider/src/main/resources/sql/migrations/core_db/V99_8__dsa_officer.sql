ALTER TABLE `m_loan`
	ADD INDEX `m_loan_m_dsa` (`dsa_officer_id`),
	ADD CONSTRAINT `m_loan_m_dsa` FOREIGN KEY (`dsa_officer_id`) REFERENCES `m_dsa` (`id`);