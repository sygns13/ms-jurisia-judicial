package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;
import pj.gob.pe.judicial.model.mysql.entities.Template;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.repository.mysql.DocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.SectionTemplateRepository;
import pj.gob.pe.judicial.repository.mysql.TemplateRepository;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.service.GenDocumentoService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenDocumentoServiceImpl implements GenDocumentoService {

    private final ExpedienteService expedienteService;
    private final TemplateRepository templateRepository;
    private final SectionTemplateRepository sectionTemplateRepository;
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

            logger.debug("XML DESPUÉS de VariablePrepare: {}", mainDocumentPart.getXML());

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
    public byte[] generateDocx(Long nUnico, String templateCode) throws Exception {

        List<DataExpedienteDTO> expedienteDatos = expedienteService.getDataExpediente(nUnico);

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


        return null;
    }

    private List<SectionTemplate> ReemplazarSeccionesTemplate1(List<SectionTemplate> sections, List<DataExpedienteDTO> expedienteDatos){

        return sections;
    }
}
