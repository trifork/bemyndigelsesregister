Bemyndigelsesservice
===

Build Release
===
To build version 1.6 e.g.:

	mvn -DreleaseVersion=1.6 -DdevelopmentVersion=1.7-SNAPSHOT -DautoVersionSubmodules=true -Dtag=bemyndigelsesregister-1.6 --batch-mode -Dresume=false release:prepare flyway:clean compile flyway:migrate release:perform -s settings.xml


	hvor settings.xml indeholder følgende:
	<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    	<servers>
    		<server>
    			<id>trifork.snapshots</id>
    			<username>fmk</username>
    			<password>fmk123</password>
    		</server>
    		<server>
    			<id>trifork.releases</id>
    			<username>fmk</username>
    			<password>fmk123</password>
    		</server>
    	</servers>
    </settings>


Husk at inkludere filen bemyndigelsesservice/src/main/resources/db/manual/trifork_data.sql når der releases til systemer der skal køre bemyndigelse for FMK eller DDV fra Trifork.

Running with MySQL
===

```
CREATE USER 'bemyndigelse'@'localhost' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'%';

CREATE DATABASE bemyndigelsetest;
GRANT ALL PRIVILEGES ON bemyndigelsetest.* TO 'bemyndigelse'@'%';
```

