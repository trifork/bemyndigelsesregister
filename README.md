Bemyndigelsesservice
===

For at køre integrationstest skal Spring profilen "test" være aktiveret.

Det er Maven Surefire pluginen som afvikler test. Surefire forker testeksekveringen for at få et rent environment. Det betyder at Surefire ignorerer -Dproperty=value. I stedet benytter Surefire en speciel system property 'argLine' hvori man så angiver de properties der skal sættes som system properties. F.eks. -DargLine="-Dspring.profiles.active=test".

Parent pom filen indeholder en Maven profil med id "test" som sætter denne argLine property automatisk, så man kan køre mvn clean package uden at angive andet.

Ønsker man at benytte "ci" - eller "dev" Maven-profilerne der også findes i parent pom'en skal man huske at angive "test" også f.eks. mvn -P ci,test clean package

Running with MySQL
===

```
CREATE USER 'bemyndigelse'@'localhost' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'localhost';

CREATE DATABASE bemyndigelsetest;
GRANT ALL PRIVILEGES ON bemyndigelsetest.* TO 'bemyndigelse'@'localhost';
```