CREATE USER 'bemyndigelse'@'%' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'%';

CREATE USER 'cra'@'%' IDENTIFIED BY 'cra';
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'cra'@'%';
