-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: valued
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.28-MariaDB

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

--
-- Table structure for table `comuna`
--

DROP TABLE IF EXISTS `comuna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comuna` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `regionId` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15203 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comuna`
--

LOCK TABLES `comuna` WRITE;
/*!40000 ALTER TABLE `comuna` DISABLE KEYS */;
INSERT INTO `comuna` VALUES (1101,'Iquique',2),(1107,'Alto Hospicio',2),(1401,'Pozo Almonte',2),(1402,'Camiña',2),(1403,'Colchane',2),(1404,'Huara',2),(1405,'Pica',2),(2101,'Antofagasta',3),(2102,'Mejillones',3),(2103,'Sierra Gorda',3),(2104,'Taltal',3),(2201,'Calama',3),(2202,'Ollagüe',3),(2203,'San Pedro de Atacama',3),(2301,'Tocopilla',3),(2302,'María Elena',3),(3101,'Copiapó',4),(3102,'Caldera',4),(3103,'Tierra Amarilla',4),(3201,'Chañaral',4),(3202,'Diego de Almagro',4),(3301,'Vallenar',4),(3302,'Alto del Carmen',4),(3303,'Freirina',4),(3304,'Huasco',4),(4101,'La Serena',5),(4102,'Coquimbo',5),(4103,'Andacollo',5),(4104,'La Higuera',5),(4105,'Paihuano',5),(4106,'Vicuña',5),(4201,'Illapel',5),(4202,'Canela',5),(4203,'Los Vilos',5),(4204,'Salamanca',5),(4301,'Ovalle',5),(4302,'Combarbalá',5),(4303,'Monte Patria',5),(4304,'Punitaqui',5),(4305,'Río Hurtado',5),(5101,'Valparaíso',6),(5102,'Casablanca',6),(5103,'Concón',6),(5104,'Juan Fernández',6),(5105,'Puchuncaví',6),(5107,'Quintero',6),(5109,'Viña del Mar',6),(5201,'Isla de Pascua',6),(5301,'Los Andes',6),(5302,'Calle Larga',6),(5303,'Rinconada',6),(5304,'San Esteban',6),(5401,'La Ligua',6),(5402,'Cabildo',6),(5403,'Papudo',6),(5404,'Petorca',6),(5405,'Zapallar',6),(5501,'Quillota',6),(5502,'Calera',6),(5503,'Hijuelas',6),(5504,'La Cruz',6),(5506,'Nogales',6),(5601,'San Antonio',6),(5602,'Algarrobo',6),(5603,'Cartagena',6),(5604,'El Quisco',6),(5605,'El Tabo',6),(5606,'Santo Domingo',6),(5701,'San Felipe',6),(5702,'Catemu',6),(5703,'Llaillay',6),(5704,'Panquehue',6),(5705,'Putaendo',6),(5706,'Santa María',6),(5801,'Quilpué',6),(5802,'Limache',6),(5803,'Olmué',6),(5804,'Villa Alemana',6),(6101,'Rancagua',7),(6102,'Codegua',7),(6103,'Coinco',7),(6104,'Coltauco',7),(6105,'Doñihue',7),(6106,'Graneros',7),(6107,'Las Cabras',7),(6108,'Machalí',7),(6109,'Malloa',7),(6110,'Mostazal',7),(6111,'Olivar',7),(6112,'Peumo',7),(6113,'Pichidegua',7),(6114,'Quinta de Tilcoco',7),(6115,'Rengo',7),(6116,'Requinoa',7),(6117,'San Vicente',7),(6201,'Pichilemu',7),(6202,'La Estrella',7),(6203,'Litueche',7),(6204,'Marchihue',7),(6205,'Navidad',7),(6206,'Paredones',7),(6301,'San Fernando',7),(6302,'Chépica',7),(6303,'Chimbarongo',7),(6304,'Lolol',7),(6305,'Nancagua',7),(6306,'Palmilla',7),(6307,'Peralillo',7),(6308,'Placilla',7),(6309,'Pumanque',7),(6310,'Santa Cruz',7),(7101,'Talca',8),(7102,'Constitución',8),(7103,'Curepto',8),(7104,'Empedrado',8),(7105,'Maule',8),(7106,'Pelarco',8),(7107,'Pencahue',8),(7108,'Río Claro',8),(7109,'San Clemente',8),(7110,'San Rafael',8),(7201,'Cauquenes',8),(7202,'Chanco',8),(7203,'Pelluhue',8),(7301,'Curicó',8),(7302,'Hualañe',8),(7303,'Licantén',8),(7304,'Molina',8),(7305,'Rauco',8),(7306,'Romeral',8),(7307,'Sagrada Familia',8),(7308,'Teno',8),(7309,'Vichuquén',8),(7401,'Linares',8),(7402,'Colbún',8),(7403,'Longaví',8),(7404,'Parral',8),(7405,'Retiro',8),(7406,'San Javier',8),(7407,'Villa Alegre',8),(7408,'Yerbas Buenas',8),(8101,'Concepción',9),(8102,'Coronel',9),(8103,'Chiguayante',9),(8104,'Florida',9),(8105,'Hualqui',9),(8106,'Lota',9),(8107,'Penco',9),(8108,'San Pedro de la Paz',9),(8109,'Santa Juana',9),(8110,'Talcahuano',9),(8111,'Tomé',9),(8112,'Hualpén',9),(8201,'Lebu',9),(8202,'Arauco',9),(8203,'Cañete',9),(8204,'Contulmo',9),(8205,'Curanilahue',9),(8206,'Los Alamos',9),(8207,'Tirúa',9),(8301,'Los Angeles',9),(8302,'Antuco',9),(8303,'Cabrero',9),(8304,'Laja',9),(8305,'Mulchén',9),(8306,'Nacimiento',9),(8307,'Negrete',9),(8308,'Quilaco',9),(8309,'Quilleco',9),(8310,'San Rosendo',9),(8311,'Santa Bárbara',9),(8312,'Tucapel',9),(8313,'Yumbel',9),(8314,'Alto Biobío',9),(8401,'Chillán',9),(8402,'Bulnes',9),(8403,'Cobquecura',9),(8404,'Coelemu',9),(8405,'Coihueco',9),(8406,'Chillán Viejo',9),(8407,'El Carmen',9),(8408,'Ninhue',9),(8409,'Ñiquén',9),(8410,'Pemuco',9),(8411,'Pinto',9),(8412,'Portezuelo',9),(8413,'Quillón',9),(8414,'Quirihue',9),(8415,'Ránquil',9),(8416,'San Carlos',9),(8417,'San Fabián',9),(8418,'San Ignacio',9),(8419,'San Nicolás',9),(8420,'Treguaco',9),(8421,'Yungay',9),(9101,'Temuco',10),(9102,'Carahue',10),(9103,'Cunco',10),(9104,'Curarrehue',10),(9105,'Freire',10),(9106,'Galvarino',10),(9107,'Gorbea',10),(9108,'Lautaro',10),(9109,'Loncoche',10),(9110,'Melipeuco',10),(9111,'Nueva Imperial',10),(9112,'Padre Las Casas',10),(9113,'Perquenco',10),(9114,'Pitrufquén',10),(9115,'Pucón',10),(9116,'Saavedra',10),(9117,'Teodoro Schmidt',10),(9118,'Toltén',10),(9119,'Vilcún',10),(9120,'Villarrica',10),(9121,'Cholchol',10),(9201,'Angol',10),(9202,'Collipulli',10),(9203,'Curacautín',10),(9204,'Ercilla',10),(9205,'Lonquimay',10),(9206,'Los Sauces',10),(9207,'Lumaco',10),(9208,'Purén',10),(9209,'Renaico',10),(9210,'Traiguén',10),(9211,'Victoria',10),(10101,'Puerto Montt',12),(10102,'Calbuco',12),(10103,'Cochamó',12),(10104,'Fresia',12),(10105,'Frutillar',12),(10106,'Los Muermos',12),(10107,'Llanquihue',12),(10108,'Maullín',12),(10109,'Puerto Varas',12),(10201,'Castro',12),(10202,'Ancud',12),(10203,'Chonchi',12),(10204,'Curaco de Vélez',12),(10205,'Dalcahue',12),(10206,'Puqueldón',12),(10207,'Queilén',12),(10208,'Quellón',12),(10209,'Quemchi',12),(10210,'Quinchao',12),(10301,'Osorno',12),(10302,'Puerto Octay',12),(10303,'Purranque',12),(10304,'Puyehue',12),(10305,'Río Negro',12),(10306,'San Juan de la Costa',12),(10307,'San Pablo',12),(10401,'Chaitén',12),(10402,'Futaleufú',12),(10403,'Hualaihué',12),(10404,'Palena',12),(11101,'Coihaique',13),(11102,'Lago Verde',13),(11201,'Aisén',13),(11202,'Cisnes',13),(11203,'Guaitecas',13),(11301,'Cochrane',13),(11302,'O´´Higgins',13),(11303,'Tortel',13),(11401,'Chile Chico',13),(11402,'Río Ibáñez',13),(12101,'Punta Arenas',14),(12102,'Laguna Blanca',14),(12103,'Río Verde',14),(12104,'San Gregorio',14),(12201,'Cabo de Hornos',14),(12202,'Antártica',14),(12301,'Porvenir',14),(12302,'Primavera',14),(12303,'Timaukel',14),(12401,'Natales',14),(12402,'Torres del Paine',14),(13101,'Santiago',15),(13102,'Cerrillos',15),(13103,'Cerro Navia',15),(13104,'Conchalí',15),(13105,'El Bosque',15),(13106,'Estación Central',15),(13107,'Huechuraba',15),(13108,'Independencia',15),(13109,'La Cisterna',15),(13110,'La Florida',15),(13111,'La Granja',15),(13112,'La Pintana',15),(13113,'La Reina',15),(13114,'Las Condes',15),(13115,'Lo Barnechea',15),(13116,'Lo Espejo',15),(13117,'Lo Prado',15),(13118,'Macul',15),(13119,'Maipú',15),(13120,'Ñuñoa',15),(13121,'Pedro Aguirre Cerda',15),(13122,'Peñalolén',15),(13123,'Providencia',15),(13124,'Pudahuel',15),(13125,'Quilicura',15),(13126,'Quinta Normal',15),(13127,'Recoleta',15),(13128,'Renca',15),(13129,'San Joaquín',15),(13130,'San Miguel',15),(13131,'San Ramón',15),(13132,'Vitacura',15),(13201,'Puente Alto',15),(13202,'Pirque',15),(13203,'San José de Maipo',15),(13301,'Colina',15),(13302,'Lampa',15),(13303,'Tiltil',15),(13401,'San Bernardo',15),(13402,'Buin',15),(13403,'Calera de Tango',15),(13404,'Paine',15),(13501,'Melipilla',15),(13502,'Alhué',15),(13503,'Curacaví',15),(13504,'María Pinto',15),(13505,'San Pedro',15),(13601,'Talagante',15),(13602,'El Monte',15),(13603,'Isla de Maipo',15),(13604,'Padre Hurtado',15),(13605,'Peñaflor',15),(14101,'Valdivia',11),(14102,'Corral',11),(14103,'Lanco',11),(14104,'Los Lagos',11),(14105,'Máfil',11),(14106,'Mariquina',11),(14107,'Paillaco',11),(14108,'Panguipulli',11),(14201,'La Unión',11),(14202,'Futrono',11),(14203,'Lago Ranco',11),(14204,'Río Bueno',11),(15101,'Arica',1),(15102,'Camarones',1),(15201,'Putre',1),(15202,'General Lagos',1);
/*!40000 ALTER TABLE `comuna` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,'Arica y Parinacota'),(2,'Tarapacá'),(3,'Antofagasta'),(4,'Atacama'),(5,'Coquimbo'),(6,'Valparaíso'),(7,'Del Libertador Gral. Bernardo O\'Higgins'),(8,'Del Maule'),(9,'Del Biobío'),(10,'De La Araucanía'),(11,'De Los Ríos'),(12,'De Los Lagos'),(13,'Aysén Del Gral. Carlos Ibañez Del Campo'),(14,'Magallanes Y De La Antártica Chilena'),(15,'Metropolitana De Santiago');
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-28 22:27:01
