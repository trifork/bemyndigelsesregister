INSERT INTO status_type (kode, sidst_modificeret, sidst_modificeret_af) VALUES ('OK', NOW(), 'mwl');

INSERT INTO linked_system(kode) VALUES ('Trifork test system');

INSERT INTO domaene(kode) VALUES ('trifork-test');

INSERT INTO arbejdsfunktion(kode, domaene_id, beskrivelse, linked_system_id) values ('Laege', 1, 'For unit test only', 1);
INSERT INTO rettighed(kode, beskrivelse, domaene_id, linked_system_id) VALUES ('R01', 'Laegemiddelordination', 1, 1);
INSERT INTO delegerbar_rettighed(kode, arbejdsfunktion_id, domaene_id, linked_system_id, rettighedskode_id) VALUES ('DR01', 1, 1, 1, 1);

##Bemyndigelser
INSERT INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status_id`,
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
        1,
        '2011-05-21 02:15:00',
        '2011-05-21 23:59:59',
        '2011-05-21 02:15:00',
        0,
        '2011-05-21 02:15:00',
        NULL);

INSERT INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status_id`,
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
        1,
        '2011-05-22 02:15:00',
        '2011-05-22 02:15:00',
        '2011-05-22 02:15:00',
        0,
        '2011-05-22 02:15:00',
        NULL);

INSERT INTO `bemyndigelse` (
        `kode`,
        `bemyndigende_cpr`,
        `bemyndigede_cpr`,
        `bemyndigede_cvr`,
        `linked_system_id`,
        `arbejdsfunktion_id`,
        `rettighed_id`,
        `status_id`,
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
        1,
        '2000-05-22 00:00:00',
        '2000-05-22 23:59:59',
        '2000-05-22 00:00:00',
        0,
        '2000-05-22 02:15:00',
        NULL);

INSERT INTO `system_variable` (`name`, `value`) VALUES ('testVariable', 'den gode test');

INSERT INTO `whitelist` (`name`, `legal_cvr`) VALUES ('test', '1');
INSERT INTO `whitelist` (`name`, `legal_cvr`) VALUES ('test', '2');

INSERT INTO `whitelist` (`name`, `legal_cvr`) VALUES ('bemyndigelsesservice.indlaesMetadata', '25520041');
INSERT INTO `whitelist` (`name`, `legal_cvr`) VALUES ('bemyndigelsesservice.hentMetadata', '25520041');
INSERT INTO `whitelist` (`name`, `legal_cvr`) VALUES ('', '25520041');