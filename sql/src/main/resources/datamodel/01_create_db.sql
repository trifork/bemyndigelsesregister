CREATE USER 'bemyndigelse'@'%' IDENTIFIED BY '';
CREATE DATABASE if not exists bemyndigelse CHARACTER SET 'utf8' COLLATE 'utf8_danish_ci';
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'%';
