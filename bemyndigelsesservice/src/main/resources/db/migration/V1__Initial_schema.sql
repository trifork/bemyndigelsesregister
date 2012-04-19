CREATE TABLE `domaene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `domaene` VARCHAR(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,

   PRIMARY KEY (`id`) 
);

CREATE TABLE `linked_system` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `system` VARCHAR(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,

   PRIMARY KEY (`id`) 
);

CREATE TABLE `status_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `status` VARCHAR(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  
  PRIMARY KEY (`id`) 
);

CREATE TABLE `arbejdsfunktion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `domaene_id` BIGINT NOT NULL,
  `linked_system_id` BIGINT NOT NULL,
  `arbejdsfunktion` VARCHAR(255) NOT NULL,
  `beskrivelse` VARCHAR(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  
  CONSTRAINT `arbejdsfunktion_domaene_type`
      FOREIGN KEY (`domaene_id` )
      REFERENCES `domaene` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `arbejdsfunktion_system_type`
      FOREIGN KEY (`linked_system_id` )
      REFERENCES `linked_system` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  PRIMARY KEY (`id`) 
);

CREATE TABLE `rettighed` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `domaene_id` BIGINT NOT NULL,
  `linked_system_id` BIGINT NOT NULL,
  `rettighedskode` VARCHAR(255) NOT NULL,
  `beskrivelse` VARCHAR(255) NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,

  CONSTRAINT `rettighed_domaene_type`
      FOREIGN KEY (`domaene_id` )
      REFERENCES `domaene` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `rettighed_system_type`
      FOREIGN KEY (`linked_system_id` )
      REFERENCES `linked_system` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
  
  PRIMARY KEY (`id`) 
);

CREATE TABLE `delegerbar_rettighed` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `domaene_id` BIGINT NOT NULL,
  `linked_system_id` BIGINT NOT NULL,
  `arbejdsfunktion` BIGINT NOT NULL,
  `rettighedskode` BIGINT NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  
  CONSTRAINT `delegerbar_rettighed_type`
      FOREIGN KEY (`rettighedskode` )
      REFERENCES `rettighed` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `delegerbar_arbejdsfunktion_type`
      FOREIGN KEY (`arbejdsfunktion` )
      REFERENCES `arbejdsfunktion` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `delegerbar_rettighed_domaene_type`
      FOREIGN KEY (`domaene_id` )
      REFERENCES `domaene` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `delegerbar_rettighed_system_type`
      FOREIGN KEY (`linked_system_id` )
      REFERENCES `linked_system` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  PRIMARY KEY (`id`) 
);

CREATE TABLE `bemyndigelse` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `kode` VARCHAR(255) NOT NULL,
  `bemyndigende_cpr` VARCHAR(10) NOT NULL,
  `bemyndigede_cpr` VARCHAR(10) NOT NULL,
  `bemyndigede_cvr` VARCHAR(10) NULL DEFAULT NULL,
  `linked_system_id` BIGINT NOT NULL,
  `arbejdsfunktion_id` BIGINT NOT NULL,
  `rettighed_id` BIGINT NOT NULL,
  `status_id` BIGINT NOT NULL,
  `godkendelsesdato` datetime DEFAULT NULL,
  `gyldig_fra` datetime NOT NULL,
  `gyldig_til` datetime NOT NULL,
  `versionsid` INT NOT NULL,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  
  PRIMARY KEY (`id`),
  
  CONSTRAINT `bemyndigelse_system_type`
      FOREIGN KEY (`linked_system_id` )
      REFERENCES `linked_system` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `rettighed_type`
      FOREIGN KEY (`rettighed_id` )
      REFERENCES `rettighed` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,

  CONSTRAINT `arbejdsfunktion_type`
      FOREIGN KEY (`arbejdsfunktion_id` )
      REFERENCES `arbejdsfunktion` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
      
  CONSTRAINT `status_type`
      FOREIGN KEY (`status_id` )
      REFERENCES `status_type` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
);

CREATE TABLE `message_retransmission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sidst_modificeret` datetime DEFAULT NULL,
  `sidst_modificeret_af` varchar(255) DEFAULT NULL,
  `message_id` varchar(255) NOT NULL,
  `message_response` BLOB NOT NULL,
  `implementation_build` varchar(255) NOT NULL,

  PRIMARY KEY(`id`)
);

