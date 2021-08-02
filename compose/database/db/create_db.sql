CREATE USER 'bemyndigelse'@'%' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'%';

CREATE DATABASE bemyndigelsetest;
GRANT ALL PRIVILEGES ON bemyndigelsetest.* TO 'bemyndigelse'@'%';
