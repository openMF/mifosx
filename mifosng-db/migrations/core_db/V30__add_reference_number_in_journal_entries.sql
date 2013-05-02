ALTER TABLE `acc_gl_journal_entry` 
ADD COLUMN `ref_number` varchar(100) NULL DEFAULT NULL AFTER `lastmodified_date`;