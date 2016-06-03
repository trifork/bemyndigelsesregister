-- ALTER TABLE bemyndigelse.bemyndigelse ADD COLUMN rettighed_id  BIGINT;
-- ALTER TABLE bemyndigelse.bemyndigelse ADD COLUMN arbejdsfunktion_id BIGINT;
-- ALTER TABLE bemyndigelse.bemyndigelse ADD COLUMN linked_system_id BIGINT;

UPDATE bemyndigelse.bemyndigelse SET linked_system_id = (SELECT id FROM bemyndigelse.linked_system WHERE kode = linked_system_kode);
UPDATE bemyndigelse.bemyndigelse AS b SET arbejdsfunktion_id = (SELECT id FROM bemyndigelse.arbejdsfunktion AS a WHERE kode = arbejdsfunktion_kode AND a.linked_system_id=b.linked_system_id);
UPDATE bemyndigelse.bemyndigelse AS b SET rettighed_id = (SELECT id FROM bemyndigelse.rettighed AS a WHERE kode = rettighed_kode AND a.linked_system_id=b.linked_system_id);

ALTER TABLE bemyndigelse.bemyndigelse MODIFY linked_system_id BIGINT NOT NULL;
ALTER TABLE bemyndigelse.bemyndigelse MODIFY arbejdsfunktion_id BIGINT NOT NULL;
ALTER TABLE bemyndigelse.bemyndigelse MODIFY rettighed_id BIGINT NOT NULL;

ALTER TABLE bemyndigelse.bemyndigelse
  ADD CONSTRAINT `rettighed_type`
      FOREIGN KEY (`rettighed_id` )
      REFERENCES `rettighed` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION;
ALTER TABLE bemyndigelse.bemyndigelse
  ADD CONSTRAINT `arbejdsfunktion_type`
      FOREIGN KEY (`arbejdsfunktion_id` )
      REFERENCES `arbejdsfunktion` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION;
ALTER TABLE bemyndigelse.bemyndigelse
  ADD CONSTRAINT `bemyndigelse_system_type`
      FOREIGN KEY (`linked_system_id` )
      REFERENCES `linked_system` (`id` )
      ON DELETE NO ACTION
      ON UPDATE NO ACTION;

ALTER TABLE bemyndigelse.bemyndigelse DROP COLUMN linked_system_kode;
ALTER TABLE bemyndigelse.bemyndigelse DROP COLUMN arbejdsfunktion_kode;
ALTER TABLE bemyndigelse.bemyndigelse DROP COLUMN rettighed_kode;


DELETE FROM bemyndigelse.schema_version WHERE version=7;

