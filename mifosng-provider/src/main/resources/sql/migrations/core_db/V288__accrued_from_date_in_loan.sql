ALTER TABLE `m_loan`
  ADD COLUMN `accrued_from` DATE NULL DEFAULT NULL AFTER `version`; 