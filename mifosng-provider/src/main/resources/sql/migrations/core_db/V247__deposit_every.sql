ALTER TABLE `m_deposit_product_recurring_detail` 
ADD COLUMN `deposit_every` BIGINT(20) NULL DEFAULT NULL,
ADD COLUMN `deposit_every_type_enum` BIGINT(20) NULL DEFAULT NULL;