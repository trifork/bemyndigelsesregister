DROP TABLE bemyndigelse20_rettighed;
DROP TABLE bemyndigelse20;
ALTER TABLE linked_system DROP COLUMN beskrivelse;

DELETE FROM schema_version WHERE version=8;
