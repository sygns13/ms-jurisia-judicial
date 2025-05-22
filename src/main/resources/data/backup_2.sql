CREATE TABLE If Not Exists JURISDB_JUDICIAL.Templates (
  id int primary key not null auto_increment,
  codigo char(50) default Null Comment 'Codigo del Template - el codigo corresponde al nombre del archivo fisico del template',
  nombreOut char(100) default null Comment 'nombre de salida del template',
  descripcion char(200) default Null Comment 'Descripcion del template',
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
COMMENT = 'Tabla de Maestro de templates';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.Templates
    ADD INDEX codigoIDX (codigo),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);


CREATE TABLE If Not Exists JURISDB_JUDICIAL.SectionTemplates (
  id int primary key not null auto_increment,
  idTemplate int default null Comment 'Id de Template Padre',
  codigo char(50) default Null Comment 'Codigo de la sección que se reemplazará en el documento',
  content text default Null Comment 'Contenido de la Sección',
  descripcion char(200) default Null Comment 'Descripcion opcional',
  isFinal tinyint default null Comment 'Si es final su valor se reemplaza directamente en el documento',
  isBold tinyint default null Comment 'Si es bold para retornar al frontend',
  isSendIA tinyint default null comment 'Indicador si se va a enviar a la IA para revisión',
  isSaltoLinea tinyint default null comment 'Indicador si luego de pintarlo se hace un salto de línea para retornar al frontend',
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
COMMENT = 'Tabla de Secciones de Templates';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.SectionTemplates
	ADD INDEX idTemplateIDX (idTemplate),
    ADD INDEX codigoIDX (codigo),
    ADD INDEX isFinalIDX (isFinal),
    ADD INDEX isBoldIDX (isBold),
    ADD INDEX isSendIAIDX (isSendIA),
    ADD INDEX isSaltoLineaIDX (isSaltoLinea),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);

CREATE TABLE If Not Exists JURISDB_JUDICIAL.SectionVars (
  id int primary key not null auto_increment,
  idSectionTemplate int default null Comment 'Id de Seccion Padre',
  codigo char(50) default Null Comment 'Codigo de la variable que se reemplazará en la sección',
  descripcion char(200) default Null Comment 'Descripcion opcional',
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
COMMENT = 'Tabla de Secciones de Templates';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.SectionVars
	ADD INDEX idSectionTemplateIDX (idSectionTemplate),
    ADD INDEX codigoIDX (codigo),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);


