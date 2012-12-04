Bemyndigelsesservice
===

Build Release
===
To build version 1.6 e.g.:
mvn -DreleaseVersion=1.6 -DdevelopmentVersion=1.7-SNAPSHOT -DautoVersionSubmodules=true -Dtag=bemyndigelsesregister-1.6 --batch-mode -Dresume=false release:prepare flyway:clean compile flyway:migrate release:perform

Husk at inkludere filen bemyndigelsesservice/src/main/resources/db/manual/trifork_data.sql når der releases til systemer der skal køre bemyndigelse for FMK eller DDV fra Trifork.

Running with MySQL
===

```
CREATE USER 'bemyndigelse'@'localhost' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'localhost';

CREATE DATABASE bemyndigelsetest;
GRANT ALL PRIVILEGES ON bemyndigelsetest.* TO 'bemyndigelse'@'localhost';
```

