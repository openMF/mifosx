INSERT INTO `m_permission`
(`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`)
VALUES
('organisation', 'READ_DSA', 'DSA', 'READ', 0),
('organisation', 'CREATE_DSA', 'DSA', 'CREATE', 0),
('organisation', 'CREATE_DSA_CHECKER', 'DSA', 'CREATE', 0),
('organisation', 'UPDATE_DSA', 'DSA', 'UPDATE', 0),
('organisation', 'UPDATE_DSA_CHECKER', 'DSA', 'UPDATE_CHECKER', 0),
('organisation', 'DELETE_DSA', 'DSA', 'DELETE', 0),
('organisation', 'DELETE_DSA_CHECKER', 'DSA', 'DELETE_CHECKER', 0),
('portfolio', 'READ_CLIENTADDRESS', 'CLIENTADDRESS', 'READ', 0),
('portfolio', 'CREATE_CLIENTADDRESS', 'CLIENTADDRESS', 'CREATE', 0),
('portfolio', 'CREATE_CLIENTADDRESS_CHECKER', 'CLIENTADDRESS', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_CLIENTADDRESS', 'CLIENTADDRESS', 'UPDATE', 0),
('portfolio', 'UPDATE_CLIENTADDRESS_CHECKER', 'CLIENTADDRESS', 'UPDATE_CHECKER', 0),
('portfolio', 'DELETE_CLIENTADDRESS', 'CLIENTADDRESS', 'DELETE', 0),
('portfolio', 'DELETE_CLIENTADDRESS_CHECKER', 'CLIENTADDRESS', 'DELETE_CHECKER', 0),
('portfolio', 'CREATE_PAYMENTTOWHOM', 'PAYMENTTOWHOM', 'CREATE', 0),
('portfolio', 'UPDATE_PAYMENTTOWHOM', 'PAYMENTTOWHOM', 'UPDATE', 0),
('portfolio', 'DELETE_PAYMENTTOWHOM', 'PAYMENTTOWHOM', 'DELETE', 0),
('portfolio', 'READ_PAYMENTTOWHOM', 'PAYMENTTOWHOM', 'READ', 0);

