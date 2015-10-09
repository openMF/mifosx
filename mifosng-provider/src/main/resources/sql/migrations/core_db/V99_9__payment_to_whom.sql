CREATE TABLE `m_payment_to_whom` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_cash_payment` tinyint(1) DEFAULT '0',
  `order_position` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;