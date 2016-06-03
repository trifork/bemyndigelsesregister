-- roll back delegerbar flag til delegerbar_rettighed kolonne

ALTER TABLE bemyndigelse.delegerbar_rettighed DROP COLUMN delegerbar;
DELETE FROM bemyndigelse.schema_version WHERE version_rank=10;

