-- Template OFICIO SUNARP
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(15,'template_auto_15', 'OFICIO SUNARP','',1,0);

-- Secciones del Template OFICIO SUNARP
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section2', '${top.ciudad} ${top.fecha}', '', 0, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section3', 'OFICIO N° ${title.oficio}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section4', 'Señor', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section5', 'JEFE DE LA SUPERINTENDENCIA NACIONAL DE LOS REGISTROS PUBLICOS (SUNARP) - SEDE HUARAZ', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section6', 'PRESENTE.', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section7', 'Tengo el agrado de dirigirme a Ud., con la finalidad de solicitarle se sirva informar a este despacho sobre los bienes muebles e inmuebles que posee el demandado', '', 1, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section8', '${body.main.nombre.demandado},', '', 0, 1, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section9', 'se requiere en el proceso seguido por ${body.main.nombre.demandante} sobre alimentos con el referido demandado.', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section10', 'Exp. N°${body.main.expediente}.', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section11', 'Aprovecho la oportunidad para testimoniarle las muestras de mi especial estima y consideración personal.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(15, 'section12', 'Atentamente;', '', 1, 0, 0, 1, 1, 0);