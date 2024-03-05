CREATE TABLE `domaene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `kode_unique` (`kode`)
) ENGINE=InnoDB
