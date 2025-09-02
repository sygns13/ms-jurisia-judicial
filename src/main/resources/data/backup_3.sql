CREATE TABLE If Not Exists JURISDB_JUDICIAL.Sedes (
    idSede char(10) not Null Comment 'ID de Sede',
    nombre char(150) default Null Comment 'Nombre de la Sede',
    direccion char(150) default Null Comment 'Direccion de la Sede',
    codDistrito char(10) default Null Comment 'Codigo de Distrito',
    regUserId bigint Null Comment 'Usuario create',
    regDate date Null Comment 'Fecha create',
    regDatetime datetime Null Comment 'Fecha Hora create',
    regTimestamp bigint Null Comment 'Epoch create',
    updUserId bigint Null Comment 'Usuario Update',
    updDate date Null Comment 'Fecha Update',
    updDatetime datetime Null Comment 'Fecha Hora Update',
    updTimestamp bigint Null Comment 'Epoch Update',
    activo tinyint null,
    borrado tinyint null,
    primary key (idSede)
    )
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci,
    COMMENT = 'Tabla de Sedes Judiciales Activas en el APUBOT';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.Sedes
    ADD INDEX codDistritoIDX (codDistrito),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);


CREATE TABLE If Not Exists JURISDB_JUDICIAL.Instancias (
    idInstancia char(10) not Null Comment 'ID de Instancia',
    codDistrito char(10) default Null Comment 'Codigo de Distrito',
    codProvincia char(10) default Null Comment 'Codigo de Provincia',
    codOrganoJurisdiccional char(10) default Null Comment 'Codigo de Organo Jurisdiccional',
    nombre char(150) default Null Comment 'Descripcion Tipo de Documento',
    numInstancia integer default Null Comment 'Numero de Instancia',
    ubicacion char(150) default Null Comment 'Ubicacion Fisica de la Instancia',
    nombreCorto char(10) default Null Comment 'Nombre Corto de la Instancia',
    idSede char(10) default Null Comment 'ID de Sede',
    codUbigeo char(10) default Null Comment 'Codigo de Ubigeo',
    regUserId bigint Null Comment 'Usuario create',
    regDate date Null Comment 'Fecha create',
    regDatetime datetime Null Comment 'Fecha Hora create',
    regTimestamp bigint Null Comment 'Epoch create',
    updUserId bigint Null Comment 'Usuario Update',
    updDate date Null Comment 'Fecha Update',
    updDatetime datetime Null Comment 'Fecha Hora Update',
    updTimestamp bigint Null Comment 'Epoch Update',
    activo tinyint null,
    borrado tinyint null,
    primary key (idInstancia)
    )
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci,
    COMMENT = 'Tabla de Instancias Judiciales Activas en el APUBOT';
-- Indexacion
ALTER TABLE JURISDB_JUDICIAL.Instancias
    ADD INDEX codDistritoIDX (codDistrito),
    ADD INDEX codProvinciaIDX (codProvincia),
    ADD INDEX codOrganoJurisdiccionalIDX (codOrganoJurisdiccional),
    ADD INDEX numInstanciaIDX (numInstancia),
    ADD INDEX nombreCortoIDX (nombreCorto),
    ADD INDEX idSedeIDX (idSede),
    ADD INDEX codUbigeoIDX (codUbigeo),
    ADD INDEX regUserIdIDX (regUserId),
    ADD INDEX regDateIDX (regDate),
    ADD INDEX regTimestampIDX (regTimestamp),
    ADD INDEX updUserIdIDX (updUserId),
    ADD INDEX updDateIDX (updDate),
    ADD INDEX updTimestampIDX (updTimestamp),
    ADD INDEX activoIDX (activo),
    ADD INDEX borradoIDX (borrado);