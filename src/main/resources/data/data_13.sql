-- Template OFICIO A LA SUNAT
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(13,'template_auto_13', 'OFICIO A LA SUNAT','',1,0);

-- Secciones del Template OFICIO A LA SUNAT
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section2', '${top.ciudad} ${top.fecha}', '', 0, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section3', 'OFICIO N° ${title.oficio}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section4', 'Señor', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section5', 'JEFE DE LA SUPERINTENDENCIA NACIONAL DE ADMINISTRACIÓN TRIBUTARIA - SUNAT', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section6', 'PRESENTE', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section7', 'Tengo a bien, dirigirme a usted  a fin que SOLICITARLE, que informe  a este despacho si el demandado', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section8', '${body.main.nombre.demandado},', '', 0, 1, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section9', 'con DNI N°${body.main.dni.demandado} ha constituido alguna empresa; así como, si se encuentra registrado en la planilla  electrónica de los empleadores, esto es Registro e Información Laboral (T- Registro), y de ser positivo remita a este despacho la planilla mensual de pagos  (PLAME),  asimismo informe respecto la declaración de anual de renta de los ultimo cinco años de ser el caso, dentro del plazo de tres días de recibida la presente comunicación bajo responsabilidad en caso de incumplimiento, se requiere en los seguidos por la demandante ${body.main.demandante}, con el demandado referido demandado, sobre  ${body.main.materia}.', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section10', 'Exp. N°${body.main.expediente}.', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section11', 'Es propicia la oportunidad para expresar, a Ud., las muestras de especial consideración y singular aprecio.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(13, 'section12', 'Atentamente;', '', 1, 0, 0, 1, 1, 0);