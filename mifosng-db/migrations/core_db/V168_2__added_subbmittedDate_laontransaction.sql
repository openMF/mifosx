ALTER TABLE `m_loan_transaction` ADD `submitted_on_date` DATE NOT NULL;

UPDATE `m_loan_transaction` SET `submitted_on_date`= `transaction_date` WHERE id IN (SELECT id FROM ( SELECT `id` FROM `m_loan_transaction` )as id);
