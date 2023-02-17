-- delegerbar flag til delegerbar_rettighed kolonne

ALTER TABLE delegerbar_rettighed ADD COLUMN delegerbar TINYINT(1) NOT NULL DEFAULT 1;
