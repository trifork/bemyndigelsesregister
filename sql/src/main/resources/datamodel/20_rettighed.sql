CREATE TABLE `rettighed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linked_system_id` bigint(20) NOT NULL,
  `kode` varchar(255) NOT NULL,
  `beskrivelse` varchar(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rettighed_system_type` (`linked_system_id`),
  CONSTRAINT `rettighed_system_type` FOREIGN KEY (`linked_system_id`) REFERENCES `linked_system` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1
