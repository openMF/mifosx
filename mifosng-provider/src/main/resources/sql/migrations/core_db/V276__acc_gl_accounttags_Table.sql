
CREATE TABLE `acc_gl_accounttags` (
	`gl_account_id` BIGINT NOT NULL,
	`tag_id` INT NOT NULL,
	PRIMARY KEY (`gl_account_id`, `tag_id`),
	CONSTRAINT `FK__m_code_value` FOREIGN KEY (`tag_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK__acc_gl_account` FOREIGN KEY (`gl_account_id`) REFERENCES `acc_gl_account` (`id`)
);

insert into acc_gl_accounttags (gl_account_id , tag_id) select ag.id, ag.tag_id
from acc_gl_account ag where ag.tag_id is not null;


ALTER TABLE `acc_gl_account`
 DROP COLUMN `tag_id`,
 DROP INDEX `FKGLACC000000002`,
 DROP FOREIGN KEY `FKGLACC000000002`;