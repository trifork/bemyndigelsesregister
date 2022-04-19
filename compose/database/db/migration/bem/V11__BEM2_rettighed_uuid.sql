-- uuid-kolonne i rettighedstabel

ALTER TABLE bemyndigelse20_rettighed ADD COLUMN kode VARCHAR(255);

UPDATE bemyndigelse20_rettighed SET kode = CONVERT(id, char);

ALTER TABLE bemyndigelse20_rettighed MODIFY kode VARCHAR(255) NOT NULL;
