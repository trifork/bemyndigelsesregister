ALTER TABLE whitelist ADD COLUMN whitelist_type ENUM('SYSTEM_CVR', 'USER_CVR_CPR') NOT NULL DEFAULT 'SYSTEM_CVR';
ALTER TABLE whitelist ALTER COLUMN whitelist_type DROP DEFAULT;
ALTER TABLE whitelist CHANGE legal_cvr subject_id varchar(255) NOT NULL;

ALTER TABLE whitelist ADD COLUMN  `sidst_modificeret` datetime DEFAULT NULL;
ALTER TABLE whitelist ADD COLUMN  `sidst_modificeret_af` varchar(255) DEFAULT NULL;
