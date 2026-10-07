-- =====================================================================
-- Generación de documentos por plantilla Word completa (.docx con
-- variables ${nombre}). Convive con el flujo por secciones
-- (Templates / SectionTemplates), que no se modifica.
-- Reutiliza TipoDocumento y Documento: cada Documento tiene como máximo
-- una PlantillaDocumento no borrada.
--   PlantillaDocumento : metadata de la plantilla
--   PlantillaArchivo   : binario .docx vigente (1 a 1, misma PK)
--   PlantillaVariable  : variables del Word y su origen (SISTEMA/MANUAL/IA)
-- =====================================================================

CREATE TABLE If Not Exists JURISDB_JUDICIAL.PlantillaDocumento (
  idPlantilla bigint primary key not null auto_increment,
  idDocumento int not null Comment 'ID de Documento (JURISDB_JUDICIAL.Documento)',
  codigo varchar(50) not null Comment 'Codigo unico de la plantilla',
  nombreOut varchar(150) default Null Comment 'Nombre del archivo de salida, sin extension',
  descripcion varchar(250) default Null Comment 'Descripcion de la plantilla',
  nombreArchivo varchar(255) default Null Comment 'Nombre original del .docx cargado',
  tamanioBytes bigint default Null Comment 'Tamanio del .docx en bytes',
  hashArchivo char(64) default Null Comment 'SHA-256 del .docx',
  version int default 1 Comment 'Version del archivo: se incrementa al reemplazar el .docx',
  corregirIA tinyint default 1 Comment '1: los parrafos con variables se envian a la IA para correccion gramatical',
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
COMMENT = 'Plantillas Word completas para la generacion de documentos por plantilla';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.PlantillaDocumento
    ADD INDEX pd_idDocumentoIDX (idDocumento),
    ADD INDEX pd_codigoIDX (codigo),
    ADD INDEX pd_regDateIDX (regDate),
    ADD INDEX pd_updDateIDX (updDate),
    ADD INDEX pd_activoIDX (activo),
    ADD INDEX pd_borradoIDX (borrado),
    ADD CONSTRAINT fk_PlantillaDocumento_Documento FOREIGN KEY (idDocumento) REFERENCES JURISDB_JUDICIAL.Documento (idDocumento);


CREATE TABLE If Not Exists JURISDB_JUDICIAL.PlantillaArchivo (
  idPlantilla bigint primary key not null Comment 'ID de Plantilla (misma PK que PlantillaDocumento)',
  archivo longblob not null Comment 'Contenido binario del .docx vigente',
  regUserId bigint Null Comment 'Usuario create',
  regDate date Null Comment 'Fecha create',
  regDatetime datetime Null Comment 'Fecha Hora create',
  regTimestamp bigint Null Comment 'Epoch create',
  updUserId bigint Null Comment 'Usuario Update',
  updDate date Null Comment 'Fecha Update',
  updDatetime datetime Null Comment 'Fecha Hora Update',
  updTimestamp bigint Null Comment 'Epoch Update',
  CONSTRAINT fk_PlantillaArchivo_PlantillaDocumento FOREIGN KEY (idPlantilla) REFERENCES JURISDB_JUDICIAL.PlantillaDocumento (idPlantilla)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Archivo .docx vigente de cada plantilla';


CREATE TABLE If Not Exists JURISDB_JUDICIAL.PlantillaVariable (
  idVariable bigint primary key not null auto_increment,
  idPlantilla bigint not null Comment 'ID de Plantilla',
  nombre varchar(100) not null Comment 'Nombre de la variable en el Word, sin ${ }',
  descripcion varchar(250) default Null Comment 'Descripcion para el administrador',
  origen varchar(20) not null Comment 'SISTEMA, MANUAL o IA',
  campoSistema varchar(100) default Null Comment 'Clave del catalogo de variables del sistema (vacio = mismo nombre)',
  valorDefecto text Comment 'Valor por defecto (MANUAL, o SISTEMA sin dato en el SIJ)',
  instruccionIA text Comment 'Instruccion de redaccion para la IA (origen IA)',
  detectada tinyint default 1 Comment '1 si la variable esta en el .docx vigente',
  orden int default Null Comment 'Orden de aparicion en el documento',
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
COMMENT = 'Variables de las plantillas Word y origen de su valor';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.PlantillaVariable
    ADD INDEX pv_idPlantillaIDX (idPlantilla),
    ADD INDEX pv_nombreIDX (nombre),
    ADD INDEX pv_origenIDX (origen),
    ADD INDEX pv_activoIDX (activo),
    ADD INDEX pv_borradoIDX (borrado),
    ADD CONSTRAINT fk_PlantillaVariable_PlantillaDocumento FOREIGN KEY (idPlantilla) REFERENCES JURISDB_JUDICIAL.PlantillaDocumento (idPlantilla);
