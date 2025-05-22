-- Template DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(9,'template_auto_09', 'DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS','',1,0);

-- Secciones del Template DECRETO TENIENDO POR APERTURADA LA CUENTA DE AHORROS
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section2', 'EXPEDIENTE	: ${title.expediente}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section3', 'MATERIA		: ${title.materia}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section4', 'JUEZ			: ${title.juez}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section5', 'ESPECIALISTA	: ${title.especialista}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section6', 'DEMANDADO	: ${title.demandado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section7', 'DEMANDANTE	: ${title.demandante}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section8', 'Resolución Nro.${top.numero}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section9', '${top.ciudad}, ${top.diamesletter}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section10', 'del año ${top.anioletter}. -', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section11', 'Dado cuenta:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section12', 'con el oficio cursado por el Administrador del Banco de la Nación que antecede,  estando a lo informado', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section13', 'TÉNGASE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section14', 'por cumplido el mandato contenido en autos, por aperturado la cuenta de ahorros número ${body.final.cuenta} a nombre de la actora ${body.final.actora} para el presente proceso, póngase a conocimiento del demandado a fin de que cumpla depositar la pensión de alimentos fijada  en la cuenta indicada, bajo responsabilidad en caso de incumplimiento', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(9, 'section15', 'Notifíquese.-', '', 1, 1, 0, 1, 1, 0);