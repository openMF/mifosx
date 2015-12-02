Alter table sms_messages_outbound
Add column campaign_name varchar(200);


INSERT INTO  `m_permission` (
`id` ,
`grouping` ,
`code` ,
`entity_name` ,
`action_name` ,
`can_maker_checker`
)
VALUES (
'0',  'organisation',  'REACTIVATE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'REACTIVATE',  '0'
);
