SET FOREIGN_KEY_CHECKS = 0;

REPLACE INTO domaene(kode) VALUES ('trifork-test');

REPLACE INTO linked_system(domaene_id, kode, beskrivelse) VALUES (1, 'testsys', 'Trifork test system');

REPLACE INTO arbejdsfunktion(kode, beskrivelse, linked_system_id) values ('Laege', 'For unit test only', 1);

REPLACE INTO rettighed(kode, beskrivelse, linked_system_id) VALUES ('R01', 'Laegemiddelordination', 1);
REPLACE INTO rettighed(kode, beskrivelse, linked_system_id) VALUES ('R02', 'Testrettighed2', 1);
REPLACE INTO rettighed(kode, beskrivelse, linked_system_id) VALUES ('*', 'Alle nuværende og fremtidige rettigheder', 1);

REPLACE INTO delegerbar_rettighed(arbejdsfunktion_id, rettighedskode_id) VALUES (1, 1);
REPLACE INTO delegerbar_rettighed(arbejdsfunktion_id, rettighedskode_id) VALUES (1, 2);
REPLACE INTO delegerbar_rettighed(arbejdsfunktion_id, rettighedskode_id) VALUES (1, 3);

##Bemyndigelser
REPLACE INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `linked_system_kode`,
        `arbejdsfunktion_id`,
        `arbejdsfunktion_kode`,
        `rettighed_id`,
        `rettighed_kode`,
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
        'testsys',
        1,
        'kode',
        1,
        'kode',
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
        `linked_system_kode`,
        `arbejdsfunktion_id`,
        `arbejdsfunktion_kode`,
        `rettighed_id`,
        `rettighed_kode`,
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
        'testsys',
        1,
        'kode',
        1,
        'kode',
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
        `linked_system_kode`,
        `arbejdsfunktion_id`,
        `arbejdsfunktion_kode`,
        `rettighed_id`,
        `rettighed_kode`,
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
        'testsys',
        1,
        'kode',
        1,
        'kode',
        'GODKENDT',
        '2000-05-22 00:00:00',
        '2000-05-22 23:59:59',
        '2000-05-22 00:00:00',
        0,
        '2000-05-22 02:15:00',
        NULL);

REPLACE INTO `bemyndigelse20` (
  `id`,
  `kode`,
  `bemyndigende_cpr`,
  `bemyndigede_cpr`,
  `bemyndigede_cvr`,
  `linked_system_kode`,
  `arbejdsfunktion_kode`,
  `status`,
  `godkendelsesdato`,
  `gyldig_fra`,
  `gyldig_til`,
  `versionsid`,
  `sidst_modificeret`,
  `sidst_modificeret_af`)
VALUES (
  1,
  'TestKode1',
  '1010101010',
  '1010101012',
  '1',
  'testsys',
  'Laege',
  'GODKENDT',
  '2011-05-21 02:15:00',
  '2011-05-21 23:59:59',
  '2011-05-21 02:15:00',
  0,
  '2011-05-21 02:15:00',
  NULL);

REPLACE INTO `bemyndigelse20` (
  `id`,
  `kode`,
  `bemyndigende_cpr`,
  `bemyndigede_cpr`,
  `bemyndigede_cvr`,
  `linked_system_kode`,
  `arbejdsfunktion_kode`,
  `status`,
  `godkendelsesdato`,
  `gyldig_fra`,
  `gyldig_til`,
  `versionsid`,
  `sidst_modificeret`,
  `sidst_modificeret_af`)
VALUES (
  2,
  'TestKode2',
  '1010101010',
  '1010101012',
  '1',
  'testsys',
  'Laege',
  'GODKENDT',
  '2011-05-22 02:15:00',
  '2011-05-22 02:15:00',
  '2011-05-22 02:15:00',
  0,
  '2011-05-22 02:15:00',
  NULL);

REPLACE INTO `bemyndigelse20` (
  `id`,
  `kode`,
  `bemyndigende_cpr`,
  `bemyndigede_cpr`,
  `bemyndigede_cvr`,
  `linked_system_kode`,
  `arbejdsfunktion_kode`,
  `status`,
  `godkendelsesdato`,
  `gyldig_fra`,
  `gyldig_til`,
  `versionsid`,
  `sidst_modificeret`,
  `sidst_modificeret_af`)
VALUES (
  3,
  'TestKode3',
  '1010101012',
  '1010101013',
  '1',
  'testsys',
  'Laege',
  'GODKENDT',
  '2000-05-22 00:00:00',
  '2000-05-22 23:59:59',
  '2000-05-22 00:00:00',
  0,
  '2000-05-22 02:15:00',
  NULL);

REPLACE INTO `bemyndigelse20` (
  `id`,
  `kode`,
  `bemyndigende_cpr`,
  `bemyndigede_cpr`,
  `bemyndigede_cvr`,
  `linked_system_kode`,
  `arbejdsfunktion_kode`,
  `status`,
  `godkendelsesdato`,
  `gyldig_fra`,
  `gyldig_til`,
  `versionsid`,
  `sidst_modificeret`,
  `sidst_modificeret_af`)
VALUES (
  4,
  'TestKode4',
  '2006271866',
  '1010101014',
  '1',
  'testsys',
  'Laege',
  'GODKENDT',
  '2000-05-22 00:00:00',
  '2000-05-22 23:59:59',
  '2100-05-22 00:00:00',
  0,
  '2000-05-22 02:15:00',
  NULL);

REPLACE INTO `bemyndigelse20` (
  `id`,
  `kode`,
  `bemyndigende_cpr`,
  `bemyndigede_cpr`,
  `bemyndigede_cvr`,
  `linked_system_kode`,
  `arbejdsfunktion_kode`,
  `status`,
  `godkendelsesdato`,
  `gyldig_fra`,
  `gyldig_til`,
  `versionsid`,
  `sidst_modificeret`,
  `sidst_modificeret_af`)
VALUES (
  5,
  'TestKode5',
  '1010101015',
  '2006271866',
  '1',
  'testsys',
  'Laege',
  'GODKENDT',
  '2000-05-22 00:00:00',
  '2000-05-22 23:59:59',
  '2100-05-22 00:00:00',
  0,
  '2000-05-22 02:15:00',
  NULL);

REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (1, 'R01', 'TestKode6');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (2, 'R01', 'TestKode7');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (3, 'R01', 'TestKode8');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (4, 'R01', 'TestKode9');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (4, 'R02', 'TestKode10');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (5, 'R01', 'TestKode11');
REPLACE INTO `bemyndigelse20_rettighed` (`bemyndigelse20_id`, `rettighed_kode`, `kode`) VALUES (5, 'R02', 'TestKode12');

REPLACE INTO `system_variable` (`name`, `value`) VALUES ('testVariable', 'den gode test');

REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'test', '1');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'test', '2');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('USER_CVR_CPR', 'test', 'CVR:1-CPR:2');

/* Testdata for integration test */
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.indlaesMetadata', '25520041');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.hentMetadata', '25520041');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', '', '25520041');

/* Testdata for bemyndigelse-indlaes-tool */
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.indlaesMetadata', '20921897');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', 'bemyndigelsesservice.hentMetadata', '20921897');
REPLACE INTO `whitelist` (whitelist_type, `name`, `subject_id`) VALUES ('SYSTEM_CVR', '', '20921897');

SET FOREIGN_KEY_CHECKS = 1;