CREATE TABLE `bemyndigelse20_rettighed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primærnøgle',
  `bemyndigelse20_id` bigint(20) NOT NULL COMMENT 'Parent, dvs. id på bemyndigelse',
  `rettighed_kode` varchar(255) NOT NULL COMMENT 'Rettighed udtrykt ved rettighedens kode',
  `sidst_modificeret` datetime DEFAULT NULL COMMENT 'Tidspunkt for sidste modificering',
  `sidst_modificeret_af` varchar(255) DEFAULT NULL COMMENT 'Bruger, der sidst har modificeret',
  `kode` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_bemyndigelse` (`bemyndigelse20_id`),
  CONSTRAINT `fk_bemyndigelse_id` FOREIGN KEY (`bemyndigelse20_id`) REFERENCES `bemyndigelse20` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Rettigheder for bemyndigelse'
