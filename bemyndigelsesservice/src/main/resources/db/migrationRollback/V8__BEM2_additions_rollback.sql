DROP TABLE bemyndigelse.bemyndigelse20_rettighed;
DROP TABLE bemyndigelse.bemyndigelse20;
ALTER TABLE bemyndigelse.linked_system DROP COLUMN beskrivelse;

DELETE FROM bemyndigelse.schema_version WHERE version=8;
