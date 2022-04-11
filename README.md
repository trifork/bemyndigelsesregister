Bemyndigelsesservice
===

Bemyndigelsesservicen (BEM) anvendes til at oprette bemyndigelser fra en person, som har ret til at anvende et IT-system, til en anden person. En bemyndigelse giver en person ret til at handle på den bemyndigendes vegne og gælder som udgangspunkt 2 år. En bemyndigelse angiver systemet, arbejdsfunktionen, den gyldige tidsperiode og de specifikke rettigheder den gælder til. Den bemyndigede og den bemyndigende angives ved deres CPR-numre, endvidere kan bemyndigelsen begrænses til et bestemt CVR-nummer.


Development build
===
To build, run Maven command:

	mvn clean install

This will compile, build the war-file and run unit tests. 

Docker deployment
===
To run service in Docker, run Docker command:

	cd compose/development
	docker-compose up -d --build

This will build docker images, including MariaDB, initialize database using FlyWay, and bring up the BEM service.

To check if service is running, open this URL in a browser:

    http://localhost:8080/bem/health

Running integration tests
===
Integration tests are running against service running in docker. After bringing up Docker as described above, run the integrationtests using Maven: 

	mvn clean install -rf :integrationtest -PITs


CRA service
===
Endpoints:

    http://localhost:8089/cra/version
    http://localhost:8089/cra/status
    http://localhost:8089/cra/job/revokeUpdate/start
    http://localhost:8089/cra/job/revokeUpdate/status
    http://localhost:8089/cra/job/cleanupRevocationLists/start
    http://localhost:8089/cra/job/cleanupRevocationLists/status
