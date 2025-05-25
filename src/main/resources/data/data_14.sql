-- Template OFICIO BANCO DE LA NACIÓN
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(14,'template_auto_14', 'OFICIO BANCO DE LA NACIÓN','',1,0);

-- Secciones del Template OFICIO BANCO DE LA NACIÓN
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section2', '${top.ciudad} ${top.fecha}', '', 0, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section3', 'OFICIO N° ${title.oficio}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section4', 'Señor', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section5', 'ADMINISTRADOR DEL BANCO DE LA NACION', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section6', 'PRESENTE.', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section7', 'Tengo el agrado de dirigirme a Ud., con la finalidad de solicitarle se sirva ordenar a quien corresponda, aperturar una cuenta de ahorros a la orden de la demandante', '', 1, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section8', '${body.main.nombre.demandante},', '', 0, 1, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section9', 'identificada con DNI N°${body.main.dni.demandante}, cuya copia se adjunta a fojas 01 útil, para uso exclusivo de pago y cobro de pensiones de alimentos, debiendo comunicar su cumplimiento de este despacho; se requiere en los seguidos por la indicada demandante con ${body.main.dni.demandado}, sobre alimentos.', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section10', 'Exp. N°${body.main.expediente}.', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section11', 'Aprovecho la oportunidad para testimoniarle las muestras de mi especial estima y consideración personal.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(14, 'section12', 'Atentamente;', '', 1, 0, 0, 1, 1, 0);