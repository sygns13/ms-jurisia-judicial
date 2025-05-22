-- Template CONTESTACIÓN DE DEMANDA
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(10,'template_auto_10', 'CONTESTACIÓN DE DEMANDA','',1,0);

-- Secciones del Template CONTESTACIÓN DE DEMANDA
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section2', 'EXPEDIENTE	: ${title.expediente}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section3', 'MATERIA		: ${title.materia}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section4', 'JUEZ			: ${title.juez}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section5', 'ESPECIALISTA	: ${title.especialista}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section6', 'DEMANDADO	: ${title.demandado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section7', 'DEMANDANTE	: ${title.demandante}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section8', 'Resolución Nro.${top.numero}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section9', '${top.ciudad}, ${top.diamesletter}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section10', 'del año ${top.anioletter}. -', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section11', 'AUTOS Y VISTOS:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section12', 'Dado cuenta a la fecha con el escrito de contestación de demanda; y,', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section13', 'CONSIDERANDO: PRIMERO.', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section14', 'Que, mediante la resolución número uno se admite a trámite la demanda, notificándose a las partes;', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section15', 'SEGUNDO.', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section16', 'Que, mediante el escrito que se da cuenta el demandado cumple con absolver la demanda, dentro del plazo legal conforme es de verse de la constancia de notificación que obra de ${body.dos.fojas}, en tanto que ha sido notificado con la resolución número ${body.dos.resolucion.numero} y demás recaudos con fecha ${body.dos.resolucion.fecha}, contestando la demanda el once de diciembre del presente año; consiguientemente', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section17', 'TÉNGASE POR CONTESTADA LA DEMANDA.', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section18', 'dentro del término por el demandado', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section19', '${body.final.demandado}', '', 1, 1, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section20', 'agregándose a los autos los antecedentes; al primer otrosí digo: otórguese las facultades generales de representación a favor del letrado que suscribe el escrito que se da cuenta; al segundo otrosí digo:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section21', 'por señalado su domicilio procesal y casilla electrónica, a', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section22', 'donde se le notificará conforme a ley; al tercer otrosí digo: téngase presente. ', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(10, 'section23', 'Notifíquese.-', '', 1, 1, 0, 1, 1, 0);