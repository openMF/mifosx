CREATE TABLE `m_product_loan_configurable_attributes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `loan_product_id` BIGINT NOT NULL,
  `amortization` TINYINT NOT NULL DEFAULT '1',
  `interest_method` TINYINT NOT NULL DEFAULT '1',
  `repayment_strategy` TINYINT NOT NULL DEFAULT '1',
  `interest_calculation_period` TINYINT NOT NULL DEFAULT '1',
  `arrears_tolerance` TINYINT NOT NULL DEFAULT '1',
  `repay_every` TINYINT NOT NULL DEFAULT '1',
  `moratorium` TINYINT NOT NULL DEFAULT '1',
  `grace_on_arrears_aging` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_m_product_loan_configurable_attributes_0001` (`loan_product_id`),
  CONSTRAINT `fk_m_product_loan_configurable_attributes_0001` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


 INSERT into `m_product_loan_configurable_attributes` 
(loan_product_id,amortization,interest_method,repayment_strategy,
interest_calculation_period,arrears_tolerance,repay_every,moratorium,grace_on_arrears_aging)
(select pl.id,'1','1','1','1','1','1','1','1' from `m_product_loan` pl where pl.id NOT IN (select loan_product_id from `m_product_loan_configurable_attributes`));