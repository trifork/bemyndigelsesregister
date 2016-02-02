-- Beskrivelse på linked_system

ALTER TABLE linked_system ADD COLUMN beskrivelse VARCHAR(255);

UPDATE linked_system SET beskrivelse = kode;

ALTER TABLE linked_system MODIFY beskrivelse VARCHAR(255) NOT NULL;

-- Bemyndigelsesrettigheder

CREATE TABLE bemyndigelse20 (
  id bigint NOT NULL AUTO_INCREMENT,
  kode varchar(255) NOT NULL,
  bemyndigende_cpr varchar(10) NOT NULL,
  bemyndigede_cpr varchar(10) NOT NULL,
  bemyndigede_cvr varchar(10) DEFAULT NULL,
  godkendelsesdato datetime DEFAULT NULL,
  gyldig_fra datetime NOT NULL,
  gyldig_til datetime NOT NULL,
  versionsid int NOT NULL,
  sidst_modificeret datetime DEFAULT NULL,
  sidst_modificeret_af varchar(255) DEFAULT NULL,
  status enum('GODKENDT','BESTILT') NOT NULL DEFAULT 'BESTILT',
  linked_system_kode varchar(255) NOT NULL,
  arbejdsfunktion_kode varchar(255) NOT NULL,
  PRIMARY KEY (id)
) COMMENT = 'Bemyndigelse version 2.0';

CREATE TABLE bemyndigelse20_rettighed (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primærnøgle',
  bemyndigelse20_id BIGINT NOT NULL COMMENT '\'Parent\', dvs. id på bemyndigelse',
  rettighed_kode VARCHAR(255) NOT NULL COMMENT 'Rettighed udtrykt ved rettighedens kode',
  sidst_modificeret DATETIME NULL COMMENT 'Tidspunkt for sidste modificering',
  sidst_modificeret_af VARCHAR(255) NULL COMMENT 'Bruger, der sidst har modificeret',
  PRIMARY KEY (id),
  INDEX ix_bemyndigelse (bemyndigelse20_id ASC),
  CONSTRAINT fk_bemyndigelse_id
    FOREIGN KEY (bemyndigelse20_id)
    REFERENCES bemyndigelse20 (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Rettigheder for bemyndigelse	';

-- ALTER TABLE bemyndigelse DROP COLUMN rettighed_kode;
