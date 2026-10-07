package pj.gob.pe.judicial.service.mysql.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pj.gob.pe.judicial.exception.ValidationServiceException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaArchivo;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;
import pj.gob.pe.judicial.model.mysql.entities.Template;
import pj.gob.pe.judicial.repository.mysql.DocumentoAdminRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaArchivoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaDocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaVariableRepository;
import pj.gob.pe.judicial.repository.mysql.SectionTemplateRepository;
import pj.gob.pe.judicial.repository.mysql.TemplateRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.MigracionPlantillaService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseMigracionPlantillas;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoMigracionDocumento;
import pj.gob.pe.judicial.utils.plantilla.ConstantesPlantilla;
import pj.gob.pe.judicial.utils.plantilla.DocxPlantillaProcessor;
import pj.gob.pe.judicial.utils.plantilla.VariableSistema;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Migra los documentos del flujo por secciones (Templates + SectionTemplates + ReemplazarSeccionesTemplateN)
 * al flujo por plantilla completa, sin modificar nada del flujo anterior:
 *
 * 1. Se toma el Word base (templates/{codigoTemplate}.docx) y cada ${sectionN} se reemplaza por el
 *    contenido de su sección en BD, obteniendo el documento completo con variables ${title.juzgado}, etc.
 * 2. Cada variable se configura según cómo la resolvía el flujo anterior
 *    (migracion/mapeo_variables_secciones.json, extraído de GenDocumentoServiceImpl).
 * 3. Se crea una plantilla por Documento (código {codigoTemplate}_doc{idDocumento}), ya que el diseño
 *    asigna una plantilla a cada documento.
 *
 * Es idempotente: omite los documentos que ya tienen plantilla. Con simular = true no escribe nada.
 */
@Service
@RequiredArgsConstructor
public class MigracionPlantillaServiceImpl implements MigracionPlantillaService {

    private static final Logger logger = LoggerFactory.getLogger(MigracionPlantillaServiceImpl.class);

    private static final String RUTA_MAPEO = "migracion/mapeo_variables_secciones.json";
    private static final String PREFIJO_SECCION = "section";

    private static final String ESTADO_CREADA = "CREADA";
    private static final String ESTADO_SIMULADA = "SIMULADA";
    private static final String ESTADO_OMITIDA = "OMITIDA";
    private static final String ESTADO_ERROR = "ERROR";

    private final DocumentoAdminRepository documentoAdminRepository;
    private final TemplateRepository templateRepository;
    private final SectionTemplateRepository sectionTemplateRepository;
    private final PlantillaDocumentoRepository plantillaDocumentoRepository;
    private final PlantillaArchivoRepository plantillaArchivoRepository;
    private final PlantillaVariableRepository plantillaVariableRepository;
    private final SecurityService securityService;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseMigracionPlantillas migrarFlujoSecciones(String SessionId, boolean simular) {

        ResponseLogin responseLogin = validarSesion(SessionId);
        Long idUser = responseLogin.getUser().getIdUser();

        Map<String, Map<String, ConfigVariable>> mapeo = cargarMapeo();
        Map<String, WordMigrado> wordsPorTemplate = new HashMap<>();

        List<Documento> documentos = documentoAdminRepository.findByBorradoOrderByIdTipoDocumentoAscDescripcionAsc(Constantes.REGISTRO_NO_BORRADO);

        ResponseMigracionPlantillas respuesta = new ResponseMigracionPlantillas();
        respuesta.setSimulacion(simular);

        TransactionTemplate transaccion = new TransactionTemplate(transactionManager);

        for (Documento documento : documentos) {
            if (documento.getCodigoTemplate() == null || documento.getCodigoTemplate().isBlank()) continue;

            String codigoTemplate = documento.getCodigoTemplate().trim();
            ResultadoMigracionDocumento resultado = new ResultadoMigracionDocumento();
            resultado.setIdDocumento(documento.getIdDocumento());
            resultado.setDocumento(documento.getDescripcion());
            resultado.setCodigoTemplate(codigoTemplate);
            resultado.setCodigoPlantilla(codigoTemplate + "_doc" + documento.getIdDocumento());
            respuesta.getDetalle().add(resultado);

            try {
                if (!plantillaDocumentoRepository.findByIdDocumentoAndBorrado(documento.getIdDocumento(), Constantes.REGISTRO_NO_BORRADO).isEmpty()) {
                    omitir(resultado, "El documento ya tiene una plantilla registrada");
                    continue;
                }
                if (plantillaDocumentoRepository.existsByCodigoAndBorrado(resultado.getCodigoPlantilla(), Constantes.REGISTRO_NO_BORRADO)) {
                    omitir(resultado, "Ya existe una plantilla con el código " + resultado.getCodigoPlantilla());
                    continue;
                }

                WordMigrado word = wordsPorTemplate.get(codigoTemplate);
                if (word == null) {
                    word = armarWord(codigoTemplate);
                    wordsPorTemplate.put(codigoTemplate, word);
                }
                if (word.error != null) {
                    omitir(resultado, word.error);
                    continue;
                }

                Map<String, ConfigVariable> configTemplate = mapeo.getOrDefault(codigoTemplate, Map.of());
                List<PlantillaVariable> variables = armarVariables(word.variables, configTemplate, idUser, resultado);

                resultado.setSeccionesReemplazadas(word.seccionesReemplazadas);
                resultado.setSeccionesSinContenido(word.seccionesSinContenido);
                resultado.setSeccionesEmparejadas(word.seccionesEmparejadas);

                if (simular) {
                    resultado.setEstado(ESTADO_SIMULADA);
                    resultado.setMensaje("Plantilla lista para crearse");
                    continue;
                }

                final WordMigrado wordFinal = word;
                Long idPlantilla = transaccion.execute(status ->
                        guardar(documento, resultado.getCodigoPlantilla(), wordFinal, variables, idUser));

                resultado.setIdPlantilla(idPlantilla);
                resultado.setEstado(ESTADO_CREADA);
                resultado.setMensaje("Plantilla creada");

            } catch (Exception e) {
                logger.error("[Migracion] error en idDocumento={} template={}: {}", documento.getIdDocumento(), codigoTemplate, e.getMessage(), e);
                resultado.setEstado(ESTADO_ERROR);
                resultado.setMensaje(e.getMessage());
            }
        }

        respuesta.setDocumentosEvaluados(respuesta.getDetalle().size());
        respuesta.setCreadas(contar(respuesta, ESTADO_CREADA));
        respuesta.setSimuladas(contar(respuesta, ESTADO_SIMULADA));
        respuesta.setOmitidas(contar(respuesta, ESTADO_OMITIDA));
        respuesta.setErrores(contar(respuesta, ESTADO_ERROR));

        logger.info("[Migracion] simulacion={} evaluados={} creadas={} simuladas={} omitidas={} errores={}",
                simular, respuesta.getDocumentosEvaluados(), respuesta.getCreadas(), respuesta.getSimuladas(),
                respuesta.getOmitidas(), respuesta.getErrores());

        return respuesta;
    }

    /** Word base + contenido de las secciones = documento completo con las variables del flujo anterior. */
    private WordMigrado armarWord(String codigoTemplate) throws Exception {

        WordMigrado word = new WordMigrado();

        Template template = templateRepository.findByCode(codigoTemplate);
        if (template == null || !Constantes.REGISTRO_ACTIVO.equals(template.getActivo())
                || Constantes.REGISTRO_BORRADO.equals(template.getBorrado())) {
            word.error = "El template " + codigoTemplate + " no existe o está inactivo en el flujo por secciones";
            return word;
        }
        word.nombreOut = template.getNombreOut();

        ClassPathResource recurso = new ClassPathResource("templates/" + codigoTemplate + ConstantesPlantilla.EXTENSION_DOCX);
        if (!recurso.exists()) {
            word.error = "No existe el Word base templates/" + codigoTemplate + ConstantesPlantilla.EXTENSION_DOCX;
            return word;
        }

        byte[] base;
        try (InputStream is = recurso.getInputStream()) {
            base = is.readAllBytes();
        }

        // Secciones indexadas por código normalizado: el Word y la BD no siempre coinciden en los
        // ceros a la izquierda (ej. template_auto_19 tiene ${section09} en el Word y section9 en BD)
        Map<String, String> porCodigoNormalizado = new HashMap<>();
        Map<String, String> codigoOriginal = new HashMap<>();
        for (SectionTemplate seccion : sectionTemplateRepository.findTemplateSections(template.getId())) {
            String normalizado = normalizarSeccion(seccion.getCodigo().trim());
            porCodigoNormalizado.put(normalizado, seccion.getContent() == null ? "" : seccion.getContent());
            codigoOriginal.put(normalizado, seccion.getCodigo().trim());
        }

        DocxPlantillaProcessor procesador = DocxPlantillaProcessor.cargar(base);

        Map<String, String> contenidos = new LinkedHashMap<>();
        for (String marcador : procesador.detectarVariables()) {
            if (!marcador.startsWith(PREFIJO_SECCION)) continue;
            String contenido = porCodigoNormalizado.get(normalizarSeccion(marcador));
            if (contenido == null) {
                // Sin sección en BD: se deja vacío para no generar una variable "sectionN"
                word.seccionesSinContenido.add(marcador);
                contenido = "";
            } else if (!marcador.equals(codigoOriginal.get(normalizarSeccion(marcador)))) {
                word.seccionesEmparejadas.add("${" + marcador + "} <- " + codigoOriginal.get(normalizarSeccion(marcador)));
            }
            contenidos.put(marcador, contenido);
        }

        word.seccionesReemplazadas = procesador.reemplazarVariables(contenidos);
        word.variables = procesador.detectarVariables();
        word.contenido = procesador.guardar();
        return word;
    }

    private List<PlantillaVariable> armarVariables(Set<String> nombres, Map<String, ConfigVariable> config,
                                                  Long idUser, ResultadoMigracionDocumento resultado) {

        LocalDateTime ahora = LocalDateTime.now();
        List<PlantillaVariable> variables = new ArrayList<>();
        int sistema = 0;
        int manuales = 0;
        int orden = 1;

        for (String nombre : nombres) {
            PlantillaVariable v = new PlantillaVariable();
            v.setNombre(nombre);
            v.setDetectada(Constantes.REGISTRO_ACTIVO);
            v.setOrden(orden++);

            ConfigVariable c = config.get(nombre);
            if (c != null) {
                v.setOrigen(c.getOrigen());
                v.setCampoSistema(c.getCampoSistema());
                v.setValorDefecto(c.getValorDefecto());
                v.setDescripcion("Migrada del flujo por secciones");
            } else {
                resultado.getVariablesSinMapeo().add(nombre);
                Optional<VariableSistema> catalogo = VariableSistema.buscar(nombre);
                v.setOrigen(catalogo.isPresent() ? ConstantesPlantilla.ORIGEN_SISTEMA : ConstantesPlantilla.ORIGEN_MANUAL);
                v.setDescripcion("Migrada: el flujo por secciones no reemplazaba esta variable");
            }

            if (ConstantesPlantilla.ORIGEN_SISTEMA.equals(v.getOrigen())) sistema++;
            else manuales++;

            v.setRegDate(ahora.toLocalDate());
            v.setRegDatetime(ahora);
            v.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
            v.setRegUserId(idUser);
            v.setActivo(Constantes.REGISTRO_ACTIVO);
            v.setBorrado(Constantes.REGISTRO_NO_BORRADO);
            variables.add(v);
        }

        resultado.setVariablesDetectadas(nombres.size());
        resultado.setVariablesSistema(sistema);
        resultado.setVariablesManuales(manuales);
        return variables;
    }

    private Long guardar(Documento documento, String codigo, WordMigrado word, List<PlantillaVariable> variables, Long idUser) {

        LocalDateTime ahora = LocalDateTime.now();

        PlantillaDocumento plantilla = new PlantillaDocumento();
        plantilla.setIdDocumento(documento.getIdDocumento());
        plantilla.setCodigo(codigo);
        plantilla.setNombreOut(word.nombreOut != null ? word.nombreOut.trim() : documento.getDescripcion());
        plantilla.setDescripcion("Migrada desde el flujo por secciones (" + documento.getCodigoTemplate().trim() + ")");
        plantilla.setNombreArchivo(documento.getCodigoTemplate().trim() + ConstantesPlantilla.EXTENSION_DOCX);
        plantilla.setTamanioBytes((long) word.contenido.length);
        plantilla.setHashArchivo(sha256(word.contenido));
        plantilla.setVersion(1);
        plantilla.setCorregirIA(Constantes.REGISTRO_ACTIVO);
        plantilla.setRegDate(ahora.toLocalDate());
        plantilla.setRegDatetime(ahora);
        plantilla.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        plantilla.setRegUserId(idUser);
        plantilla.setActivo(Constantes.REGISTRO_ACTIVO);
        plantilla.setBorrado(Constantes.REGISTRO_NO_BORRADO);
        plantilla = plantillaDocumentoRepository.save(plantilla);

        PlantillaArchivo archivo = new PlantillaArchivo();
        archivo.setIdPlantilla(plantilla.getIdPlantilla());
        archivo.setArchivo(word.contenido);
        archivo.setRegDate(ahora.toLocalDate());
        archivo.setRegDatetime(ahora);
        archivo.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        archivo.setRegUserId(idUser);
        plantillaArchivoRepository.save(archivo);

        for (PlantillaVariable v : variables) {
            v.setIdPlantilla(plantilla.getIdPlantilla());
        }
        plantillaVariableRepository.saveAll(variables);

        return plantilla.getIdPlantilla();
    }

    private Map<String, Map<String, ConfigVariable>> cargarMapeo() {
        try (InputStream is = new ClassPathResource(RUTA_MAPEO).getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<Map<String, Map<String, ConfigVariable>>>() {});
        } catch (Exception e) {
            throw new ValidationServiceException("No se pudo leer el mapeo de variables " + RUTA_MAPEO + ": " + e.getMessage());
        }
    }

    /** "section09" y "section9" son la misma sección. */
    private static String normalizarSeccion(String codigo) {
        if (!codigo.startsWith(PREFIJO_SECCION)) return codigo;
        String sufijo = codigo.substring(PREFIJO_SECCION.length());
        if (!sufijo.isEmpty() && sufijo.length() < 9 && sufijo.chars().allMatch(Character::isDigit)) {
            return PREFIJO_SECCION + Integer.parseInt(sufijo);
        }
        return codigo;
    }

    private static void omitir(ResultadoMigracionDocumento resultado, String mensaje) {
        resultado.setEstado(ESTADO_OMITIDA);
        resultado.setMensaje(mensaje);
    }

    private static int contar(ResponseMigracionPlantillas respuesta, String estado) {
        return (int) respuesta.getDetalle().stream().filter(r -> estado.equals(r.getEstado())).count();
    }

    private static String sha256(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (Exception e) {
            return null;
        }
    }

    private ResponseLogin validarSesion(String SessionId) {

        if (SessionId == null || SessionId.isEmpty()) {
            throw new ValidationSessionServiceException("La sessión remitida es inválida");
        }

        ResponseLogin responseLogin = securityService.GetSessionData(SessionId);

        if (responseLogin == null || !responseLogin.isSuccess() || !responseLogin.isItemFound() || responseLogin.getUser() == null) {
            throw new ValidationSessionServiceException("La sessión remitida es inválida");
        }

        return responseLogin;
    }

    /** Configuración de una variable en migracion/mapeo_variables_secciones.json. */
    @lombok.Data
    public static class ConfigVariable {
        private String origen;
        private String campoSistema;
        private String valorDefecto;
    }

    /** Word ya armado de un template (se reutiliza para todos los documentos que lo usan). */
    private static class WordMigrado {
        private String error;
        private String nombreOut;
        private byte[] contenido;
        private Set<String> variables = Set.of();
        private int seccionesReemplazadas;
        private final List<String> seccionesSinContenido = new ArrayList<>();
        private final List<String> seccionesEmparejadas = new ArrayList<>();
    }
}
