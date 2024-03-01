CREATE TABLE `domaene`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `kode`                 varchar(255) NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `domaene_kode_unique` (`kode`)
);

CREATE TABLE `linked_system`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `kode`                 varchar(255) NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    `domaene_id`           bigint(20) NOT NULL,
    `beskrivelse`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `system_kode_unique` (`kode`)
);

CREATE TABLE `arbejdsfunktion`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `linked_system_id`     bigint(20) NOT NULL,
    `kode`                 varchar(255) NOT NULL,
    `beskrivelse`          varchar(255) NOT NULL,
    `uddannelseskoder`     varchar(255) DEFAULT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                    `arbejdsfunktion_system_type` (`linked_system_id`),
    CONSTRAINT `arbejdsfunktion_system_type` FOREIGN KEY (`linked_system_id`) REFERENCES `linked_system` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `rettighed`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `linked_system_id`     bigint(20) NOT NULL,
    `kode`                 varchar(255) NOT NULL,
    `beskrivelse`          varchar(255) NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                    `rettighed_system_type` (`linked_system_id`),
    CONSTRAINT `rettighed_system_type` FOREIGN KEY (`linked_system_id`) REFERENCES `linked_system` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `delegerbar_rettighed`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `arbejdsfunktion_id`   bigint(20) NOT NULL,
    `rettighedskode_id`    bigint(20) NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    `delegerbar`           tinyint(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `delegerbar_rettighed_unique` (`arbejdsfunktion_id`,`rettighedskode_id`),
    KEY                    `delegerbar_rettighed_type` (`rettighedskode_id`),
    CONSTRAINT `delegerbar_arbejdsfunktion_type` FOREIGN KEY (`arbejdsfunktion_id`) REFERENCES `arbejdsfunktion` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `delegerbar_rettighed_type` FOREIGN KEY (`rettighedskode_id`) REFERENCES `rettighed` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `bemyndigelse20`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `kode`                 varchar(255) NOT NULL,
    `bemyndigende_cpr`     varchar(10)  NOT NULL,
    `bemyndigede_cpr`      varchar(10)  NOT NULL,
    `bemyndigede_cvr`      varchar(10)  DEFAULT NULL,
    `godkendelsesdato`     datetime     DEFAULT NULL,
    `gyldig_fra`           datetime     NOT NULL,
    `gyldig_til`           datetime     NOT NULL,
    `versionsid`           int(11) NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    `status`               enum('GODKENDT','ANMODET') NOT NULL DEFAULT 'ANMODET',
    `linked_system_kode`   varchar(255) NOT NULL,
    `arbejdsfunktion_kode` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY                    `ix_bemyndigende_cpr` (`bemyndigende_cpr`),
    KEY                    `ix_bemyndigede_cpr` (`bemyndigede_cpr`),
    KEY                    `ix_sidst_modificeret` (`sidst_modificeret`),
    KEY                    `ix_system_gyldig_til` (`linked_system_kode`,`gyldig_til`)
);

CREATE TABLE `bemyndigelse20_rettighed`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primærnøgle',
    `bemyndigelse20_id`    bigint(20) NOT NULL COMMENT '''Parent'', dvs. id på bemyndigelse',
    `rettighed_kode`       varchar(255) NOT NULL COMMENT 'Rettighed udtrykt ved rettighedens kode',
    `sidst_modificeret`    datetime     DEFAULT NULL COMMENT 'Tidspunkt for sidste modificering',
    `sidst_modificeret_af` varchar(255) DEFAULT NULL COMMENT 'Bruger, der sidst har modificeret',
    `kode`                 varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY                    `ix_bemyndigelse` (`bemyndigelse20_id`),
    CONSTRAINT `fk_bemyndigelse_id` FOREIGN KEY (`bemyndigelse20_id`) REFERENCES `bemyndigelse20` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `system_variable`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    `name`                 varchar(255) NOT NULL,
    `value`                varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `whitelist`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                 varchar(255) NOT NULL,
    `subject_id`           varchar(255) NOT NULL,
    `whitelist_type`       enum('SYSTEM_CVR','USER_CVR_CPR') NOT NULL,
    `sidst_modificeret`    datetime     DEFAULT NULL,
    `sidst_modificeret_af` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);
