SET FOREIGN_KEY_CHECKS = 0;

REPLACE INTO domaene(kode) VALUES ('trifork-test');

REPLACE INTO linked_system(domaene_id, kode) VALUES (1, 'Trifork test system');

REPLACE INTO arbejdsfunktion(kode, beskrivelse, linked_system_id) values ('Laege', 'For unit test only', 1);
REPLACE INTO rettighed(kode, beskrivelse, linked_system_id) VALUES ('R01', 'Laegemiddelordination', 1);
REPLACE INTO delegerbar_rettighed(arbejdsfunktion_id, rettighedskode_id) VALUES (1, 1);

##Bemyndigelser
REPLACE INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status`,
        `godkendelsesdato`,
        `gyldig_fra`,
        `gyldig_til`,
        `versionsid`,
        `sidst_modificeret`,
        `sidst_modificeret_af`)
  VALUES (
        'TestKode1',
        '1010101010',
        '1010101012',
        '1',
        1,
        1,
        1,
        'GODKENDT',
        '2011-05-21 02:15:00',
        '2011-05-21 23:59:59',
        '2011-05-21 02:15:00',
        0,
        '2011-05-21 02:15:00',
        NULL);

REPLACE INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status`,
        `godkendelsesdato`,
        `gyldig_fra`,
        `gyldig_til`,
        `versionsid`,
        `sidst_modificeret`,
        `sidst_modificeret_af`)
  VALUES (
        'TestKode2',
        '1010101010',
        '1010101012',
        '1',
        1,
        1,
        1,
        'GODKENDT',
        '2011-05-22 02:15:00',
        '2011-05-22 02:15:00',
        '2011-05-22 02:15:00',
        0,
        '2011-05-22 02:15:00',
        NULL);

REPLACE INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status`,
        `godkendelsesdato`,
        `gyldig_fra`,
        `gyldig_til`,
        `versionsid`,
        `sidst_modificeret`,
        `sidst_modificeret_af`)
  VALUES (
        'TestKode3',
        '1010101012',
        '1010101013',
        '1',
        1,
        1,
        1,
        'GODKENDT',
        '2000-05-22 00:00:00',
        '2000-05-22 23:59:59',
        '2000-05-22 00:00:00',
        0,
        '2000-05-22 02:15:00',
        NULL);

REPLACE INTO `system_variable` (`name`, `value`) VALUES ('testVariable', 'den gode test');

REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'test', '1');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'test', '2');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('USER_CVR_CPR', 'test', 'CVR:1-CPR:2');

/* Testdata for integration test */
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.indlaesMetadata', '25520041');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.HentMetadata', '25520041');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', '', '25520041');

SET FOREIGN_KEY_CHECKS = 1;