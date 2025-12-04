package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.beans.CabDocumentoGenerado;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;
import pj.gob.pe.judicial.model.mysql.entities.Template;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.repository.mysql.DocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.SectionTemplateRepository;
import pj.gob.pe.judicial.repository.mysql.TemplateRepository;
import pj.gob.pe.judicial.repository.mysql.TipoDocumentoRepository;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.service.GenDocumentoService;
import pj.gob.pe.judicial.service.externals.ConsultaiaService;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class GenDocumentoServiceImpl implements GenDocumentoService {

    private final ExpedienteService expedienteService;
    private final ConsultaiaService consultaiaService;
    private final TemplateRepository templateRepository;
    private final SectionTemplateRepository sectionTemplateRepository;
    private final SecurityService securityService;
    private final DocumentoRepository documentoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GenDocumentoServiceImpl.class);

    @Override
    public byte[] generateDocx(String templateCode) throws Exception {

        //Se obtiene el template de base de datos
        Template template = templateRepository.findByCode(templateCode);

        if(template == null){
            throw new Exception("Error al obtener el template: ");
        }

        List<SectionTemplate> sections = sectionTemplateRepository.findTemplateSections(template.getId());

        // 1. Cargar plantilla desde resources
        ClassPathResource resource = new ClassPathResource("templates/" + template.getCodigo()+".docx");

        try (InputStream file = resource.getInputStream()) {
            WordprocessingMLPackage document = WordprocessingMLPackage.load(file);

            MainDocumentPart mainDocumentPart = document.getMainDocumentPart();
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);

            //logger.debug("XML DESPUÉS de VariablePrepare: {}", mainDocumentPart.getXML());

            HashMap<String, String> variables = new HashMap<>();
            sections.forEach(sectionTemplate -> {
                variables.put(sectionTemplate.getCodigo(), sectionTemplate.getContent());
            });

            mainDocumentPart.variableReplace(variables);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            return outputStream.toByteArray();

        }
        catch (Exception e) {
            throw new Exception("Error al generar el documento: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseDocumentHTML generateDocxHTML(Long nUnico, String numIncidente, String templateCode, String SessionId, Long idDocumento) throws Exception {

        String errorValidacion = "";

        if(SessionId == null || SessionId.isEmpty()){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        ResponseLogin responseLogin = securityService.GetSessionData(SessionId);

        if(responseLogin == null || !responseLogin.isSuccess() || !responseLogin.isItemFound() || responseLogin.getUser() == null){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        List<DataExpedienteDTO> expedienteDatos = expedienteService.getDataExpediente(nUnico, numIncidente);

        if(expedienteDatos == null || expedienteDatos.size() == 0) {
            throw new ModeloNotFoundException("Expediente no encontrados");
        }

        //Se obtiene el template de base de datos
        Template template = templateRepository.findByCode(templateCode);

        if(template == null){
            throw new Exception("Error al obtener el template del documento:");
        }

        List<SectionTemplate> sections = sectionTemplateRepository.findTemplateSections(template.getId());

        if(sections == null || sections.size() == 0) {
            throw new ModeloNotFoundException("Secciones de Documento no encontrados");
        }

        switch (templateCode) {
            case "template_auto_01":
                sections = this.ReemplazarSeccionesTemplate1(sections, expedienteDatos);
                break;
            case "template_auto_02":
                sections = this.ReemplazarSeccionesTemplate2(sections, expedienteDatos);
                break;
            case "template_auto_03":
                sections = this.ReemplazarSeccionesTemplate3(sections, expedienteDatos);
                break;
            case "template_auto_04":
                sections = this.ReemplazarSeccionesTemplate4(sections, expedienteDatos);
                break;
            case "template_auto_05":
                sections = this.ReemplazarSeccionesTemplate5(sections, expedienteDatos);
                break;
            case "template_auto_06":
                sections = this.ReemplazarSeccionesTemplate6(sections, expedienteDatos);
                break;
            case "template_auto_07":
                sections = this.ReemplazarSeccionesTemplate7(sections, expedienteDatos);
                break;
            case "template_auto_08":
                sections = this.ReemplazarSeccionesTemplate8(sections, expedienteDatos);
                break;
            case "template_auto_09":
                sections = this.ReemplazarSeccionesTemplate9(sections, expedienteDatos);
                break;
            case "template_auto_10":
                sections = this.ReemplazarSeccionesTemplate10(sections, expedienteDatos);
                break;
            case "template_auto_11":
                sections = this.ReemplazarSeccionesTemplate11(sections, expedienteDatos);
                break;
            case "template_auto_12":
                sections = this.ReemplazarSeccionesTemplate12(sections, expedienteDatos);
                break;
            case "template_auto_13":
                sections = this.ReemplazarSeccionesTemplate13(sections, expedienteDatos);
                break;
            case "template_auto_14":
                sections = this.ReemplazarSeccionesTemplate14(sections, expedienteDatos);
                break;
            case "template_auto_15":
                sections = this.ReemplazarSeccionesTemplate15(sections, expedienteDatos);
                break;
            case "template_auto_16":
                sections = this.ReemplazarSeccionesTemplate16(sections, expedienteDatos);
                break;
            case "template_auto_17":
                sections = this.ReemplazarSeccionesTemplate17(sections, expedienteDatos);
                break;
            case "template_auto_18":
                sections = this.ReemplazarSeccionesTemplate18(sections, expedienteDatos);
                break;
            case "template_auto_19":
                sections = this.ReemplazarSeccionesTemplate19(sections, expedienteDatos);
                break;
            case "template_auto_20":
                sections = this.ReemplazarSeccionesTemplate20(sections, expedienteDatos);
                break;
            case "template_auto_21":
                sections = this.ReemplazarSeccionesTemplate21(sections, expedienteDatos);
                break;
            case "template_auto_22":
                sections = this.ReemplazarSeccionesTemplate22(sections, expedienteDatos);
                break;
            case "template_auto_23":
                sections = this.ReemplazarSeccionesTemplate23(sections, expedienteDatos);
                break;
            case "template_auto_24":
                sections = this.ReemplazarSeccionesTemplate24(sections, expedienteDatos);
                break;
            case "template_auto_25":
                sections = this.ReemplazarSeccionesTemplate25(sections, expedienteDatos);
                break;
            default:
                // Código si no coincide ningún caso
                break;
        }

        ResponseDocument responseDocument = this.SendSectionsIA(sections, nUnico, templateCode, responseLogin);
        sections = responseDocument.getSectionTemplates();

        ResponseDocumentHTML responseDocumentHTML = new ResponseDocumentHTML();

        String responseContentHTML = "";
        boolean saltoLinea = true;

        for(SectionTemplate section : sections) {

            if(saltoLinea){
                responseContentHTML += "<p style='margin-top: 1em; margin-bottom: 1em;'>";
                saltoLinea = false;
            }

            if(section.getIsBold().equals(Constantes.REGISTRO_ACTIVO)){
                responseContentHTML += "<b>";
            }

            responseContentHTML += section.getContent();

            if(section.getIsBold().equals(Constantes.REGISTRO_ACTIVO)){
                responseContentHTML += "</b>";
            }

            if(section.getIsSaltoLinea().equals(Constantes.REGISTRO_ACTIVO)){
                responseContentHTML += "</p>";
                saltoLinea = true;
            }
        }

        responseDocumentHTML.setContentHTML(responseContentHTML);
        responseDocumentHTML.setNUnico(nUnico);
        responseDocumentHTML.setCodeTemplate(templateCode);
        responseDocumentHTML.setSuccess(true);

        //Enviar a Kafka
        //Enviar a Kafka
        CabDocumentoGenerado documentToKafka = this.generarDocumentoToKafka(expedienteDatos, template, responseLogin, "web", idDocumento);

        //Set datos de respuesta del modelo de IA
        documentToKafka.setModel(responseDocument.getModel());
        documentToKafka.setRoleSystem(responseDocument.getRoleSystem());
        documentToKafka.setTemperature(responseDocument.getTemperature());
        documentToKafka.setObject(responseDocument.getObject());
        documentToKafka.setModelResponse(responseDocument.getModelResponse());
        documentToKafka.setRoleResponse(responseDocument.getRoleResponse());
        documentToKafka.setLogprobs(responseDocument.getLogprobs());
        documentToKafka.setFinishReason(responseDocument.getFinishReason());
        documentToKafka.setPromptTokens(responseDocument.getPromptTokens());
        documentToKafka.setCompletionTokens(responseDocument.getCompletionTokens());
        documentToKafka.setTotalTokens(responseDocument.getTotalTokens());
        documentToKafka.setCachedTokens(responseDocument.getCachedTokens());
        documentToKafka.setAudioTokens(responseDocument.getAudioTokens());
        documentToKafka.setCompletionReasoningTokens(responseDocument.getCompletionReasoningTokens());
        documentToKafka.setCompletionAudioTokens(responseDocument.getCompletionAudioTokens());
        documentToKafka.setCompletionAceptedTokens(responseDocument.getCompletionAceptedTokens());
        documentToKafka.setCompletionRejectedTokens(responseDocument.getCompletionRejectedTokens());
        documentToKafka.setServiceTier(responseDocument.getServiceTier());
        documentToKafka.setConfigurationsId(responseDocument.getConfigurationsId());
        documentToKafka.setSessionUID(responseDocument.getSessionUID());

        kafkaTemplate.send("judicial-documentos-generado", "key2", documentToKafka);

        return responseDocumentHTML;
    }

    @Override
    public byte[] generateDocx(Long nUnico, String numIncidente, String templateCode, AuxDocument auxDocument, String SessionId, Long idDocumento) throws Exception {

        String errorValidacion = "";

        if(SessionId == null || SessionId.isEmpty()){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        ResponseLogin responseLogin = securityService.GetSessionData(SessionId);

        if(responseLogin == null || !responseLogin.isSuccess() || !responseLogin.isItemFound() || responseLogin.getUser() == null){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        List<DataExpedienteDTO> expedienteDatos = expedienteService.getDataExpediente(nUnico, numIncidente);

        if(expedienteDatos == null || expedienteDatos.size() == 0) {
            throw new ModeloNotFoundException("Expediente no encontrados");
        }

        //Se obtiene el template de base de datos
        Template template = templateRepository.findByCode(templateCode);

        if(template == null){
            throw new Exception("Error al obtener el template del documento:");
        }

        auxDocument.setNombreDoc(template.getNombreOut() + ".docx");

        List<SectionTemplate> sections = sectionTemplateRepository.findTemplateSections(template.getId());

        if(sections == null || sections.size() == 0) {
            throw new ModeloNotFoundException("Secciones de Documento no encontrados");
        }

        switch (templateCode) {
            case "template_auto_01":
                sections = this.ReemplazarSeccionesTemplate1(sections, expedienteDatos);
            break;
            case "template_auto_02":
                sections = this.ReemplazarSeccionesTemplate2(sections, expedienteDatos);
                break;
            case "template_auto_03":
                sections = this.ReemplazarSeccionesTemplate3(sections, expedienteDatos);
                break;
            case "template_auto_04":
                sections = this.ReemplazarSeccionesTemplate4(sections, expedienteDatos);
                break;
            case "template_auto_05":
                sections = this.ReemplazarSeccionesTemplate5(sections, expedienteDatos);
                break;
            case "template_auto_06":
                sections = this.ReemplazarSeccionesTemplate6(sections, expedienteDatos);
                break;
            case "template_auto_07":
                sections = this.ReemplazarSeccionesTemplate7(sections, expedienteDatos);
                break;
            case "template_auto_08":
                sections = this.ReemplazarSeccionesTemplate8(sections, expedienteDatos);
                break;
            case "template_auto_09":
                sections = this.ReemplazarSeccionesTemplate9(sections, expedienteDatos);
                break;
            case "template_auto_10":
                sections = this.ReemplazarSeccionesTemplate10(sections, expedienteDatos);
                break;
            case "template_auto_11":
                sections = this.ReemplazarSeccionesTemplate11(sections, expedienteDatos);
                break;
            case "template_auto_12":
                sections = this.ReemplazarSeccionesTemplate12(sections, expedienteDatos);
                break;
            case "template_auto_13":
                sections = this.ReemplazarSeccionesTemplate13(sections, expedienteDatos);
                break;
            case "template_auto_14":
                sections = this.ReemplazarSeccionesTemplate14(sections, expedienteDatos);
                break;
            case "template_auto_15":
                sections = this.ReemplazarSeccionesTemplate15(sections, expedienteDatos);
                break;
            case "template_auto_16":
                sections = this.ReemplazarSeccionesTemplate16(sections, expedienteDatos);
                break;
            case "template_auto_17":
                sections = this.ReemplazarSeccionesTemplate17(sections, expedienteDatos);
                break;
            case "template_auto_18":
                sections = this.ReemplazarSeccionesTemplate18(sections, expedienteDatos);
                break;
            case "template_auto_19":
                sections = this.ReemplazarSeccionesTemplate19(sections, expedienteDatos);
                break;
            case "template_auto_20":
                sections = this.ReemplazarSeccionesTemplate20(sections, expedienteDatos);
                break;
            case "template_auto_21":
                sections = this.ReemplazarSeccionesTemplate21(sections, expedienteDatos);
                break;
            case "template_auto_22":
                sections = this.ReemplazarSeccionesTemplate22(sections, expedienteDatos);
                break;
            case "template_auto_23":
                sections = this.ReemplazarSeccionesTemplate23(sections, expedienteDatos);
                break;
            case "template_auto_24":
                sections = this.ReemplazarSeccionesTemplate24(sections, expedienteDatos);
                break;
            case "template_auto_25":
                sections = this.ReemplazarSeccionesTemplate25(sections, expedienteDatos);
                break;
            default:
                // Código si no coincide ningún caso
            break;
        }

        ResponseDocument responseDocument = this.SendSectionsIA(sections, nUnico, templateCode, responseLogin);
        sections = responseDocument.getSectionTemplates();

        // 1. Cargar plantilla desde resources
        ClassPathResource resource = new ClassPathResource("templates/" + template.getCodigo()+".docx");

        try (InputStream file = resource.getInputStream()) {
            WordprocessingMLPackage document = WordprocessingMLPackage.load(file);

            MainDocumentPart mainDocumentPart = document.getMainDocumentPart();
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);
            VariablePrepare.prepare(document);

            //logger.debug("XML DESPUÉS de VariablePrepare: {}", mainDocumentPart.getXML());

            HashMap<String, String> variables = new HashMap<>();
            sections.forEach(sectionTemplate -> {
                variables.put(sectionTemplate.getCodigo(), sectionTemplate.getContent());
            });

            mainDocumentPart.variableReplace(variables);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            //Enviar a Kafka
            CabDocumentoGenerado documentToKafka = this.generarDocumentoToKafka(expedienteDatos, template, responseLogin, "doc", idDocumento);

            //Set datos de respuesta del modelo de IA
            documentToKafka.setModel(responseDocument.getModel());
            documentToKafka.setRoleSystem(responseDocument.getRoleSystem());
            documentToKafka.setTemperature(responseDocument.getTemperature());
            documentToKafka.setObject(responseDocument.getObject());
            documentToKafka.setModelResponse(responseDocument.getModelResponse());
            documentToKafka.setRoleResponse(responseDocument.getRoleResponse());
            documentToKafka.setLogprobs(responseDocument.getLogprobs());
            documentToKafka.setFinishReason(responseDocument.getFinishReason());
            documentToKafka.setPromptTokens(responseDocument.getPromptTokens());
            documentToKafka.setCompletionTokens(responseDocument.getCompletionTokens());
            documentToKafka.setTotalTokens(responseDocument.getTotalTokens());
            documentToKafka.setCachedTokens(responseDocument.getCachedTokens());
            documentToKafka.setAudioTokens(responseDocument.getAudioTokens());
            documentToKafka.setCompletionReasoningTokens(responseDocument.getCompletionReasoningTokens());
            documentToKafka.setCompletionAudioTokens(responseDocument.getCompletionAudioTokens());
            documentToKafka.setCompletionAceptedTokens(responseDocument.getCompletionAceptedTokens());
            documentToKafka.setCompletionRejectedTokens(responseDocument.getCompletionRejectedTokens());
            documentToKafka.setServiceTier(responseDocument.getServiceTier());
            documentToKafka.setConfigurationsId(responseDocument.getConfigurationsId());
            documentToKafka.setSessionUID(responseDocument.getSessionUID());

            kafkaTemplate.send("judicial-documentos-generado", "key2", documentToKafka);

            return outputStream.toByteArray();

        }
        catch (Exception e) {
            throw new Exception("Error al generar el documento: " + e.getMessage(), e);
        }
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate1(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String otroSiDigo = "...";
        String nombreMenor = "...";
        String empresaTrabajoDDo = "...";
        String parentestoDteMenor = "...";
        String edadMenor = "...";
        String fecNacimientoMenor = "...";
        String motivoDemanda = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String tipoProceso = "UNICO";
        String casillaElectronica = "...";
        String fechaAudiencia = "...";
        String horaLetrasAudiencia = "...";
        String horaAudiencia = "...";
        String linkMeets = "...";
        String montoPension = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.quinto.otrosi.demanda}", otroSiDigo));
            section.setContent(section.getContent().replace("${body.quinto.menor}", nombreMenor));
            section.setContent(section.getContent().replace("${body.quinto.empresa}", empresaTrabajoDDo));
            section.setContent(section.getContent().replace("${body.resolve.uno.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.resolve.uno.menor.parentesco}", parentestoDteMenor));
            section.setContent(section.getContent().replace("${body.resolve.uno.hija.demandante}", nombreMenor));
            section.setContent(section.getContent().replace("${body.resolve.uno.edad.hija}", edadMenor));
            section.setContent(section.getContent().replace("${body.resolve.uno.fecha.nacimiento}", fecNacimientoMenor));
            section.setContent(section.getContent().replace("${body.resolve.uno.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.resolve.uno.motivo.demanda}", motivoDemanda));
            section.setContent(section.getContent().replace("${body.resolve.uno.tipo.proceso}", tipoProceso));
            section.setContent(section.getContent().replace("${body.resolve.uno.casilla.electronica}", casillaElectronica));
            section.setContent(section.getContent().replace("${body.resolve.cuatro.fecha.audiencia}", fechaAudiencia));
            section.setContent(section.getContent().replace("${body.resolve.cuatro.hora.letras.audiencia}", horaLetrasAudiencia));
            section.setContent(section.getContent().replace("${body.resolve.cuatro.hora.audiencia}", horaAudiencia));
            section.setContent(section.getContent().replace("${body.resolve.cuatro.link.meets}", linkMeets));
            section.setContent(section.getContent().replace("${body.resolve.cinco.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.resolve.cinco.menor.parentesco}", parentestoDteMenor));
            section.setContent(section.getContent().replace("${body.resolve.cinco.hija.demandante}", nombreMenor));
            section.setContent(section.getContent().replace("${body.resolve.cinco.monto.pension}", montoPension));
        }

        return sections;
    }

    private ResponseDocument SendSectionsIA(List<SectionTemplate> sections, Long nUnico, String templateCode, ResponseLogin responseLogin){

        InputDocument inputDocument = new InputDocument();

        inputDocument.setIdUser(responseLogin.getUser().getIdUser());
        inputDocument.setCodeTemplate(templateCode);
        inputDocument.setNUnico(nUnico);
        inputDocument.setSectionTemplates(sections);

        ResponseDocument responseDocument = consultaiaService.ProcessDocument(inputDocument);

        return responseDocument;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate2(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String resolucionBaseNro = "...";
        String resolucionBaseFecha = "...";
        String sentenciaFecha = "...";
        String resolucionConsentidaNro = "...";
        String resolucionConsentidaFecha = "...";
        String fojas = "...";


        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.uno.resolucion.numero}", resolucionBaseNro));
            section.setContent(section.getContent().replace("${body.uno.resolucion.fecha}", resolucionBaseFecha));
            section.setContent(section.getContent().replace("${body.dos.sentencia.fecha}", sentenciaFecha));
            section.setContent(section.getContent().replace("${body.resolve.resolucion.numero}", resolucionConsentidaNro));
            section.setContent(section.getContent().replace("${body.resolve.resolucion.fecha}", resolucionConsentidaFecha));
            section.setContent(section.getContent().replace("${body.resolve.fojas}", fojas));

        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate3(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String resolucionNotificacionNro = "...";
        String fojas = "...";
        String resolucionNotificacionFecha = "...";
        String resolucionApercibimientoNro = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.dos.resolucion.notificacion.numero}", resolucionNotificacionNro));
            section.setContent(section.getContent().replace("${body.dos.fojas}", fojas));
            section.setContent(section.getContent().replace("${body.dos.resolucion.notificacion.fecha}", resolucionNotificacionFecha));
            section.setContent(section.getContent().replace("${body.dos.resolucion.apercibimiento.numero}", resolucionApercibimientoNro));
            section.setContent(section.getContent().replace("${body.resolve.demandado}", demandados));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate4(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String resolucionNotificacionNro = "...";
        String fojas = "...";
        String resolucionNotificacionFecha = "...";
        String resolucionConstanciaNro = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.dos.resolucion.numero}", resolucionNotificacionNro));
            section.setContent(section.getContent().replace("${body.dos.fojas}", fojas));
            section.setContent(section.getContent().replace("${body.tres.resolucion.notificacion.fecha}", resolucionNotificacionFecha));
            section.setContent(section.getContent().replace("${body.tres.resolucion.notificacion.constancias}", resolucionConstanciaNro));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate5(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String solicitudFecha = "...";
        String solicitudFojas = "...";
        String solicitudMonto = "...";
        String liquidacionFojas = "...";
        String liquidacionResolucionNro = "...";
        String liquidacionResolucionFecha = "...";
        String liquidacionMonto = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.uno.solicitud.fecha}", solicitudFecha));
            section.setContent(section.getContent().replace("${body.uno.solicitud.fojas}", solicitudFojas));
            section.setContent(section.getContent().replace("${body.uno.solicitud.monto}", solicitudMonto));
            section.setContent(section.getContent().replace("${body.dos.liquidacion.fojas}", liquidacionFojas));
            section.setContent(section.getContent().replace("${body.resolve.resolucion.numero}", liquidacionResolucionNro));
            section.setContent(section.getContent().replace("${body.resolve.resolucion.fecha}", liquidacionResolucionFecha));
            section.setContent(section.getContent().replace("${body.resolve.monto}", liquidacionMonto));
            section.setContent(section.getContent().replace("${body.resolve.demandado}", demandados));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate6(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate7(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.inicio.demandado}", demandados));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate8(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate9(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String numeroCuenta = "...";
        String actora = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.final.cuenta}", numeroCuenta));
            section.setContent(section.getContent().replace("${body.final.actora}", actora));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate10(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String notificacionFojas = "...";
        String resolucionNotificacionNro = "...";
        String resolucionNotificacionFecha = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.dos.fojas}", notificacionFojas));
            section.setContent(section.getContent().replace("${body.dos.resolucion.numero}", resolucionNotificacionNro));
            section.setContent(section.getContent().replace("${body.dos.resolucion.fecha}", resolucionNotificacionFecha));
            section.setContent(section.getContent().replace("${body.final.demandado}", demandados));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate11(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        String resolucionNro = "...";

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        String resolucionNotificacionFecha = "...";
        String resolucionAdministrativaNro = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.uno.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.uno.notificacion.fecha}", resolucionNotificacionFecha));
            section.setContent(section.getContent().replace("${body.dos.resolucion}", resolucionAdministrativaNro));
            section.setContent(section.getContent().replace("${body.final.demandado}", demandados));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate12(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String fechaLetter = ahora.format(fecha);
        String oficioNro = "...";


        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fechaLetter));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate13(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";

        String demandantes;
        String demandados;
        String dniDemandados;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        StringJoiner dnisDDO = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    dnisDDO.add(dniParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();
        dniDemandados = dnisDDO.toString();

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String fechaLetter = ahora.format(fecha);
        String oficioNro = "...";


        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fechaLetter));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.dni.demandado}", dniDemandados));
            section.setContent(section.getContent().replace("${body.main.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.materia}", materia));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate14(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";

        String demandantes;
        String demandados;
        String dniDemandados;
        String dniDemandantes;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        StringJoiner dnisDDO = new StringJoiner(" , ");
        StringJoiner dnisDTE = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    dnisDDO.add(dniParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dnisDTE.add(dniParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();
        dniDemandados = dnisDDO.toString();
        dniDemandantes = dnisDTE.toString();

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String fechaLetter = ahora.format(fecha);
        String oficioNro = "...";


        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fechaLetter));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.dni.demandante}", dniDemandantes));
            section.setContent(section.getContent().replace("${body.main.dni.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate15(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";

        String demandantes;
        String demandados;
        String dniDemandados;
        String dniDemandantes;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        StringJoiner dnisDDO = new StringJoiner(" , ");
        StringJoiner dnisDTE = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    dnisDDO.add(dniParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dnisDTE.add(dniParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();
        dniDemandados = dnisDDO.toString();
        dniDemandantes = dnisDTE.toString();

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String fechaLetter = ahora.format(fecha);
        String oficioNro = "...";


        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fechaLetter));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate16(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        // Obtener la fecha actual
        LocalDate ahora = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'Del año' yyyy", new Locale("es", "PE"));

        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        // Formatear la fecha
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);
        String fechaLetter = ahora.format(fecha);

        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String resolucionNro = "...";
        String oficioNro = "...";
        String fojasexp = "...";

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fechaLetter));

            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));

            section.setContent(section.getContent().replace("${body.main.fojas}", fojasexp));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
            section.setContent(section.getContent().replace("${body.main.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.resolucion}", resolucionNro));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate17(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String resolucion = "...";
        String oficioNro = "...";
        String nombreMenor = "...";
        String edadMenor = "...";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));
        String ciudad = "Huaraz";
        String fecha = ahora.format(formatoFecha);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fecha));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.menor}", nombreMenor));
            section.setContent(section.getContent().replace("${body.main.edad.menor}", edadMenor));
            section.setContent(section.getContent().replace("${body.main.resolucion}", resolucion));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate18(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String oficioNro = "...";

        String demandantes;
        String demandados;
        String dniDemandado = "...";

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dniDemandado = dniParte;
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));
        String ciudad = "Huaraz";
        String fecha = ahora.format(formatoFecha);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fecha));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.demandado.dni}", dniDemandado));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate19(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String oficioNro = "...";

        String demandantes;
        String demandados;
        String dniDemandado = "...";

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dniDemandado = dniParte;
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));
        String ciudad = "Huaraz";
        String fecha = ahora.format(formatoFecha);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fecha));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.demandado.dni}", dniDemandado));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate20(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String oficioNro = "...";

        String demandantes;
        String demandados;
        String dniDemandante = "...";

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    dniDemandante = dniParte;
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));
        String ciudad = "Huaraz";
        String fecha = ahora.format(formatoFecha);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fecha));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.demandante.dni}", dniDemandante));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate21(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String corte = "CORTE SUPERIOR DE JUSTICIA DE ANCASH";
        String nombreanio = "AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA";
        String oficioNro = "...";

        String demandantes;
        String demandados;
        String dniDemandado = "...";

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dniDemandado = dniParte;
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "PE"));
        String ciudad = "Huaraz";
        String fecha = ahora.format(formatoFecha);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.corte}", corte));
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.nombreanio}", nombreanio));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.fecha}", fecha));
            section.setContent(section.getContent().replace("${title.oficio}", oficioNro));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.demandado.dni}", dniDemandado));
            section.setContent(section.getContent().replace("${body.main.nombre.demandante}", demandantes));
            section.setContent(section.getContent().replace("${body.main.expediente}", expediente));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate22(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";
        String fiscalia = "";

        String resolucionNro = "...";
        String resolucionRef = "...";
        String resolucionFecha = "...";
        String tiempoTranscurrido = "...";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;
            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.fiscalia}", fiscalia));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.main.tiempotranscurrido}", tiempoTranscurrido));
            section.setContent(section.getContent().replace("${body.main.resolucion}", resolucionRef));
            section.setContent(section.getContent().replace("${body.main.resolucionfecha}", resolucionFecha));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate23(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";
        String fiscalia = "...";

        String resolucionNro = "...";
        String fechaEscrito = "...";
        String anioEscrito = "...";
        String nroResolucionLetras = "...";
        String fojasResolucion = "...";
        String fechaResolucionConstancia = "...";
        String rangoFojas = "...";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;
            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.fiscalia}", fiscalia));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.main.fechaescrito}", fechaEscrito));
            section.setContent(section.getContent().replace("${body.main.anioescrito}", anioEscrito));
            section.setContent(section.getContent().replace("${body.main.nroresolucionletras}", nroResolucionLetras));
            section.setContent(section.getContent().replace("${body.main.fojasresolucion}", fojasResolucion));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.fecharestosconstancias}", fechaResolucionConstancia));
            section.setContent(section.getContent().replace("${body.main.rangofojas}", rangoFojas));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate24(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";
        String abogado = "...";
        String fiscalia = "...";

        String resolucionNro = "...";
        String nroResolucionLetras = "...";
        String resolucionFecha = "...";
        String tiempoAcumulado = "...";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;
            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.abogado}", abogado));
            section.setContent(section.getContent().replace("${title.fiscalia}", fiscalia));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.main.nroresolucionletras}", nroResolucionLetras));
            section.setContent(section.getContent().replace("${body.main.resolucionfecha}", resolucionFecha));
            section.setContent(section.getContent().replace("${body.main.tiempoacumulado}", tiempoAcumulado));
        }

        return sections;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate25(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos) {

        String juzgado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String expediente = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String especialista = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSecretario() : "";
        String fiscalia = "";
        String perito = "...";

        String resolucionNro = "...";
        String nroResolucionLetras = "...";
        String fechaResolucionLetras = "...";
        String fojasResolucion = "...";
        String montoLetras = "...";
        String montoNumero = "...";
        String plazoDias = "...";

        String demandantes;
        String demandados;

        StringJoiner dtes = new StringJoiner(", ");
        StringJoiner ddos = new StringJoiner(", ");

        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;
            String nombreParte = dto.getNombreParte().trim();

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    break;
            }
        }

        demandantes = dtes.toString();
        demandados = ddos.toString();

        LocalDate ahora = LocalDate.now();
        DateTimeFormatter formatoLargo = DateTimeFormatter.ofPattern("dd 'de' MMMM", new Locale("es", "PE"));
        DateTimeFormatter formatoAnio = DateTimeFormatter.ofPattern("'del año' yyyy", new Locale("es", "PE"));

        String ciudad = "Huaraz";
        String diamesletter = ahora.format(formatoLargo);
        String anioletter = ahora.format(formatoAnio);

        for (SectionTemplate section : sections) {
            section.setContent(section.getContent().replace("${title.juzgado}", juzgado));
            section.setContent(section.getContent().replace("${title.expediente}", expediente));
            section.setContent(section.getContent().replace("${title.materia}", materia));
            section.setContent(section.getContent().replace("${title.juez}", juez));
            section.setContent(section.getContent().replace("${title.especialista}", especialista));
            section.setContent(section.getContent().replace("${title.fiscalia}", fiscalia));
            section.setContent(section.getContent().replace("${title.perito}", perito));
            section.setContent(section.getContent().replace("${title.demandado}", demandados));
            section.setContent(section.getContent().replace("${title.demandante}", demandantes));
            section.setContent(section.getContent().replace("${top.numero}", resolucionNro));
            section.setContent(section.getContent().replace("${top.ciudad}", ciudad));
            section.setContent(section.getContent().replace("${top.diamesletter}", diamesletter));
            section.setContent(section.getContent().replace("${top.anioletter}", anioletter));
            section.setContent(section.getContent().replace("${body.main.nroresolucionletras}", nroResolucionLetras));
            section.setContent(section.getContent().replace("${body.main.fechasresolucionletras}", fechaResolucionLetras));
            section.setContent(section.getContent().replace("${body.main.fojasresolucion}", fojasResolucion));
            section.setContent(section.getContent().replace("${body.main.nombre.demandado}", demandados));
            section.setContent(section.getContent().replace("${body.main.montoletras}", montoLetras));
            section.setContent(section.getContent().replace("${body.main.montonumero}", montoNumero));
            section.setContent(section.getContent().replace("${body.main.plazodias}", plazoDias));
        }

        return sections;
    }


    private CabDocumentoGenerado generarDocumentoToKafka(List<DataExpedienteDTO> expedienteDatos, Template template, ResponseLogin responseLogin, String typeDoc, Long idDocumento){

        String materia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescMateria() : "";
        String codSede = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getCodSede() : "";
        String codInstancia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getCodInstancia() : "";
        String codEspecialidad = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getEspecialidad() : "";
        String codMateria = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getCodMateria() : "";
        String sede = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreSede() : "";
        String instancia = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreInstancia() : "";
        String especialidad = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreEspecialidad() : "";
        Long nUnico = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNUnico() : 0L;
        String numeroExp = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNumero() : "";
        String yearExp = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getYear() : "";
        String xFormato = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getFormato() : "";
        String ubicacion = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescUbicacion() : "";
        String juez = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getNombreJuez() : "";
        String estado = expedienteDatos.get(0) != null ? expedienteDatos.get(0).getDescEstado() : "";

        Documento doc = documentoRepository.getById(idDocumento);
        TipoDocumento tipoDoc = tipoDocumentoRepository.getById(doc.getIdTipoDocumento());

        String demandantes;
        String demandados;
        String dniDemandados;
        String dniDemandantes;

        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        StringJoiner dnisDDO = new StringJoiner(" , ");
        StringJoiner dnisDTE = new StringJoiner(" , ");
        for (DataExpedienteDTO dto : expedienteDatos) {
            if (dto.getTipoParteCodigo() == null || dto.getNombreParte() == null) continue;

            String nombreParte = dto.getNombreParte().trim();
            String dniParte = dto.getDniParte() != null ? dto.getDniParte().trim() : "";

            switch (dto.getTipoParteCodigo()) {
                case "DTE":
                    dtes.add(nombreParte);
                    dnisDDO.add(dniParte);
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    dnisDTE.add(dniParte);
                    break;
                default:
                    // Ignorar otros tipos
                    break;
            }
        }
        demandantes = dtes.toString();
        demandados = ddos.toString();
        dniDemandados = dnisDDO.toString();
        dniDemandantes = dnisDTE.toString();

        // Obtener la fecha actual
        //LocalDate ahora = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        CabDocumentoGenerado cabDocumentoGenerado = new CabDocumentoGenerado();

        cabDocumentoGenerado.setId(null); // ID será generado por la base de datos
        cabDocumentoGenerado.setUserId(responseLogin.getUser().getIdUser());
        cabDocumentoGenerado.setTypedoc(typeDoc);
        cabDocumentoGenerado.setCodSede(codSede);
        cabDocumentoGenerado.setCodInstancia(codInstancia);
        cabDocumentoGenerado.setCodEspecialidad(codEspecialidad);
        cabDocumentoGenerado.setCodMateria(codMateria);
        cabDocumentoGenerado.setSede(sede);
        cabDocumentoGenerado.setInstancia(instancia);
        cabDocumentoGenerado.setEspecialidad(especialidad);
        cabDocumentoGenerado.setMateria(materia);
        cabDocumentoGenerado.setCodNumero(numeroExp);
        cabDocumentoGenerado.setCodYear(yearExp);
        cabDocumentoGenerado.setIdDocumento(doc.getIdDocumento());
        cabDocumentoGenerado.setIdTipoDocumento(tipoDoc.getIdTipoDocumento());
        cabDocumentoGenerado.setTipoDocumento(tipoDoc.getDescripcion());
        cabDocumentoGenerado.setDocumento(doc.getDescripcion());
        cabDocumentoGenerado.setNUnico(nUnico);
        cabDocumentoGenerado.setXFormato(xFormato);
        cabDocumentoGenerado.setUbicacion(ubicacion);
        cabDocumentoGenerado.setJuez(juez);
        cabDocumentoGenerado.setEstado(estado);
        cabDocumentoGenerado.setDniDemandante(dniDemandantes);
        cabDocumentoGenerado.setDemandante(demandantes);
        cabDocumentoGenerado.setDniDemandado(dniDemandados);
        cabDocumentoGenerado.setDemandado(demandados);
        cabDocumentoGenerado.setTemplateCode(template.getCodigo());
        cabDocumentoGenerado.setTemplateID(template.getId());
        cabDocumentoGenerado.setTemplateNombreOut(template.getNombreOut());

        cabDocumentoGenerado.setRegDate(ahora.toLocalDate());
        cabDocumentoGenerado.setRegDatetime(ahora);
        cabDocumentoGenerado.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));

        return cabDocumentoGenerado;
    }


}
