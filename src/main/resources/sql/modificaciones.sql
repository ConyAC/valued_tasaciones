ALTER TABLE `valued`.`solicitud_tasacion` 
CHANGE COLUMN `montoCompraEstimado` `montoCompraEstimadoUF` FLOAT NULL DEFAULT NULL ,
ADD COLUMN `montoCompraEstimadoPesos` FLOAT NULL DEFAULT NULL AFTER `montoCompraEstimadoUF`;

ALTER TABLE `valued`.`usuario` 
CHANGE COLUMN `rol` `rolId` INT(10) NULL DEFAULT NULL ;


ALTER TABLE `valued`.`usuario` 
CHANGE COLUMN `habilitado` `estadoUsuario` INT(10) NOT NULL ;

ALTER TABLE `valued`.`usuario` 
CHANGE COLUMN `contrasena` `contrasena` VARCHAR(100) NULL DEFAULT NULL ;

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

insert into permisos (rolId, rol_permisos)values(1,'ADMINISTRACION');
insert into permisos (rolId, rol_permisos)values(1,'INGRESAR_SOLICITUD');
insert into permisos (rolId, rol_permisos)values(1,'FACTURAR');
insert into permisos (rolId, rol_permisos)values(1,'ENVIAR_A_CLIENTE');
insert into permisos (rolId, rol_permisos)values(1,'VISUALIZAR_REPORTES');
insert into permisos (rolId, rol_permisos)values(1,'VISUALIZAR_TASACIONES');
insert into permisos (rolId, rol_permisos)values(1,'BUSCAR_TASACIONES');


ALTER TABLE `valued`.`usuario` 
CHANGE COLUMN `habilitado` `eliminado` INT(11) NOT NULL ;

/*observacion reparo*/
ALTER TABLE `valued`.`solicitud_tasacion` ADD COLUMN `observacionReparo` TEXT NULL DEFAULT NULL  AFTER `tasadorId` ;

/* facturación */
CREATE  TABLE `factura` (
  `id` INT(10) NOT NULL ,
  `nombre` VARCHAR(45) NULL ,
  `numero` VARCHAR(45) NOT NULL ,
  `clienteId` INT(10) NOT NULL ,
  `montoManual` FLOAT NULL ,
  `montoCalculado` FLOAT NULL ,
  `fecha` DATETIME NULL ,
  `estado` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) );

ALTER TABLE `factura` ADD COLUMN `usuarioId` INT(10) NOT NULL  AFTER `estado` ;
ALTER TABLE `factura` CHANGE COLUMN `id` `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT  ;

CREATE TABLE `factura_solicitud_tasacion` (
  `factura_Id` int(10) NOT NULL,
  `solicitud_Id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1$$


ALTER TABLE `factura` DROP COLUMN `montoCalculado` ;

CREATE  TABLE `bitacora` (
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `solicitud_tasacionId` INT(10) NOT NULL ,
  `etapaTasacion` VARCHAR(145) NOT NULL ,
  `usuarioId` INT(10) NOT NULL ,
  `fechaInicio` DATETIME NOT NULL ,
  `fechaTermino` DATETIME NOT NULL ,
  PRIMARY KEY (`id`) );

CREATE TABLE `valor_uf` (
  `fecha` date NOT NULL,
  `valor` double NOT NULL,
  PRIMARY KEY (`fecha`)
);
