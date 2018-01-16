ALTER TABLE `valued`.`solicitud_tasacion` 
CHANGE COLUMN `montoCompraEstimado` `montoCompraEstimadoUF` FLOAT NULL DEFAULT NULL ,
ADD COLUMN `montoCompraEstimadoPesos` FLOAT NULL DEFAULT NULL AFTER `montoCompraEstimadoUF`;

ALTER TABLE `valued`.`usuario` 
CHANGE COLUMN `rol` `rolId` INT(10) NULL DEFAULT NULL ;

CREATE TABLE `valued`.`rol` (
  `rolId` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rolId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

INSERT INTO `valued`.`rol` (`rolId`, `descripcion`, `nombre`) VALUES ('1', 'Administrador de plataforma', 'Administrador');
INSERT INTO `valued`.`rol` (`rolId`, `nombre`) VALUES ('2', 'Visador');


CREATE TABLE `valued`.`permisos` (
  `rolId` bigint(20) DEFAULT NULL,
  `rol_permisos` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

