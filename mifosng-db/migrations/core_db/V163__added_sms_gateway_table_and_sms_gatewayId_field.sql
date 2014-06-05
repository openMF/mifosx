DROP TABLE IF EXISTS `m_sms_gateway`;
CREATE TABLE `m_sms_gateway` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gateway_name` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `auth_token` varchar(333) CHARACTER SET utf8 DEFAULT NULL,
  `url` varchar(333) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

-- Permissions for CRUD on SMS gateway
DELETE FROM `m_permission` WHERE `entity_name`='SMSGATEWAY';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'READ_SMSGATEWAY', 'SMSGATEWAY', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'CREATE_SMSGATEWAY', 'SMSGATEWAY', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'CREATE_SMSGATEWAY_CHECKER', 'SMSGATEWAY', 'CREATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'UPDATE_SMSGATEWAY', 'SMSGATEWAY', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'UPDATE_SMSGATEWAY_CHECKER', 'SMSGATEWAY', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'DELETE_SMSGATEWAY', 'SMSGATEWAY', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'DELETE_SMSGATEWAY_CHECKER', 'SMSGATEWAY', 'DELETE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('organisation', 'TEST_SMSGATEWAY', 'SMSGATEWAY', 'TEST', 0);

-- Adding gatewayId field to sms_messages_outbound
ALTER TABLE `sms_messages_outbound`
  ADD `gatewayId` int(20) DEFAULT NULL;