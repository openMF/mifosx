/*
this scripts adding new column 'event_start_time' to m_calendar table
*/

ALTER TABLE `m_calendar` ADD `event_start_time` SMALLINT(4) AFTER `end_date`;
