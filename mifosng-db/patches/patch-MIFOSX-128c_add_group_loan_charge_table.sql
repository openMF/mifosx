DROP TABLE IF EXISTS `m_group_loan_charge`;
CREATE TABLE `m_group_loan_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_loan_id` bigint(20) NOT NULL,
  `charge_id` bigint(20) NOT NULL,
  `is_penalty` tinyint(1) NOT NULL DEFAULT 0,
  `charge_time_enum` smallint(5) NOT NULL,
  `due_for_collection_as_of_date` date DEFAULT NULL,
  `is_paid_derived` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY (`charge_id`),
  KEY (`group_loan_id`),
  FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
  FOREIGN KEY (`group_loan_id`) REFERENCES `m_group_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8