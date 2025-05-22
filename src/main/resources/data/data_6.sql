-- Template Decreto Ordenando Liquidación
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(6,'template_auto_06', 'DECRETO ORDENANDO SE PRACTIQUE LIQUIDACIÓN','',1,0);

-- Secciones del Template Decreto Ordenando Liquidación
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section2', 'EXPEDIENTE	: ${title.expediente}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section3', 'MATERIA		: ${title.materia}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section4', 'JUEZ			: ${title.juez}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section5', 'ESPECIALISTA	: ${title.especialista}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section6', 'DEMANDADO	: ${title.demandado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section7', 'DEMANDANTE	: ${title.demandante}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section8', 'Resolución Nro.${top.numero}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section9', '${top.ciudad}, ${top.diamesletter}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section10', 'Del año ${top.anioletter}. -', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section11', 'Dado cuenta:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section12', 'en la fecha con oficio cursado por el Administrador del Banco de la Nación que antecede, habiendo remitido el reporte sobre el estado de cuenta de la accionante', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section13', 'TÉNGASE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section14', 'por cumplido el mandato contenido en autos, en consecuencia se procede a proveer conforme a ley lo solicitado por la actora; estando a lo peticionado de conformidad a lo establecido por el artículo 568° del Código Procesal Civil;', '', 0, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section15', 'PRACTÍQUESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section16', 'por secretaria la liquidación de las pensiones alimenticias devengadas en base a la propuesta de liquidación de la demandante.', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(6, 'section17', 'Notifíquese.-', '', 1, 1, 0, 1, 1, 0);