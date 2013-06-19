

ALTER TABLE `m_loan`
	ADD COLUMN `sync_disbursement_with_meeting` TINYINT(1) NULL AFTER `loan_transaction_strategy_id`,
	ADD COLUMN `sync_repayments_with_meeting` TINYINT(1) NULL AFTER `sync_disbursement_with_meeting`;