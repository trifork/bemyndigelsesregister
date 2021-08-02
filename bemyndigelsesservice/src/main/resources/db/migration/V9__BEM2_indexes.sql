ALTER TABLE bemyndigelse20
ADD INDEX ix_bemyndigende_cpr (bemyndigende_cpr ASC),
ADD INDEX ix_bemyndigede_cpr (bemyndigede_cpr ASC),
ADD INDEX ix_sidst_modificeret (sidst_modificeret ASC),
ADD INDEX ix_system_gyldig_til (linked_system_kode ASC, gyldig_til ASC);
