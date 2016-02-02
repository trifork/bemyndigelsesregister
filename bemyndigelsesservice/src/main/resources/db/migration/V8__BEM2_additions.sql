-- Beskrivelse på linked_system

ALTER TABLE linked_system ADD COLUMN beskrivelse VARCHAR(255);

UPDATE linked_system SET beskrivelse = kode;

ALTER TABLE linked_system MODIFY beskrivelse VARCHAR(255) NOT NULL;

-- Bemyndigelsesrettigheder

CREATE TABLE bemyndigelse_rettighed (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primærnøgle',
  bemyndigelse_id BIGINT NOT NULL COMMENT '\'Parent\', dvs. id på bemyndigelse',
  rettighed_kode VARCHAR(255) NOT NULL COMMENT 'Rettighed udtrykt ved rettighedens kode',
  sidst_modificeret DATETIME NULL COMMENT 'Tidspunkt for sidste modificering',
  sidst_modificeret_af VARCHAR(255) NULL COMMENT 'Bruger, der sidst har modificeret',
  PRIMARY KEY (id),
  INDEX ix_bemyndigelse (bemyndigelse_id ASC),
  CONSTRAINT `fk_bemyndigelse_id`
    FOREIGN KEY (bemyndigelse_id)
    REFERENCES bemyndigelse (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Rettigheder for bemyndigelse	';

-- ALTER TABLE bemyndigelse DROP COLUMN rettighed_kode;
