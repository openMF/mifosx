INSERT INTO c_configuration (name,enabled) VALUES ('amazon-S3',0);


CREATE TABLE IF NOT EXISTS m_s3_details (
	name VARCHAR(150) NOT NULL,
	value VARCHAR(250),
	UNIQUE(name) 
);

INSERT INTO m_s3_details (name) VALUES ('cdn_url');
INSERT INTO m_s3_details (name) VALUES ('bucket_name');
INSERT INTO m_s3_details (name) VALUES ('access_key');
INSERT INTO m_s3_details (name) VALUES ('secret_key');

ALTER TABLE m_document ADD COLUMN storage_location varchar(50);
