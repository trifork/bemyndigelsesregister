INSERT INTO status_type (status, sidst_modificeret, sidst_modificeret_af) VALUES ('OK', NOW(), 'mwl');
INSERT INTO linked_system(system) VALUES ('Trifork test system');
INSERT INTO domaene(domaene) VALUES ('trifork');
INSERT INTO arbejdsfunktion(arbejdsfunktion, domaene_id, beskrivelse, linked_system_id) values ('Laege', 1, 'For unit test only', 1);
INSERT INTO rettighed(rettighedskode, beskrivelse, domaene_id, linked_system_id) VALUES ('R01', 'Laegemiddelordination', 1, 1);
