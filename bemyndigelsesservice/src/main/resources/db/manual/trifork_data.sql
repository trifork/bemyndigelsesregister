/* Opret Trifork domaene */
REPLACE INTO domaene (kode, sidst_modificeret, sidst_modificeret_af) VALUES ('Trifork', '2012-11-30 10:00:00', 'frj@trifork.com');

/* Opret default systemer */
REPLACE INTO linked_system (domaene_id, kode, beskrivelse, sidst_modificeret, sidst_modificeret_af) VALUES (1, 'DDV', 'Det Danske Vaccinationsregister', '2012-11-30 10:00:00', 'frj@trifork.com');

REPLACE INTO linked_system (domaene_id, kode, beskrivelse, sidst_modificeret, sidst_modificeret_af) VALUES (1, 'FMK', 'Det Fælles Medicinkort', '2012-11-30 10:00:00', 'frj@trifork.com');


/* Whitelist 25520041 - dette cvr kan hente metadata, indlaese metadata og oprette bemyndigelser (OGSAA godkendte bemyndigelser) */
REPLACE INTO `whitelist` (`name`, `legal_cvr`) VALUES ('bemyndigelsesservice.indlaesMetadata', '25520041');
REPlACE INTO `whitelist` (`name`, `legal_cvr`) VALUES ('bemyndigelsesservice.hentMetadata', '25520041');
REPLACE INTO `whitelist` (`name`, `legal_cvr`) VALUES ('', '25520041');