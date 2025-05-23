-- Template Auto Admisorio
insert into JURISDB_JUDICIAL.Templates(id, codigo, nombreOut, descripcion, activo, borrado) values
(1,'template_auto_01', 'AUTO ADMISORIO','',1,0);

-- Secciones del Template Auto Admisorio
insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section1', '${title.juzgado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section2', 'EXPEDIENTE	: ${title.expediente}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section3', 'MATERIA		: ${title.materia}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section4', 'JUEZ			: ${title.juez}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section5', 'ESPECIALISTA	: ${title.especialista}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section6', 'DEMANDADO	: ${title.demandado}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section7', 'DEMANDANTE	: ${title.demandante}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section8', 'Resolución Nro.${top.numero}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section9', '${top.ciudad}, ${top.diamesletter}', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section10', 'Del año ${top.anioletter}. -', '', 0, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section11', 'AUTOS y VISTOS: ', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section12', 'En despacho para calificar el postulatorio; y CONSIDERANDO:', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section13', 'De la calificación de la demanda', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section14', 'Primero:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section15', 'Que, los Juzgados de Paz Letrado son competentes para conocer la demanda en los procesos de fijación, aumento, reducción, extinción, o prorrateo de alimentos, sin perjuicio de la cuantía de la pensión, la edad o la prueba sobre el vínculo familiar, salvo que la pretensión alimentaria se proponga accesoriamente a otras pretensiones, conforme a lo dispuesto en el artículo 96° del Código de los Niños y Adolescentes modificado por la ley 28439.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section16', 'Segundo:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section17', 'Que, los artículos 130° y 424° del Código Procesal Civil, señalan los requisitos que toda demanda debe contener, los cuales constituyen los elementos intrínsecos, al lado de éstos existen otros establecidos por el artículo 425° del mismo cuerpo de leyes, cuyo texto establece los documentos que deben acompañarse a la demanda, con la finalidad de dar cumplimiento a los requisitos exigidos para la admisibilidad y procedencia de la misma.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section18', 'Tercero:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section19', 'Que, lo expuesto procedo a calificar la demanda, teniendo en cuenta además lo dispuesto en los artículos IV y VI del Título Preliminar del Código Civil, en el entendido que para ejercitar o contestar una acción es necesario tener legítimo interés económico y moral, así como legitimidad para obrar, presupuestos jurídicos que concurren en el caso de autos.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section20', 'De la realización de audiencia', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section21', 'Cuarto:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section22', 'Que, en el presente caso tratándose de procesos en los que se encuentran inmersos de los derechos de los niños y adolescentes, es necesario proceder con observancia a los principios de economía y celeridad procesal, siendo por lo cual que se encuentra justificado señalar a través de la presente resolución fecha de audiencia;  asimismo, cabe además precisar que el Consejo Ejecutivo del Poder Judicial, ha aprobado de manera oficial la Directiva N° 007-2020-CE-PJ, a través de la Resolución Administrativa N°00167-2020-CE-PJ, de fecha 04 de junio del año en curso, publicado el dieciocho de Junio del 2020 en el diario oficial el “El Peruano”, implementándose de esta forma el  "Proceso Simplificado y Virtual de Pensión de Alimentos para Niñas, Niños y Adolescentes"; estas disposiciones normativas se aplican a los Juzgados de Paz Letrado de las Cortes Superiores de Justicia, en atención a la primacía del interés superior del niño y a la aplicación de mecanismos de celeridad, oralidad y empleo de recursos tecnológicos disponibles; por lo que es factible señalar audiencias virtuales más aun en este tipo de procesos, en los cuales incluso se puede sentencia sin la presencia de las partes.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section23', 'De la realización de audiencia', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section24', 'Quinto:', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section25', 'Resolviendo la solicitud de asignación anticipada solicita en el ${body.quinto.otrosi.demanda}, de conformidad con el artículo 675° del Código Procesal Civil modificado por la Ley 29803 “En el proceso sobre prestación de alimentos procede la medida de asignación anticipada de alimentos cuando es requerida por los ascendientes, por el cónyuge, por los hijos menores con indubitable relación familiar (…). El juez señala el monto de la asignación que el obligado pagará por mensualidades adelantadas, las que serán descontadas de la que se establezca en la sentencia definitiva”; siendo que, en el caso de autos se solicita asignación anticipada a favor de la menor ${body.quinto.menor}, quien acredita vínculo familiar con el demandado, conforme se colige de la partida de nacimiento obrante en autos existiendo la obligación alimentaria  por parte del demandado conforme al inciso 2° del artículo 474° del Código Civil y atendiendo a lo prescrito en el  artículo 612° del Código Procesal Civil en cuanto toda medida cautelar es provisoria, variable e instrumental, cabe asignar un monto de pensión alimenticia acorde a las necesidades alimenticias de la menor, fijándose por lo mismo la asignación anticipada de alimentos de acuerdo a las necesidades de la alimentista y las posibilidades del obligado, quien según refiere la demandante trabaja en la ${body.quinto.empresa}., siendo ello así, de conformidad con el artículo 481° del Código  Civil, cabe proceder con atención a lo solicitado en parte.', '', 0, 0, 1, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section26', 'Por estas consideraciones:', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section27', 'SE RESUELVE:', '', 1, 1, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section28', 'ADMITIR', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section29', 'a trámite la demanda interpuesta por', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section30', '${body.resolve.uno.demandante}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section31', 'quien actúa en representación de su menor ${body.resolve.uno.menor.parentesco} ${body.resolve.uno.hija.demandante} de ${body.resolve.uno.edad.hija} años de edad (Fecha de Nacimiento:${body.resolve.uno.fecha.nacimiento}), contra', '', 0, 0, 1, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section32', '${body.resolve.uno.demandado}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section33', 'sobre', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section34', '${body.resolve.uno.motivo.demanda}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section35', 'en la vía de proceso', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section36', '${body.resolve.uno.tipo.proceso}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section37', 'en consecuencia:', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section38', 'TÉNGASE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section39', 'por señalado su domicilio procesal en el lugar que indica y', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section40', 'casilla electrónica número ${body.resolve.uno.casilla.electronica}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section41', 'donde se le harán llegar las notificaciones con arreglo a Ley.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section42', 'CORRER ', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section43', 'traslado de la demanda al indicado demandado por el plazo de', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section44', 'CINCO DÍAS HÁBILES,', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section45', 'bajo apercibimiento de seguirse el proceso en su rebeldía, debiendo acompañar a su escrito de contestación el anexo especial prescrito por el artículo 565° del Código Procesal Civil, ', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section46', 'bajo apercibimiento', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section47', 'de tenerse por no presentada la absolución.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section48', 'TÉNGASE ', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section49', 'presente los medios probatorios que se adjuntan y', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section50', 'AGRÉGUESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section51', 'a los autos los anexos acompañados.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section52', 'SEÑALAR', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section53', 'fecha para la', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section54', 'AUDIENCIA ÚNICA VIRTUAL', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section55', 'para el día', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section56', '${body.resolve.cuatro.fecha.audiencia}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section57', 'a las', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section58', '${body.resolve.cuatro.hora.letras.audiencia} (${body.resolve.cuatro.hora.audiencia} - hora exacta)', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section59', '-haciéndose presente que la fecha señalada se debe a la carga procesal que mantiene el Juzgado y el rol de audiencias programadas en la agenda judicial electrónica-,', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section60', 'a través del aplicativo', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section61', 'GOOGLE MEET,', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section62', 'debiendo las partes poner a conocimiento del Juzgado su correo electrónico gmail y número telefónico días antes de la audiencia, bajo responsabilidad; en consecuencia,', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section63', 'NOTIFÍQUESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section64', 'a las partes con el enlace', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section65', '${body.resolve.cuatro.link.meets}', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section66', 'para su concurrencia a la audiencia señalada.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section67', 'DECLARAR FUNDADA EN PARTE LA SOLICITUD DE ASIGNACIÓN ANTICIPADA DE ALIMENTOS', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section68', 'a favor de', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section69', '${body.resolve.cinco.demandante}', '', 0, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section70', 'quien actúa en representación de su menor ${body.resolve.cinco.menor.parentesco}', '', 0, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section71', '${body.resolve.cinco.hija.demandante},', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section72', 'y por lo mismo', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section73', 'SEÑÁLESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section74', 'una pensión alimenticia provisional mensual y adelantada ascendente a la suma de', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section75', '${body.resolve.cinco.monto.pension};', '', 0, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section76', 'monto que deberá abonar el demandado a partir de la notificación con la demanda en forma mensual y adelantada mediante certificados de depósito judicial a nombre del juzgado por ante el Banco de la Nación, las que serán descontadas de las que se establezca en la sentencia definitiva, y a fin de garantizar el cumplimiento de la asignación anticipada,', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section77', 'OFICIESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section78', 'al Banco de la Nación a fin de aperturar una cuenta de ahorros a nombre de la demandante; asimismo', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section79', 'OFÍCIESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section80', 'a la Superintendencia Nacional de los Registros Públicos (Sunarp), a fin de que informe a este despacho sobre los bienes muebles e inmuebles  que posee el demandado;', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section81', 'OFÍCIESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section82', 'a la Superintendencia Nacional de Administración  Tributaria -SUNAT, a fin que informe si el demandado ha formado empresa, la empleadora del demandado, así como la remuneración que percibe, y además informe sobre la declaración de anual de renta de los ultimo cinco años de ser el caso;', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section83', 'OFÍCIESE', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section84', 'a la', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section85', 'RENIEC', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section86', 'a fin que informe sobre el vínculo paterno filial con algún hijo que se encuentra reconocido por el demandado y de ser el caso, cumpla con remitir las actas de nacimiento de todos los hijos del emplazado;', '', 1, 0, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section87', 'notifíquese al demandado', '', 1, 1, 0, 0, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section88', 'además en la dirección que aparece en la Ficha de Reniec que se tiene a la vista.', '', 1, 0, 0, 1, 1, 0);

insert into JURISDB_JUDICIAL.SectionTemplates(idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, activo, borrado) values
(1, 'section89', 'NOTIFÍQUESE. -', '', 1, 1, 0, 1, 1, 0);