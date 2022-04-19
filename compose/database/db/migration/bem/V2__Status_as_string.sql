ALTER TABLE bemyndigelse ADD COLUMN status ENUM('GODKENDT', 'BESTILT') NOT NULL DEFAULT 'BESTILT';

UPDATE bemyndigelse SET status = 'GODKENDT';

ALTER TABLE bemyndigelse DROP FOREIGN KEY status_type;
ALTER TABLE bemyndigelse DROP COLUMN status_id;

DROP TABLE status_type;