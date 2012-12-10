ALTER TABLE m_loan add column group_loan_id bigint(20) DEFAULT NULL AFTER group_id,
ADD CONSTRAINT FOREIGN KEY(group_loan_id) references m_group_loan(id);