package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.beans.DocumentoPlantillaGeneradoToKafka;
import pj.gob.pe.judicial.model.beans.VariableDocumentoToKafka;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaArchivo;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.repository.mysql.DocumentoAdminRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaArchivoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaDocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaVariableRepository;
import pj.gob.pe.judicial.repository.mysql.TipoDocumentoAdminRepository;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.service.GenDocumentoPlantillaService;
import pj.gob.pe.judicial.service.externals.ConsultaiaPlantillaService;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.utils.beans.UserLogin;
import pj.gob.pe.judicial.utils.beans.plantilla.BloquePlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.InputProcesarPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.ParrafoPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoGenerable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoPlantillaHTML;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseProcesarPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;
import pj.gob.pe.judicial.utils.plantilla.ConstantesPlantilla;
import pj.gob.pe.judicial.utils.plantilla.ContextoExpediente;
import pj.gob.pe.judicial.utils.plantilla.DocxHtmlConverter;
import pj.gob.pe.judicial.utils.plantilla.DocxPlantillaProcessor;
import pj.gob.pe.judicial.utils.plantilla.ResolucionVariable;
import pj.gob.pe.judicial.utils.plantilla.VariableSistema;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generación de documentos por plantilla completa (.docx con variables ${nombre}). Convive con
 * {@link GenDocumentoServiceImpl} (flujo por secciones) sin compartir estado.
 *
 * Flujo:
 * 1. Datos del expediente desde el SIJ (solo lectura, vía ExpedienteService).
 * 2. Resolución de variables: catálogo del sistema (SIJ/calculadas), MANUAL ("..." o valor por
 *    defecto) e IA; reemplazo directo sobre el Word conservando el formato.
 * 3. UNA sola llamada a Gemini (consultaia) para corregir la concordancia de los párrafos que
 *    recibieron variables y redactar los bloques de origen IA. Si la IA falla, el documento se
 *    entrega igual sin corrección (estadoIA = ERROR) en lugar de perder la generación.
 * 4. Salida DOCX o HTML y evento de métricas a Kafka (no bloqueante), también en errores.
 */
@Service
@RequiredArgsConstructor
public class GenDocumentoPlantillaServiceImpl implements GenDocumentoPlantillaService {

    private static final Logger logger = LoggerFactory.getLogger(GenDocumentoPlantillaServiceImpl.class);

    private static final int LARGO_MAXIMO_MENSAJE = 1000;

    private final ExpedienteService expedienteService;
    private final SecurityService securityService;
    private final ConsultaiaPlantillaService consultaiaPlantillaService;
    private final PlantillaDocumentoRepository plantillaDocumentoRepository;
    private final PlantillaArchivoRepository plantillaArchivoRepository;
    private final PlantillaVariableRepository plantillaVariableRepository;
    private final DocumentoAdminRepository documentoAdminRepository;
    private final TipoDocumentoAdminRepository tipoDocumentoAdminRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ====================================================================
    // Catálogo para el frontend de generación
    // ====================================================================

    @Override
    public List<ResponseDocumentoGenerable> listarDocumentosGenerables(String SessionId, String idInstancia) {

        validarSesion(SessionId);

        List<TipoDocumento> tipos = (idInstancia != null && !idInstancia.isBlank())
                ? tipoDocumentoAdminRepository.findByIdInstanciaAndActivoAndBorradoOrderByDescripcionAsc(
                        idInstancia.trim(), Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO)
                : tipoDocumentoAdminRepository.findByActivoAndBorradoOrderByIdInstanciaAscDescripcionAsc(
                        Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO);

        if (tipos.isEmpty()) return new ArrayList<>();

        Map<Long, TipoDocumento> tiposPorId = tipos.stream()
                .collect(Collectors.toMap(TipoDocumento::getIdTipoDocumento, Function.identity()));

        List<Documento> documentos = documentoAdminRepository.findByIdTipoDocumentoInAndActivoAndBorrado(
                new ArrayList<>(tiposPorId.keySet()), Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO);

        Map<Long, PlantillaDocumento> plantillaPorDocumento = new HashMap<>();
        for (PlantillaDocumento p : plantillaDocumentoRepository.findByActivoAndBorrado(Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO)) {
            plantillaPorDocumento.putIfAbsent(p.getIdDocumento(), p);
        }

        List<ResponseDocumentoGenerable> resultado = new ArrayList<>();
        for (Documento documento : documentos) {
            PlantillaDocumento plantilla = plantillaPorDocumento.get(documento.getIdDocumento());
            if (plantilla == null) continue;

            TipoDocumento tipo = tiposPorId.get(documento.getIdTipoDocumento());
            resultado.add(new ResponseDocumentoGenerable(
                    tipo.getIdTipoDocumento(), tipo.getDescripcion(), tipo.getIdInstancia(),
                    documento.getIdDocumento(), documento.getDescripcion(),
                    plantilla.getIdPlantilla(), plantilla.getCodigo(), plantilla.getNombreOut(), plantilla.getVersion()));
        }
        return resultado;
    }

    // ====================================================================
    // Generación
    // ====================================================================

    @Override
    public ResultadoDocumentoPlantilla generarDocx(Long nUnico, String numIncidente, Long idDocumento, String SessionId) throws Exception {

        Generacion g = generar(nUnico, numIncidente, idDocumento, SessionId, ConstantesPlantilla.TYPEDOC_DOC);

        ResultadoDocumentoPlantilla resultado = new ResultadoDocumentoPlantilla();
        resultado.setContenido(g.docx);
        resultado.setNombreArchivo(g.nombreArchivo);
        resultado.setVariablesSinValor(g.variablesSinValor);
        resultado.setEstadoIA(g.estadoIA);
        resultado.setSessionUID(g.sessionUID);
        return resultado;
    }

    @Override
    public ResponseDocumentoPlantillaHTML generarHTML(Long nUnico, String numIncidente, Long idDocumento, String SessionId) throws Exception {

        Generacion g = generar(nUnico, numIncidente, idDocumento, SessionId, ConstantesPlantilla.TYPEDOC_WEB);

        ResponseDocumentoPlantillaHTML response = new ResponseDocumentoPlantillaHTML();
        response.setNUnico(nUnico);
        response.setIdDocumento(idDocumento);
        response.setCodigoPlantilla(g.plantilla.getCodigo());
        response.setVersionPlantilla(g.plantilla.getVersion());
        response.setNombreArchivo(g.nombreArchivo);
        response.setContentHTML(g.html);
        response.setVariablesSinValor(g.variablesSinValor);
        response.setEstadoIA(g.estadoIA);
        response.setMensajeIA(g.mensajeIA);
        response.setSessionUID(g.sessionUID);
        response.setSuccess(true);
        return response;
    }

    private Generacion generar(Long nUnico, String numIncidente, Long idDocumento, String SessionId, String typedoc) throws Exception {

        long inicio = System.nanoTime();

        ResponseLogin responseLogin = validarSesion(SessionId);

        Generacion g = new Generacion();
        g.sessionUID = UUID.randomUUID().toString();
        g.evento = nuevoEvento(responseLogin.getUser(), typedoc, g.sessionUID, nUnico, numIncidente, idDocumento);

        try {
            // ---- Documento, tipo y plantilla (MySQL) ----
            Documento documento = documentoAdminRepository.findById(idDocumento)
                    .filter(d -> Constantes.REGISTRO_ACTIVO.equals(d.getActivo()) && !Constantes.REGISTRO_BORRADO.equals(d.getBorrado()))
                    .orElseThrow(() -> new ModeloNotFoundException("Documento no encontrado o inactivo: " + idDocumento));
            g.evento.setDocumento(documento.getDescripcion());

            TipoDocumento tipoDocumento = tipoDocumentoAdminRepository.findById(documento.getIdTipoDocumento()).orElse(null);
            g.evento.setIdTipoDocumento(documento.getIdTipoDocumento());
            g.evento.setTipoDocumento(tipoDocumento != null ? tipoDocumento.getDescripcion() : null);

            g.plantilla = plantillaDocumentoRepository.findFirstByIdDocumentoAndActivoAndBorradoOrderByIdPlantillaDesc(
                            idDocumento, Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO)
                    .orElseThrow(() -> new ModeloNotFoundException("El documento no tiene una plantilla activa"));
            llenarPlantilla(g.evento, g.plantilla);

            PlantillaArchivo archivo = plantillaArchivoRepository.findById(g.plantilla.getIdPlantilla())
                    .orElseThrow(() -> new ModeloNotFoundException("La plantilla no tiene archivo cargado"));

            // ---- Expediente (SIJ, solo lectura) ----
            long inicioSij = System.nanoTime();
            List<DataExpedienteDTO> filas = expedienteService.getDataExpediente(nUnico, numIncidente);
            g.evento.setTiempoSijMs(ms(inicioSij));

            if (filas == null || filas.isEmpty() || filas.get(0) == null) {
                throw new ModeloNotFoundException("Expediente no encontrado");
            }

            ContextoExpediente contexto = new ContextoExpediente(filas, LocalDate.now());
            llenarExpediente(g.evento, contexto);

            // ---- Resolución de variables y reemplazo (fase 1: todo menos IA) ----
            DocxPlantillaProcessor procesador = DocxPlantillaProcessor.cargar(archivo.getArchivo());

            Map<String, PlantillaVariable> definiciones = new HashMap<>();
            for (PlantillaVariable v : plantillaVariableRepository.findByIdPlantillaAndBorradoOrderByOrdenAscIdVariableAsc(
                    g.plantilla.getIdPlantilla(), Constantes.REGISTRO_NO_BORRADO)) {
                if (Constantes.REGISTRO_ACTIVO.equals(v.getActivo())) {
                    definiciones.put(v.getNombre(), v);
                }
            }

            Map<String, ResolucionVariable> resoluciones = resolver(procesador.detectarVariables(), definiciones, contexto);

            Map<String, String> valoresFase1 = new HashMap<>();
            List<ResolucionVariable> pendientesIA = new ArrayList<>();
            for (ResolucionVariable r : resoluciones.values()) {
                if (r.getValor() == null) {
                    pendientesIA.add(r);
                } else {
                    valoresFase1.put(r.getNombre(), r.getValor());
                }
            }

            // Los párrafos se identifican ANTES del reemplazo (después ya no tienen ${...})
            boolean corregir = Constantes.REGISTRO_ACTIVO.equals(g.plantilla.getCorregirIA());
            List<P> parrafosCorregir = corregir
                    ? procesador.parrafosConVariables(valoresFase1::containsKey)
                    : new ArrayList<>();

            int reemplazos = procesador.reemplazarVariables(valoresFase1);

            // ---- IA: una sola llamada (corrección + bloques) ----
            procesarIA(g, procesador, parrafosCorregir, pendientesIA, contexto, responseLogin, SessionId);

            Map<String, String> valoresIA = new HashMap<>();
            pendientesIA.forEach(r -> valoresIA.put(r.getNombre(), r.getValor()));
            reemplazos += procesador.reemplazarVariables(valoresIA);

            // ---- Salida ----
            g.nombreArchivo = nombreArchivo(g.plantilla);
            if (ConstantesPlantilla.TYPEDOC_DOC.equals(typedoc)) {
                g.docx = procesador.guardar();
                g.evento.setTamanioSalidaBytes((long) g.docx.length);
            } else {
                g.html = DocxHtmlConverter.convertir(procesador.getPaquete());
                g.evento.setTamanioSalidaBytes((long) g.html.getBytes(StandardCharsets.UTF_8).length);
            }

            g.variablesSinValor = resoluciones.values().stream()
                    .filter(r -> !r.isTieneValor())
                    .map(ResolucionVariable::getNombre)
                    .toList();
            llenarVariables(g.evento, resoluciones, reemplazos);

            g.evento.setStatus(Constantes.COMPLETION_EXITOSO);
            g.evento.setTiempoTotalMs(ms(inicio));
            publicar(g.evento);

            logger.info("[DocumentoPlantilla] sessionUID={} nUnico={} idDocumento={} plantilla={} v{} typedoc={} variables={} sinValor={} IA={} tiempo={}ms",
                    g.sessionUID, nUnico, idDocumento, g.plantilla.getCodigo(), g.plantilla.getVersion(), typedoc,
                    resoluciones.size(), g.variablesSinValor.size(), g.estadoIA, g.evento.getTiempoTotalMs());

            return g;

        } catch (Exception e) {
            g.evento.setStatus(Constantes.COMPLETION_ERROR);
            g.evento.setMensajeError(recortar(e.getMessage()));
            g.evento.setTiempoTotalMs(ms(inicio));
            publicar(g.evento);
            throw e;
        }
    }

    /**
     * Arma la solicitud a consultaia con los párrafos a corregir (partidos por w:t para conservar el
     * formato) y los bloques a redactar, y aplica la respuesta. Nunca lanza: ante un fallo deja el
     * documento sin corregir y los bloques de IA en "...".
     */
    private void procesarIA(Generacion g, DocxPlantillaProcessor procesador, List<P> parrafosCorregir,
                            List<ResolucionVariable> pendientesIA, ContextoExpediente contexto,
                            ResponseLogin responseLogin, String SessionId) {

        Map<Integer, List<Text>> textosPorParrafo = new LinkedHashMap<>();
        List<ParrafoPlantillaIA> parrafos = new ArrayList<>();
        int id = 1;
        for (P p : parrafosCorregir) {
            List<Text> textos = DocxPlantillaProcessor.textos(p);
            List<String> segmentos = textos.stream().map(DocxPlantillaProcessor::valor).toList();
            if (String.join("", segmentos).isBlank()) continue;

            textosPorParrafo.put(id, textos);
            parrafos.add(new ParrafoPlantillaIA(id, segmentos));
            id++;
        }

        List<BloquePlantillaIA> bloques = pendientesIA.stream()
                .map(r -> new BloquePlantillaIA(r.getNombre(), r.getInstruccionIA(), null))
                .toList();

        g.evento.setParrafosEnviadosIA(parrafos.size());
        g.evento.setBloquesSolicitadosIA(bloques.size());
        g.evento.setParrafosCorregidosIA(0);
        g.evento.setBloquesGeneradosIA(0);

        if (parrafos.isEmpty() && bloques.isEmpty()) {
            g.estadoIA = ConstantesPlantilla.IA_NO_APLICA;
            g.evento.setEstadoIA(g.estadoIA);
            return;
        }

        InputProcesarPlantillaIA input = new InputProcesarPlantillaIA();
        input.setSessionUID(g.sessionUID);
        input.setNUnico(contexto.getNUnico());
        input.setIdUser(responseLogin.getUser().getIdUser());
        input.setIdDocumento(g.plantilla.getIdDocumento());
        input.setDocumento(g.evento.getDocumento());
        input.setIdPlantilla(g.plantilla.getIdPlantilla());
        input.setCodigoPlantilla(g.plantilla.getCodigo());
        input.setVersionPlantilla(g.plantilla.getVersion());
        input.setContextoExpediente(bloques.isEmpty() ? null : contexto.resumenParaIA());
        input.setParrafos(parrafos);
        input.setBloques(bloques);

        long inicioIA = System.nanoTime();
        ResponseProcesarPlantillaIA respuesta;
        try {
            respuesta = consultaiaPlantillaService.ProcesarPlantillaGemini(input, SessionId);
        } catch (Exception e) {
            logger.warn("[DocumentoPlantilla] sessionUID={} la IA no pudo procesar el documento, se entrega sin corrección: {}",
                    g.sessionUID, e.getMessage());
            g.evento.setTiempoIAMs(ms(inicioIA));
            g.estadoIA = ConstantesPlantilla.IA_ERROR;
            g.mensajeIA = "No se pudo aplicar la corrección con IA: " + e.getMessage();
            g.evento.setEstadoIA(g.estadoIA);
            g.evento.setMensajeIA(recortar(g.mensajeIA));
            pendientesIA.forEach(this::sinDato);
            return;
        }
        g.evento.setTiempoIAMs(ms(inicioIA));

        // Correcciones: solo se aplican si respetan la cantidad de segmentos (formato intacto)
        int corregidos = 0;
        if (respuesta.getParrafos() != null) {
            for (ParrafoPlantillaIA corregido : respuesta.getParrafos()) {
                List<Text> textos = corregido.getId() != null ? textosPorParrafo.get(corregido.getId()) : null;
                if (textos == null || corregido.getSegmentos() == null || corregido.getSegmentos().size() != textos.size()) {
                    logger.warn("[DocumentoPlantilla] sessionUID={} corrección descartada para el párrafo {}: segmentos no coinciden",
                            g.sessionUID, corregido.getId());
                    continue;
                }
                for (int i = 0; i < textos.size(); i++) {
                    DocxPlantillaProcessor.asignar(textos.get(i), DocxPlantillaProcessor.sanear(corregido.getSegmentos().get(i)));
                }
                corregidos++;
            }
        }

        Map<String, String> contenidos = new HashMap<>();
        if (respuesta.getBloques() != null) {
            for (BloquePlantillaIA b : respuesta.getBloques()) {
                if (b.getVariable() != null && b.getContenido() != null && !b.getContenido().isBlank()) {
                    contenidos.put(b.getVariable(), b.getContenido().trim());
                }
            }
        }
        int generados = 0;
        for (ResolucionVariable r : pendientesIA) {
            String contenido = contenidos.get(r.getNombre());
            if (contenido != null) {
                r.setValor(contenido);
                r.setTieneValor(true);
                generados++;
            } else {
                sinDato(r);
            }
        }

        g.estadoIA = ConstantesPlantilla.IA_EXITOSO;
        g.evento.setEstadoIA(g.estadoIA);
        g.evento.setParrafosCorregidosIA(corregidos);
        g.evento.setBloquesGeneradosIA(generados);
        g.evento.setModel(respuesta.getModel());
        g.evento.setRoleSystem(respuesta.getRoleSystem());
        g.evento.setTemperature(respuesta.getTemperature());
        g.evento.setConfigurationsId(respuesta.getConfigurationsId());
        g.evento.setFinishReason(respuesta.getFinishReason());
        g.evento.setPromptTokens(respuesta.getPromptTokens());
        g.evento.setCandidatesTokens(respuesta.getCandidatesTokens());
        g.evento.setThoughtsTokens(respuesta.getThoughtsTokens());
        g.evento.setCachedTokens(respuesta.getCachedTokens());
        g.evento.setTotalTokens(respuesta.getTotalTokens());
    }

    /**
     * Resuelve cada variable del documento. Las de origen IA con instrucción quedan con valor null
     * (pendientes); el resto queda con su valor final o "...".
     */
    private Map<String, ResolucionVariable> resolver(Set<String> nombres, Map<String, PlantillaVariable> definiciones,
                                                    ContextoExpediente contexto) {

        Map<String, ResolucionVariable> resoluciones = new LinkedHashMap<>();

        for (String nombre : nombres) {
            ResolucionVariable r = new ResolucionVariable();
            r.setNombre(nombre);

            PlantillaVariable def = definiciones.get(nombre);
            String origen = def != null
                    ? def.getOrigen()
                    : (VariableSistema.buscar(nombre).isPresent() ? ConstantesPlantilla.ORIGEN_SISTEMA : null);

            if (ConstantesPlantilla.ORIGEN_IA.equals(origen)) {
                r.setTipo(ConstantesPlantilla.TIPO_IA);
                r.setInstruccionIA(def.getInstruccionIA());
                if (!noVacio(def.getInstruccionIA())) {
                    sinDato(r);
                }
            } else if (ConstantesPlantilla.ORIGEN_MANUAL.equals(origen)) {
                r.setTipo(ConstantesPlantilla.TIPO_MANUAL);
                if (noVacio(def.getValorDefecto())) {
                    conValor(r, def.getValorDefecto());
                } else {
                    sinDato(r);
                }
            } else if (ConstantesPlantilla.ORIGEN_SISTEMA.equals(origen)) {
                String clave = def != null && noVacio(def.getCampoSistema()) ? def.getCampoSistema().trim() : nombre;
                r.setCampoSistema(clave);

                Optional<VariableSistema> sistema = VariableSistema.buscar(clave);
                if (sistema.isEmpty()) {
                    r.setTipo(ConstantesPlantilla.TIPO_NO_DEFINIDA);
                    sinDato(r);
                } else {
                    r.setTipo(sistema.get().getTipo());
                    String valor = sistema.get().resolver(contexto);
                    if (!valor.isEmpty()) {
                        conValor(r, valor);
                    } else if (def != null && noVacio(def.getValorDefecto())) {
                        conValor(r, def.getValorDefecto());
                    } else {
                        sinDato(r);
                    }
                }
            } else {
                r.setTipo(ConstantesPlantilla.TIPO_NO_DEFINIDA);
                sinDato(r);
            }

            resoluciones.put(nombre, r);
        }
        return resoluciones;
    }

    private void conValor(ResolucionVariable r, String valor) {
        r.setValor(valor);
        r.setTieneValor(true);
    }

    private void sinDato(ResolucionVariable r) {
        r.setValor(ConstantesPlantilla.VALOR_SIN_DATO);
        r.setTieneValor(false);
    }

    // ====================================================================
    // Métricas (Kafka)
    // ====================================================================

    private DocumentoPlantillaGeneradoToKafka nuevoEvento(UserLogin user, String typedoc, String sessionUID,
                                                          Long nUnico, String numIncidente, Long idDocumento) {

        LocalDateTime ahora = LocalDateTime.now();

        DocumentoPlantillaGeneradoToKafka evento = new DocumentoPlantillaGeneradoToKafka();
        evento.setSessionUID(sessionUID);
        evento.setTypedoc(typedoc);
        evento.setStatus(Constantes.COMPLETION_INICIADO);
        evento.setEstadoIA(ConstantesPlantilla.IA_NO_APLICA);

        evento.setUserId(user.getIdUser());
        evento.setUsername(user.getUsername());
        evento.setNombreUsuario(((user.getNombres() == null ? "" : user.getNombres()) + " "
                + (user.getApellidos() == null ? "" : user.getApellidos())).trim());
        evento.setCargo(user.getCargo());
        evento.setIdDependencia(user.getIdDependencia());
        evento.setDependencia(user.getNombreDependencia());

        evento.setNUnico(nUnico);
        evento.setNumIncidente(numIncidente);
        evento.setIdDocumento(idDocumento);

        evento.setRegDate(ahora.toLocalDate());
        evento.setRegDatetime(ahora);
        evento.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        return evento;
    }

    private void llenarPlantilla(DocumentoPlantillaGeneradoToKafka evento, PlantillaDocumento plantilla) {
        evento.setIdPlantilla(plantilla.getIdPlantilla());
        evento.setCodigoPlantilla(plantilla.getCodigo());
        evento.setNombreOutPlantilla(plantilla.getNombreOut());
        evento.setVersionPlantilla(plantilla.getVersion());
        evento.setCorregirIA(plantilla.getCorregirIA());
        evento.setTamanioPlantillaBytes(plantilla.getTamanioBytes());
    }

    private void llenarExpediente(DocumentoPlantillaGeneradoToKafka evento, ContextoExpediente ctx) {
        evento.setCodSede(ctx.getCodSede());
        evento.setSede(ctx.getSede());
        evento.setCodInstancia(ctx.getCodInstancia());
        evento.setInstancia(ctx.getJuzgado());
        evento.setCodEspecialidad(ctx.getCodEspecialidad());
        evento.setEspecialidad(ctx.getEspecialidad());
        evento.setCodMateria(ctx.getCodMateria());
        evento.setMateria(ctx.getMateria());
        evento.setCodNumero(ctx.getNumero());
        evento.setCodYear(ctx.getAnio());
        evento.setXFormato(ctx.getExpediente());
        evento.setUbicacion(ctx.getUbicacion());
        evento.setJuez(ctx.getJuez());
        evento.setEspecialista(ctx.getEspecialista());
        evento.setEstado(ctx.getEstado());
        evento.setDniDemandante(ctx.getDniDemandantes());
        evento.setDemandante(ctx.getDemandantes());
        evento.setDniDemandado(ctx.getDniDemandados());
        evento.setDemandado(ctx.getDemandados());
        evento.setCantidadDemandantes(ctx.getCantidadDemandantes());
        evento.setCantidadDemandados(ctx.getCantidadDemandados());
    }

    private void llenarVariables(DocumentoPlantillaGeneradoToKafka evento, Map<String, ResolucionVariable> resoluciones, int reemplazos) {

        Map<String, Long> porTipo = resoluciones.values().stream()
                .collect(Collectors.groupingBy(ResolucionVariable::getTipo, Collectors.counting()));

        evento.setTotalVariables(resoluciones.size());
        evento.setVariablesSij(porTipo.getOrDefault(ConstantesPlantilla.TIPO_SIJ, 0L).intValue());
        evento.setVariablesCalculadas(porTipo.getOrDefault(ConstantesPlantilla.TIPO_CALCULADA, 0L).intValue());
        evento.setVariablesManuales(porTipo.getOrDefault(ConstantesPlantilla.TIPO_MANUAL, 0L).intValue());
        evento.setVariablesIA(porTipo.getOrDefault(ConstantesPlantilla.TIPO_IA, 0L).intValue());
        evento.setVariablesNoDefinidas(porTipo.getOrDefault(ConstantesPlantilla.TIPO_NO_DEFINIDA, 0L).intValue());
        evento.setVariablesSinValor((int) resoluciones.values().stream().filter(r -> !r.isTieneValor()).count());
        evento.setReemplazosRealizados(reemplazos);

        evento.setVariables(resoluciones.values().stream()
                .map(r -> new VariableDocumentoToKafka(r.getNombre(), r.getTipo(), r.getCampoSistema(), r.isTieneValor()))
                .collect(Collectors.toList()));
    }

    /** Best-effort: las métricas nunca deben hacer fallar ni demorar la generación. */
    private void publicar(DocumentoPlantillaGeneradoToKafka evento) {
        try {
            kafkaTemplate.send(ConstantesPlantilla.KAFKA_TOPIC, evento.getSessionUID(), evento)
                    .whenComplete((resultado, error) -> {
                        if (error != null) {
                            logger.warn("Error publicando en Kafka topic {} sessionUID={}: {}",
                                    ConstantesPlantilla.KAFKA_TOPIC, evento.getSessionUID(), error.getMessage());
                        }
                    });
        } catch (Exception ex) {
            logger.warn("Error publicando en Kafka topic {} sessionUID={}: {}",
                    ConstantesPlantilla.KAFKA_TOPIC, evento.getSessionUID(), ex.getMessage());
        }
    }

    // ====================================================================
    // Soporte
    // ====================================================================

    private static String nombreArchivo(PlantillaDocumento plantilla) {
        String base = noVacio(plantilla.getNombreOut()) ? plantilla.getNombreOut().trim() : plantilla.getCodigo();
        return base + ConstantesPlantilla.EXTENSION_DOCX;
    }

    private static long ms(long inicioNano) {
        return (System.nanoTime() - inicioNano) / 1_000_000L;
    }

    private static String recortar(String mensaje) {
        if (mensaje == null) return null;
        return mensaje.length() > LARGO_MAXIMO_MENSAJE ? mensaje.substring(0, LARGO_MAXIMO_MENSAJE) : mensaje;
    }

    private static boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
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

    /** Estado de una generación en curso. */
    private static final class Generacion {
        private String sessionUID;
        private DocumentoPlantillaGeneradoToKafka evento;
        private PlantillaDocumento plantilla;
        private String nombreArchivo;
        private byte[] docx;
        private String html;
        private List<String> variablesSinValor = new ArrayList<>();
        private String estadoIA = ConstantesPlantilla.IA_NO_APLICA;
        private String mensajeIA;
    }
}
