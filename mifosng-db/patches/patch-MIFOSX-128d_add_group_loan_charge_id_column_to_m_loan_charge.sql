ALTER TABLE m_loan_charge add column group_loan_charge_id bigint(20) DEFAULT NULL AFTER charge_id,
ADD CONSTRAINT FOREIGN KEY(group_loan_charge_id) references m_group_loan_charge(id);
