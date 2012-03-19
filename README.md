Bemyndigelsesservice
===

TBD

Running with MySQL
===

```
CREATE USER 'bemyndigelse'@'localhost' IDENTIFIED BY '';
CREATE DATABASE bemyndigelse;
GRANT ALL PRIVILEGES ON bemyndigelse.* TO 'bemyndigelse'@'localhost';
```

Add the following systemproperty

spring.profiles.active=live