-- uuid-kolonne i rettighedstabel

ALTER TABLE bemyndigelse.bemyndigelse20_rettighed DROP COLUMN kode;

DELETE FROM bemyndigelse.schema_version WHERE version_rank=11;
