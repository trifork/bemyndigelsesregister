ALTER TABLE bemyndigelse ADD COLUMN linked_system_kode VARCHAR(255);
ALTER TABLE bemyndigelse ADD COLUMN arbejdsfunktion_kode VARCHAR(255);
ALTER TABLE bemyndigelse ADD COLUMN rettighed_kode VARCHAR(255);

UPDATE bemyndigelse SET linked_system_kode = (SELECT kode FROM linked_system WHERE id = linked_system_id);
UPDATE bemyndigelse SET arbejdsfunktion_kode = (SELECT kode FROM arbejdsfunktion WHERE id = arbejdsfunktion_id);
UPDATE bemyndigelse SET rettighed_kode = (SELECT kode FROM rettighed WHERE id = rettighed_id);

ALTER TABLE  bemyndigelse MODIFY linked_system_kode VARCHAR(255) NOT NULL;
ALTER TABLE  bemyndigelse MODIFY arbejdsfunktion_kode VARCHAR(255) NOT NULL;
ALTER TABLE  bemyndigelse MODIFY rettighed_kode VARCHAR(255) NOT NULL;

ALTER TABLE bemyndigelse DROP FOREIGN KEY arbejdsfunktion_type;
ALTER TABLE bemyndigelse DROP FOREIGN KEY bemyndigelse_system_type;
ALTER TABLE bemyndigelse DROP FOREIGN KEY rettighed_type;
ALTER TABLE bemyndigelse DROP COLUMN linked_system_id;
ALTER TABLE bemyndigelse DROP COLUMN arbejdsfunktion_id;
ALTER TABLE bemyndigelse DROP COLUMN rettighed_id;