create database JURISDB_JUDICIAL;
use JURISDB_JUDICIAL;

CREATE TABLE If Not Exists JURISDB_JUDICIAL.TipoDocumento (
  idTipoDocumento int primary key not null auto_increment,
  idInstancia char(10) default Null Comment 'ID de Instancia',
  descripcion char(150) default Null Comment 'Descripcion Tipo de Documento',
  regUserId bigint Null Comment 'Usuario create',
  regDate date Null Comment 'Fecha create',
  regDatetime datetime Null Comment 'Fecha Hora create',
  regTimestamp bigint Null Comment 'Epoch create',
  updUserId bigint Null Comment 'Usuario Update',
  updDate date Null Comment 'Fecha Update',
  updDatetime datetime Null Comment 'Fecha Hora Update',
  updTimestamp bigint Null Comment 'Epoch Update',
  activo tinyint null,
  borrado tinyint null
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Tabla de Tipos de Documentos que se visualizarán en el combobox';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.TipoDocumento
    ADD INDEX idInstanciaIDX (idInstancia),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);

CREATE TABLE If Not Exists JURISDB_JUDICIAL.Documento (
  idDocumento int primary key not null auto_increment,
  idTipoDocumento int not null,
  descripcion char(200) default Null Comment 'Descripcion Documento',
  regUserId bigint Null Comment 'Usuario create',
  regDate date Null Comment 'Fecha create',
  regDatetime datetime Null Comment 'Fecha Hora create',
  regTimestamp bigint Null Comment 'Epoch create',
  updUserId bigint Null Comment 'Usuario Update',
  updDate date Null Comment 'Fecha Update',
  updDatetime datetime Null Comment 'Fecha Hora Update',
  updTimestamp bigint Null Comment 'Epoch Update',
  activo tinyint null,
  borrado tinyint null
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Tabla de Documentos que se visualizarán en el combobox';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.Documento
    ADD INDEX idTipoDocumentoIDX (idTipoDocumento),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);




insert into JURISDB_JUDICIAL.TipoDocumento(idTipoDocumento, idInstancia, descripcion, activo, borrado)  values
(1, '701', 'AUTO', 1, 0),
(2, '701', 'DECRETO', 1, 0),
(3, '701', 'DEMANDA', 1, 0),
(4, '701', 'OFICIO', 1, 0),
(5, '702', 'AUTO', 1, 0),
(6, '702', 'DECRETO', 1, 0),
(7, '702', 'DEMANDA', 1, 0),
(8, '702', 'OFICIO', 1, 0),
(9, '044', 'AUTO', 1, 0),
(10, '044', 'DECRETO', 1, 0),
(11, '044', 'DEMANDA', 1, 0),
(12, '044', 'OFICIO', 1, 0);

insert into JURISDB_JUDICIAL.Documento(idDocumento, idTipoDocumento, descripcion, activo, borrado)  values
(1, 1, 'AUTO ADMISORIO', 1, 0),
(2, 1, 'AUTO CONCENTIDA SENTENCIA', 1, 0),
(3, 1, 'AUTO DE REBELDÍA', 1, 0),
(4, 1, 'AUTO DE REMISIÓN A LA FISCALÍA', 1, 0),
(5, 1, 'AUTO QUE APRUEBA LIQUIDACIÓN', 1, 0),
(6, 2, 'DECRETO ORDENANDO SE PRACTIQUE LIQUIDACIÓN', 1, 0),
(7, 2, 'DECRETO PROVEYENDO APERSONAMIENTO', 1, 0),
(8, 2, 'DECRETO PROVEYENDO OFICIO DE LA SUNAT', 1, 0),
(9, 2, 'DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS', 1, 0),
(10, 3, 'CONTESTACIÓN DE DEMANDA', 1, 0),
(11, 3, 'INADMISIBLE', 1, 0),
(12, 4, 'OFICIO A LA RENIEC', 1, 0),
(13, 4, 'OFICIO A LA SUNAT', 1, 0),
(14, 4, 'OFICIO BANCO DE LA NACIÓN', 1, 0),
(15, 4, 'OFICIO SUNARP', 1, 0);

insert into JURISDB_JUDICIAL.Documento(idDocumento, idTipoDocumento, descripcion, activo, borrado)  values
(16, 5, 'AUTO ADMISORIO', 1, 0),
(17, 5, 'AUTO CONCENTIDA SENTENCIA', 1, 0),
(18, 5, 'AUTO DE REBELDÍA', 1, 0),
(19, 5, 'AUTO DE REMISIÓN A LA FISCALÍA', 1, 0),
(20, 5, 'AUTO QUE APRUEBA LIQUIDACIÓN', 1, 0),
(21, 6, 'DECRETO ORDENANDO SE PRACTIQUE LIQUIDACIÓN', 1, 0),
(22, 6, 'DECRETO PROVEYENDO APERSONAMIENTO', 1, 0),
(23, 6, 'DECRETO PROVEYENDO OFICIO DE LA SUNAT', 1, 0),
(24, 6, 'DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS', 1, 0),
(25, 7, 'CONTESTACIÓN DE DEMANDA', 1, 0),
(26, 7, 'INADMISIBLE', 1, 0),
(27, 8, 'OFICIO A LA RENIEC', 1, 0),
(28, 8, 'OFICIO A LA SUNAT', 1, 0),
(29, 8, 'OFICIO BANCO DE LA NACIÓN', 1, 0),
(30, 8, 'OFICIO SUNARP', 1, 0);

insert into JURISDB_JUDICIAL.Documento(idDocumento, idTipoDocumento, descripcion, activo, borrado)  values
(31, 9, 'AUTO ADMISORIO', 1, 0),
(32, 9, 'AUTO CONCENTIDA SENTENCIA', 1, 0),
(33, 9, 'AUTO DE REBELDÍA', 1, 0),
(34, 9, 'AUTO DE REMISIÓN A LA FISCALÍA', 1, 0),
(35, 9, 'AUTO QUE APRUEBA LIQUIDACIÓN', 1, 0),
(36, 10, 'DECRETO ORDENANDO SE PRACTIQUE LIQUIDACIÓN', 1, 0),
(37, 10, 'DECRETO PROVEYENDO APERSONAMIENTO', 1, 0),
(38, 10, 'DECRETO PROVEYENDO OFICIO DE LA SUNAT', 1, 0),
(39, 10, 'DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS', 1, 0),
(40, 11, 'CONTESTACIÓN DE DEMANDA', 1, 0),
(41, 11, 'INADMISIBLE', 1, 0),
(42, 12, 'OFICIO A LA RENIEC', 1, 0),
(43, 12, 'OFICIO A LA SUNAT', 1, 0),
(44, 12, 'OFICIO BANCO DE LA NACIÓN', 1, 0),
(45, 12, 'OFICIO SUNARP', 1, 0);