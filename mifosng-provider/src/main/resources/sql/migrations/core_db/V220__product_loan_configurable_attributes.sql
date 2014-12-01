CREATE TABLE `m_product_loan_configurable_attributes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `loan_product_id` BIGINT NOT NULL,
  `allow_attribute_configuration` TINYINT NOT NULL DEFAULT '0',
  `amortization` TINYINT NOT NULL DEFAULT '0',
  `interest_method` TINYINT NOT NULL DEFAULT '0',
  `repayment_strategy` TINYINT NOT NULL DEFAULT '0',
  `interest_calculation_period` TINYINT NOT NULL DEFAULT '0',
  `arrears_tolerance` TINYINT NOT NULL DEFAULT '0',
  `repay_every` TINYINT NOT NULL DEFAULT '0',
  `moratorium` TINYINT NOT NULL DEFAULT '0',
  `grace_on_arrears_aging` TINYINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_m_product_loan_configurable_attributes_0001` (`loan_product_id`),
  CONSTRAINT `fk_m_product_loan_configurable_attributes_0001` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;