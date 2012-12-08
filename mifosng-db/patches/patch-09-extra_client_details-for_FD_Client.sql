drop table if exists extra_client_details;

CREATE TABLE `extra_client_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `client_dob` date DEFAULT NULL,
  `client_address` varchar(60) DEFAULT NULL,
  `father_name` varchar(40) DEFAULT NULL,
  `nominee` varchar(40) DEFAULT NULL,
  `nominee_relationship` varchar(40) DEFAULT NULL,
  `nominee_address` varchar(60) DEFAULT NULL,
  `crime_no` varchar(40) DEFAULT NULL,
  `police_station` varchar(40) DEFAULT NULL,
  `other_notes` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `client_id` (`client_id`),
  KEY `FK_extra_client_details_1` (`client_id`),
  CONSTRAINT `FK_extra_client_details` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;