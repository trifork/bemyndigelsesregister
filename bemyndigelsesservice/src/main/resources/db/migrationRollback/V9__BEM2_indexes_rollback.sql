ALTER TABLE `bemyndigelse`.`bemyndigelse20`
DROP INDEX `ix_bemyndigende_cpr`,
DROP INDEX `ix_bemyndigede_cpr`,
DROP INDEX `ix_sidst_modificeret`,
DROP INDEX `ix_system_gyldig_til`;

DELETE FROM `bemyndigelse`.`schema_version` WHERE `version_rank`=9;