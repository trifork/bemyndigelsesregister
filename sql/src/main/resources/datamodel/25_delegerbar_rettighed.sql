CREATE TABLE `delegerbar_rettighed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arbejdsfunktion_id` bigint(20) NOT NULL,
  `rettighedskode_id` bigint(20) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  `delegerbar` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `delegerbar_rettighed_unique` (`arbejdsfunktion_id`,`rettighedskode_id`),
  KEY `delegerbar_rettighed_type` (`rettighedskode_id`),
  CONSTRAINT `delegerbar_arbejdsfunktion_type` FOREIGN KEY (`arbejdsfunktion_id`) REFERENCES `arbejdsfunktion` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `delegerbar_rettighed_type` FOREIGN KEY (`rettighedskode_id`) REFERENCES `rettighed` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB
