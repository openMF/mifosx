CREATE TABLE `m_oauth_access_token` (
    `token` char(36) NOT NULL,
    `client_id` varchar(100) NOT NULL,
    `expiration_time` bigint(20) DEFAULT NULL,
    `authentication_id` char(32) NOT NULL,
    UNIQUE KEY `FK_m_oauth_access_token_token` (`token`),
    UNIQUE KEY `FK_m_oauth_access_token_authentication_id` (`authentication_id`),
    KEY `FK_m_oauth_access_token_m_appuser` (`client_id`),
    CONSTRAINT `FK_m_oauth_access_token_m_appuser` FOREIGN KEY (`client_id`) REFERENCES `m_appuser` (`username`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;