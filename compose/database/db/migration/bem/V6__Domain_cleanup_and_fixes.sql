ALTER TABLE linked_system ADD COLUMN domaene_id bigint(20) NOT NULL default 1;
ALTER TABLE linked_system ALTER COLUMN domaene_id DROP DEFAULT;
ALTER TABLE linked_system ADD CONSTRAINT kode_unique UNIQUE (kode);
ALTER TABLE domaene ADD CONSTRAINT kode_unique UNIQUE (kode);

ALTER TABLE rettighed DROP FOREIGN KEY rettighed_domaene_type;
ALTER TABLE rettighed DROP KEY rettighed_domaene_type;
ALTER TABLE rettighed DROP COLUMN domaene_id;

ALTER TABLE arbejdsfunktion DROP FOREIGN KEY arbejdsfunktion_domaene_type;
ALTER TABLE arbejdsfunktion DROP KEY arbejdsfunktion_domaene_type;
ALTER TABLE arbejdsfunktion DROP COLUMN domaene_id;

ALTER TABLE delegerbar_rettighed DROP FOREIGN KEY delegerbar_rettighed_domaene_type;
ALTER TABLE delegerbar_rettighed DROP KEY delegerbar_rettighed_domaene_type;
ALTER TABLE delegerbar_rettighed DROP COLUMN domaene_id;

ALTER TABLE delegerbar_rettighed DROP FOREIGN KEY delegerbar_rettighed_system_type;
ALTER TABLE delegerbar_rettighed DROP KEY delegerbar_rettighed_system_type;
ALTER TABLE delegerbar_rettighed DROP COLUMN linked_system_id;

ALTER TABLE delegerbar_rettighed DROP COLUMN kode;