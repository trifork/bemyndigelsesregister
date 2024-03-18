CREATE TABLE `whitelist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `subject_id` varchar(255) NOT NULL,
  `whitelist_type` enum('SYSTEM_CVR','USER_CVR_CPR') NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
