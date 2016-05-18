/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

LOCK TABLES `delegerbar_rettighed` WRITE;
DELETE FROM delegerbar_rettighed;
UNLOCK TABLES;

LOCK TABLES `rettighed` WRITE;
DELETE FROM rettighed;
UNLOCK TABLES;

LOCK TABLES `arbejdsfunktion` WRITE;
DELETE FROM `arbejdsfunktion`;
UNLOCK TABLES;

LOCK TABLES `linked_system` WRITE;
DELETE FROM `linked_system`;
UNLOCK TABLES;

LOCK TABLES `domaene` WRITE;
DELETE FROM `domaene`;
UNLOCK TABLES;

LOCK TABLES `domaene` WRITE;
INSERT INTO `domaene` VALUES (1,'Trifork','2012-11-30 10:00:00','frj@trifork.com');
UNLOCK TABLES;

LOCK TABLES `linked_system` WRITE;
INSERT INTO `linked_system` VALUES (1,'DDV','2012-11-30 10:00:00','frj@trifork.com',1,'Det Danske Vaccinationsregister'),(2,'FMK','2012-11-30 10:00:00','frj@trifork.com',1,'Det Fælles Medicinkort'),(3,'TAS','2013-07-03 12:06:59','ahj',1,'Tilskudsansøgningsservicen');
UNLOCK TABLES;

LOCK TABLES `arbejdsfunktion` WRITE;
INSERT INTO `arbejdsfunktion` VALUES (42,2,'Læge','Læge',NULL,NULL),(43,2,'Tandlæge','Tandlæge',NULL,NULL),(44,2,'Jordemoder','Jordemoder',NULL,NULL),(45,2,'Sygeplejerske','Sygeplejerske',NULL,NULL),(46,2,'Social- og sundhedsassistent','Social- og sundhedsassistent',NULL,NULL),(47,2,'Social- og sundhedshjælper','Social- og sundhedshjælper',NULL,NULL),(48,2,'Sundhedsplejerske','Sundhedsplejerske',NULL,NULL),(49,2,'Farmaceut','Farmaceut',NULL,NULL),(50,2,'Farmakonom','Farmakonom',NULL,NULL),(51,2,'Assistent for Læge','Assistent for Læge',NULL,NULL),(52,2,'Assistent for Tandlæge','Assistent for Tandlæge',NULL,NULL),(53,2,'Assistent for Jordemoder','Assistent for Jordemoder',NULL,NULL),(54,2,'Assistent for Sygeplejer','Assistent for Sygeplejer',NULL,NULL),(55,2,'Assistent for Social- og sundhedsassistent','Assistent for Social- og sundhedsassistent',NULL,NULL),(56,2,'Borger','Borger',NULL,NULL),(57,2,'Forældermyndighed','Forældermyndighed',NULL,NULL),(58,2,'Værge','Værge',NULL,NULL),(59,2,'Web administrator','Web administrator',NULL,NULL),(60,2,'Apoteker','Apoteker',NULL,NULL),(77,1,'Læge','Læge',NULL,NULL),(78,1,'Tandlæge','Tandlæge',NULL,NULL),(79,1,'Jordemoder','Jordemoder',NULL,NULL),(80,1,'Sygeplejerske','Sygeplejerske',NULL,NULL),(81,1,'Social- og sundhedsassistent','Social- og sundhedsassistent',NULL,NULL),(82,1,'Social- og sundhedshjælper','Social- og sundhedshjælper',NULL,NULL),(83,1,'Sundhedsplejerske','Sundhedsplejerske',NULL,NULL),(84,1,'Farmaceut','Farmaceut',NULL,NULL),(85,1,'Farmakonom','Farmakonom',NULL,NULL),(86,1,'Assistent for Læge','Assistent for Læge',NULL,NULL),(87,1,'Assistent for Tandlæge','Assistent for Tandlæge',NULL,NULL),(88,1,'Assistent for Jordemoder','Assistent for Jordemoder',NULL,NULL),(89,1,'Assistent for Sygeplejer','Assistent for Sygeplejer',NULL,NULL),(90,1,'Assistent for Social- og sundhedsassistent','Assistent for Social- og sundhedsassistent',NULL,NULL),(91,1,'Borger','Borger',NULL,NULL),(92,1,'Forældermyndighed','Forældermyndighed',NULL,NULL),(93,1,'Værge','Værge',NULL,NULL),(94,3,'Læge','Læge',NULL,NULL),(95,3,'Tandlæge','Tandlæge',NULL,NULL);
UNLOCK TABLES;

LOCK TABLES `rettighed` WRITE;
INSERT INTO `rettighed` VALUES (19,2,'BorgerOpslag','Borger opslag',NULL,NULL),(20,2,'SundhedsfagligOpslag','Sundhedsfagligt opslag',NULL,NULL),(21,2,'Recept','Opret eller annuller recept',NULL,NULL),(22,2,'Lægemiddelordination','Opret, ret eller seponer lægemiddelordination',NULL,NULL),(23,2,'Effektuering','Opret, ret eller slet effektuering (indgivelse/udlevering af et lægemiddel)',NULL,NULL),(24,2,'Privatmarkering','Sæt/fjern privatmarkering',NULL,NULL),(25,2,'VisPrivatmarkeretVærdispring','Vis privatmarkeret med værdispring',NULL,NULL),(26,2,'VisPrivatmarkeretSamtykke','Vis privatmarkeret med samtykke',NULL,NULL),(27,2,'Suspendering','Suspendering: Suspender/frigiv medicinkort',NULL,NULL),(28,2,'Afstemning','Sæt markering for medicinafstemning',NULL,NULL),(29,2,'LøsRecept','Ændring af status for løs recept (Opret ordination på baggrund af løs recept eller marker løs recept ”ikke aktuel”)',NULL,NULL),(30,2,'BestilEffektuering','Bestil effektuering: Bestil udlevering eller ny recept',NULL,NULL),(31,2,'Tilknytning','Opretning tilknytning til en enhed (F.eks. tilknytning til hjemmepleje)',NULL,NULL),(39,1,'Administration','Administration',NULL,NULL),(40,1,'DEFAULT','Default permissions - Ingen specielle permissions påkrævet',NULL,NULL),(41,1,'VaccinationSe','Se Vaccinationer',NULL,NULL),(42,1,'VaccinationVedligehold','Opret, ret eller slet vaccinationer',NULL,NULL),(43,1,'VaccinationVedligeholdAnbefalet','Opret, ret eller slet anbefalede vaccinationer',NULL,NULL),(44,1,'VaccinationGodkend','Godkend vaccinationer',NULL,NULL),(45,1,'AuditLog','Se og søge i auditlog',NULL,NULL),(46,3,'LæsKladder','Vise kladder for tilskudsansøgninger',NULL,NULL),(47,3,'SkrivKladder','Rette og slette kladder for tilskudsansøgninger',NULL,NULL),(48,3,'LæsSager','Vise indsendte tilskudsansøgninger',NULL,NULL),(49,3,'SkrivSager','Indsende tilskudsansøgninger og YO-svar',NULL,NULL);
INSERT INTO `rettighed` VALUES (50,1, '*', 'Alle delegerbare rettigheder (inkl. fremtidige)','2016-04-04 21:00:00','2016-04-04 21:00:00'),(51,2, '*', 'Alle delegerbare rettigheder (inkl. fremtidige)','2016-04-04 21:00:00','2016-04-04 21:00:00'),(52,3, '*', 'Alle delegerbare rettigheder (inkl. fremtidige)','2016-04-04 21:00:00','2016-04-04 21:00:00');
UNLOCK TABLES;

LOCK TABLES `delegerbar_rettighed` WRITE;
INSERT INTO `delegerbar_rettighed` VALUES (296,42,20,NULL,NULL,1),(297,42,22,NULL,NULL,1),(298,42,23,NULL,NULL,1),(299,42,24,NULL,NULL,1),(300,42,25,NULL,NULL,1),(301,42,26,NULL,NULL,1),(302,42,27,NULL,NULL,1),(303,42,28,NULL,NULL,1),(304,42,29,NULL,NULL,1),(305,42,31,NULL,NULL,1),(306,42,30,NULL,NULL,1),(307,43,20,NULL,NULL,1),(308,43,22,NULL,NULL,1),(309,43,23,NULL,NULL,1),(310,43,24,NULL,NULL,1),(311,43,25,NULL,NULL,1),(312,43,26,NULL,NULL,1),(313,43,27,NULL,NULL,1),(314,43,28,NULL,NULL,1),(315,43,29,NULL,NULL,1),(316,43,31,NULL,NULL,1),(317,43,30,NULL,NULL,1),(318,44,20,NULL,NULL,1),(319,44,23,NULL,NULL,1),(320,44,25,NULL,NULL,1),(321,44,26,NULL,NULL,1),(322,44,27,NULL,NULL,1),(323,44,31,NULL,NULL,1),(324,44,30,NULL,NULL,1),(325,45,20,NULL,NULL,1),(326,45,23,NULL,NULL,1),(327,45,25,NULL,NULL,1),(328,45,26,NULL,NULL,1),(329,45,27,NULL,NULL,1),(330,45,31,NULL,NULL,1),(331,45,30,NULL,NULL,1),(332,46,20,NULL,NULL,1),(333,46,23,NULL,NULL,1),(334,46,25,NULL,NULL,1),(335,46,26,NULL,NULL,1),(336,46,27,NULL,NULL,1),(337,46,31,NULL,NULL,1),(338,46,30,NULL,NULL,1),(339,77,41,NULL,NULL,1),(340,77,42,NULL,NULL,1),(341,77,43,NULL,NULL,1),(342,77,44,NULL,NULL,1),(343,78,41,NULL,NULL,1),(344,78,42,NULL,NULL,1),(345,78,43,NULL,NULL,1),(346,79,41,NULL,NULL,1),(347,79,42,NULL,NULL,1),(348,79,43,NULL,NULL,1),(349,80,41,NULL,NULL,1),(350,80,42,NULL,NULL,1),(351,80,43,NULL,NULL,1),(352,81,41,NULL,NULL,1),(353,94,46,NULL,NULL,1),(354,94,47,NULL,NULL,1),(355,94,48,NULL,NULL,1),(356,95,46,NULL,NULL,1),(357,95,47,NULL,NULL,1),(358,95,48,NULL,NULL,1),(359,60,20,NULL,NULL,1),(360,60,26,NULL,NULL,1);
INSERT INTO `delegerbar_rettighed` VALUES (398,42,21,'2016-05-18 11:32:00','2016-05-18 11:32:00',0);
INSERT INTO `delegerbar_rettighed` VALUES (361,77,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(362,79,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(363,80,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(364,81,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(365,82,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(366,83,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(367,84,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(368,85,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(369,86,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(370,87,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(371,88,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(372,89,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(373,90,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(374,91,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(375,92,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(376,93,50,'2016-04-04 21:00:00','2016-04-04 21:00:00',1);
INSERT INTO `delegerbar_rettighed` VALUES (377,43,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(378,44,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(379,45,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(380,46,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(381,47,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(382,48,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(383,49,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(384,50,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(385,51,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(386,52,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(387,53,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(388,54,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(389,55,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(390,56,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(391,57,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(392,58,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(393,59,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(394,60,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(395,42,51,'2016-04-04 21:00:00','2016-04-04 21:00:00',1);
INSERT INTO `delegerbar_rettighed` VALUES (396,94,52,'2016-04-04 21:00:00','2016-04-04 21:00:00',1),(397,95,52,'2016-04-04 21:00:00','2016-04-04 21:00:00',1);
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
