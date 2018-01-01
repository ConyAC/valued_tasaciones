ALTER TABLE `valued`.`solicitud_tasacion` 
CHANGE COLUMN `montoCompraEstimado` `montoCompraEstimadoUF` FLOAT NULL DEFAULT NULL ,
ADD COLUMN `montoCompraEstimadoPesos` FLOAT NULL DEFAULT NULL AFTER `montoCompraEstimadoUF`;