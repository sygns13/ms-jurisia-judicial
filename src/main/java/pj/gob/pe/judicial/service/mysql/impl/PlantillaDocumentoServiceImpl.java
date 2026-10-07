package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationServiceException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaArchivo;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;
import pj.gob.pe.judicial.repository.mysql.DocumentoAdminRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaArchivoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaDocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaVariableRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.PlantillaDocumentoService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaDocumento;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaVariable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponsePlantillaDetalle;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseVariableSistema;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;
import pj.gob.pe.judicial.utils.plantilla.ConstantesPlantilla;
import pj.gob.pe.judicial.utils.plantilla.DocxPlantillaProcessor;
import pj.gob.pe.judicial.utils.plantilla.VariableSistema;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Mantenimiento de plantillas .docx completas. Al cargar o reemplazar el archivo se detectan las
 * variables ${...} del Word y se registran automáticamente en PlantillaVariable: las que existen en
 * el catálogo del sistema quedan como SISTEMA y el resto como MANUAL ("..."); el administrador
 * puede luego cambiarlas, por ejemplo a IA con su instrucción.
 */
@Service
@RequiredArgsConstructor
public class PlantillaDocumentoServiceImpl implements PlantillaDocumentoService {

    private static final Logger logger = LoggerFactory.getLogger(PlantillaDocumentoServiceImpl.class);

    private final PlantillaDocumentoRepository plantillaDocumentoRepository;
    private final PlantillaArchivoRepository plantillaArchivoRepository;
    private final PlantillaVariableRepository plantillaVariableRepository;
    private final DocumentoAdminRepository documentoAdminRepository;
    private final SecurityService securityService;

    // ====================================================================
    // Plantillas
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaDocumento> listar(String SessionId, Long idDocumento) {

        validarSesion(SessionId);

        if (idDocumento != null && idDocumento > 0) {
            return plantillaDocumentoRepository.findByIdDocumentoAndBorrado(idDocumento, Constantes.REGISTRO_NO_BORRADO);
        }
        return plantillaDocumentoRepository.findByBorradoOrderByIdPlantillaDesc(Constantes.REGISTRO_NO_BORRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePlantillaDetalle listarPorId(String SessionId, Long idPlantilla) {

        validarSesion(SessionId);

        return armarDetalle(obtenerNoBorrada(idPlantilla));
    }

    @Override
    @Transactional
    public ResponsePlantillaDetalle registrar(String SessionId, InputPlantillaDocumento input, MultipartFile archivo) {

        ResponseLogin responseLogin = validarSesion(SessionId);
        Long idUser = responseLogin.getUser().getIdUser();

        Documento documento = validarDocumento(input.getIdDocumento());
        String codigo = input.getCodigo().trim();

        if (plantillaDocumentoRepository.existsByCodigoAndBorrado(codigo, Constantes.REGISTRO_NO_BORRADO)) {
            throw new ValidationServiceException("Ya existe una plantilla con el código " + codigo);
        }
        if (!plantillaDocumentoRepository.findByIdDocumentoAndBorrado(input.getIdDocumento(), Constantes.REGISTRO_NO_BORRADO).isEmpty()) {
            throw new ValidationServiceException("El Documento ya tiene una plantilla registrada: reemplace su archivo o elimínela antes de crear otra");
        }

        byte[] contenido = leerArchivo(archivo);
        Set<String> detectadas = detectarVariables(contenido);

        LocalDateTime ahora = LocalDateTime.now();

        PlantillaDocumento plantilla = new PlantillaDocumento();
        plantilla.setIdDocumento(input.getIdDocumento());
        plantilla.setCodigo(codigo);
        plantilla.setNombreOut(noVacio(input.getNombreOut()) ? input.getNombreOut().trim() : documento.getDescripcion());
        plantilla.setDescripcion(noVacio(input.getDescripcion()) ? input.getDescripcion().trim() : null);
        plantilla.setNombreArchivo(nombreArchivo(archivo));
        plantilla.setTamanioBytes((long) contenido.length);
        plantilla.setHashArchivo(sha256(contenido));
        plantilla.setVersion(1);
        plantilla.setCorregirIA(input.getCorregirIA() != null ? input.getCorregirIA() : Constantes.REGISTRO_ACTIVO);
        plantilla.setRegDate(ahora.toLocalDate());
        plantilla.setRegDatetime(ahora);
        plantilla.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        plantilla.setRegUserId(idUser);
        plantilla.setActivo(Constantes.REGISTRO_ACTIVO);
        plantilla.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        plantilla = plantillaDocumentoRepository.save(plantilla);

        PlantillaArchivo plantillaArchivo = new PlantillaArchivo();
        plantillaArchivo.setIdPlantilla(plantilla.getIdPlantilla());
        plantillaArchivo.setArchivo(contenido);
        plantillaArchivo.setRegDate(ahora.toLocalDate());
        plantillaArchivo.setRegDatetime(ahora);
        plantillaArchivo.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        plantillaArchivo.setRegUserId(idUser);
        plantillaArchivoRepository.save(plantillaArchivo);

        sincronizarVariables(plantilla.getIdPlantilla(), detectadas, idUser);

        logger.info("[Plantilla] registrada id={} codigo={} idDocumento={} variables={}",
                plantilla.getIdPlantilla(), codigo, plantilla.getIdDocumento(), detectadas.size());

        return armarDetalle(plantilla);
    }

    @Override
    @Transactional
    public ResponsePlantillaDetalle modificar(String SessionId, Long idPlantilla, InputPlantillaDocumento input) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        PlantillaDocumento plantilla = obtenerNoBorrada(idPlantilla);
        validarDocumento(input.getIdDocumento());
        String codigo = input.getCodigo().trim();

        if (plantillaDocumentoRepository.existsByCodigoAndBorradoAndIdPlantillaNot(codigo, Constantes.REGISTRO_NO_BORRADO, idPlantilla)) {
            throw new ValidationServiceException("Ya existe una plantilla con el código " + codigo);
        }
        if (!input.getIdDocumento().equals(plantilla.getIdDocumento())
                && !plantillaDocumentoRepository.findByIdDocumentoAndBorrado(input.getIdDocumento(), Constantes.REGISTRO_NO_BORRADO).isEmpty()) {
            throw new ValidationServiceException("El Documento destino ya tiene una plantilla registrada");
        }

        plantilla.setIdDocumento(input.getIdDocumento());
        plantilla.setCodigo(codigo);
        if (noVacio(input.getNombreOut())) {
            plantilla.setNombreOut(input.getNombreOut().trim());
        }
        plantilla.setDescripcion(noVacio(input.getDescripcion()) ? input.getDescripcion().trim() : null);
        if (input.getCorregirIA() != null) {
            plantilla.setCorregirIA(input.getCorregirIA());
        }
        marcarModificacion(plantilla, responseLogin.getUser().getIdUser());

        return armarDetalle(plantillaDocumentoRepository.save(plantilla));
    }

    @Override
    @Transactional
    public ResponsePlantillaDetalle reemplazarArchivo(String SessionId, Long idPlantilla, MultipartFile archivo) {

        ResponseLogin responseLogin = validarSesion(SessionId);
        Long idUser = responseLogin.getUser().getIdUser();

        PlantillaDocumento plantilla = obtenerNoBorrada(idPlantilla);

        byte[] contenido = leerArchivo(archivo);
        Set<String> detectadas = detectarVariables(contenido);

        LocalDateTime ahora = LocalDateTime.now();

        PlantillaArchivo plantillaArchivo = plantillaArchivoRepository.findById(idPlantilla).orElseGet(() -> {
            PlantillaArchivo nuevo = new PlantillaArchivo();
            nuevo.setIdPlantilla(idPlantilla);
            nuevo.setRegDate(ahora.toLocalDate());
            nuevo.setRegDatetime(ahora);
            nuevo.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
            nuevo.setRegUserId(idUser);
            return nuevo;
        });
        plantillaArchivo.setArchivo(contenido);
        plantillaArchivo.setUpdDate(ahora.toLocalDate());
        plantillaArchivo.setUpdDatetime(ahora);
        plantillaArchivo.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        plantillaArchivo.setUpdUserId(idUser);
        plantillaArchivoRepository.save(plantillaArchivo);

        plantilla.setNombreArchivo(nombreArchivo(archivo));
        plantilla.setTamanioBytes((long) contenido.length);
        plantilla.setHashArchivo(sha256(contenido));
        plantilla.setVersion(plantilla.getVersion() == null ? 1 : plantilla.getVersion() + 1);
        marcarModificacion(plantilla, idUser);
        plantilla = plantillaDocumentoRepository.save(plantilla);

        sincronizarVariables(idPlantilla, detectadas, idUser);

        logger.info("[Plantilla] archivo reemplazado id={} version={} variables={}",
                idPlantilla, plantilla.getVersion(), detectadas.size());

        return armarDetalle(plantilla);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoDocumentoPlantilla descargarArchivo(String SessionId, Long idPlantilla) {

        validarSesion(SessionId);

        PlantillaDocumento plantilla = obtenerNoBorrada(idPlantilla);
        PlantillaArchivo plantillaArchivo = plantillaArchivoRepository.findById(idPlantilla)
                .orElseThrow(() -> new ModeloNotFoundException("La plantilla " + idPlantilla + " no tiene archivo cargado"));

        String nombre = noVacio(plantilla.getNombreArchivo())
                ? plantilla.getNombreArchivo()
                : plantilla.getCodigo() + ConstantesPlantilla.EXTENSION_DOCX;

        ResultadoDocumentoPlantilla resultado = new ResultadoDocumentoPlantilla();
        resultado.setContenido(plantillaArchivo.getArchivo());
        resultado.setNombreArchivo(nombre);
        return resultado;
    }

    @Override
    @Transactional
    public void eliminar(String SessionId, Long idPlantilla) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        PlantillaDocumento plantilla = obtenerNoBorrada(idPlantilla);
        plantilla.setActivo(Constantes.REGISTRO_INACTIVO);
        plantilla.setBorrado(Constantes.REGISTRO_BORRADO);
        marcarModificacion(plantilla, responseLogin.getUser().getIdUser());

        plantillaDocumentoRepository.save(plantilla);
    }

    @Override
    @Transactional
    public PlantillaDocumento altabaja(String SessionId, Long idPlantilla, Integer valor) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        if (!Constantes.REGISTRO_ACTIVO.equals(valor) && !Constantes.REGISTRO_INACTIVO.equals(valor)) {
            throw new ValidationServiceException("El valor de activación debe ser 0 o 1");
        }

        PlantillaDocumento plantilla = obtenerNoBorrada(idPlantilla);
        plantilla.setActivo(valor);
        marcarModificacion(plantilla, responseLogin.getUser().getIdUser());

        return plantillaDocumentoRepository.save(plantilla);
    }

    // ====================================================================
    // Variables
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaVariable> listarVariables(String SessionId, Long idPlantilla) {

        validarSesion(SessionId);
        obtenerNoBorrada(idPlantilla);

        return plantillaVariableRepository.findByIdPlantillaAndBorradoOrderByOrdenAscIdVariableAsc(idPlantilla, Constantes.REGISTRO_NO_BORRADO);
    }

    @Override
    @Transactional
    public PlantillaVariable registrarVariable(String SessionId, Long idPlantilla, InputPlantillaVariable input) {

        ResponseLogin responseLogin = validarSesion(SessionId);
        obtenerNoBorrada(idPlantilla);
        validarVariable(input);

        String nombre = input.getNombre().trim();
        if (plantillaVariableRepository.findFirstByIdPlantillaAndNombreAndBorrado(idPlantilla, nombre, Constantes.REGISTRO_NO_BORRADO).isPresent()) {
            throw new ValidationServiceException("La variable " + nombre + " ya está registrada en la plantilla");
        }

        LocalDateTime ahora = LocalDateTime.now();

        PlantillaVariable variable = new PlantillaVariable();
        variable.setIdPlantilla(idPlantilla);
        variable.setNombre(nombre);
        // Registrada a mano: se marca como detectada al volver a cargar un .docx que la contenga
        variable.setDetectada(Constantes.REGISTRO_INACTIVO);
        variable.setOrden(input.getOrden() != null ? input.getOrden() : Integer.MAX_VALUE);
        aplicarConfiguracion(variable, input);
        variable.setRegDate(ahora.toLocalDate());
        variable.setRegDatetime(ahora);
        variable.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        variable.setRegUserId(responseLogin.getUser().getIdUser());
        variable.setActivo(Constantes.REGISTRO_ACTIVO);
        variable.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        return plantillaVariableRepository.save(variable);
    }

    @Override
    @Transactional
    public PlantillaVariable modificarVariable(String SessionId, Long idPlantilla, Long idVariable, InputPlantillaVariable input) {

        ResponseLogin responseLogin = validarSesion(SessionId);
        obtenerNoBorrada(idPlantilla);
        validarVariable(input);

        PlantillaVariable variable = obtenerVariable(idPlantilla, idVariable);

        String nombre = input.getNombre().trim();
        Optional<PlantillaVariable> mismoNombre = plantillaVariableRepository
                .findFirstByIdPlantillaAndNombreAndBorrado(idPlantilla, nombre, Constantes.REGISTRO_NO_BORRADO);
        if (mismoNombre.isPresent() && !mismoNombre.get().getIdVariable().equals(idVariable)) {
            throw new ValidationServiceException("La variable " + nombre + " ya está registrada en la plantilla");
        }

        variable.setNombre(nombre);
        if (input.getOrden() != null) {
            variable.setOrden(input.getOrden());
        }
        aplicarConfiguracion(variable, input);

        LocalDateTime ahora = LocalDateTime.now();
        variable.setUpdDate(ahora.toLocalDate());
        variable.setUpdDatetime(ahora);
        variable.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        variable.setUpdUserId(responseLogin.getUser().getIdUser());

        return plantillaVariableRepository.save(variable);
    }

    @Override
    @Transactional
    public void eliminarVariable(String SessionId, Long idPlantilla, Long idVariable) {

        validarSesion(SessionId);
        obtenerNoBorrada(idPlantilla);

        PlantillaVariable variable = obtenerVariable(idPlantilla, idVariable);

        // Eliminación física: un borrado lógico solo acumularía registros sin uso
        plantillaVariableRepository.delete(variable);
    }

    @Override
    public List<ResponseVariableSistema> listarVariablesSistema(String SessionId) {

        validarSesion(SessionId);

        return Arrays.stream(VariableSistema.values())
                .map(v -> new ResponseVariableSistema(v.getClave(), v.getTipo(), v.getDescripcion()))
                .toList();
    }

    // ====================================================================
    // Soporte
    // ====================================================================

    /**
     * Regenera las variables de la plantilla a partir del .docx cargado: elimina físicamente todas
     * las existentes (la configuración previa no se conserva) y registra cada variable detectada
     * como SISTEMA si su nombre existe en el catálogo {@link VariableSistema}, o como MANUAL en otro
     * caso. Nunca se registran variables IA: las configura después el administrador.
     */
    private void sincronizarVariables(Long idPlantilla, Set<String> detectadas, Long idUser) {

        int eliminadas = plantillaVariableRepository.eliminarPorPlantilla(idPlantilla);

        LocalDateTime ahora = LocalDateTime.now();
        List<PlantillaVariable> guardar = new ArrayList<>();

        int orden = 1;
        for (String nombre : detectadas) {
            Optional<VariableSistema> sistema = VariableSistema.buscar(nombre);

            PlantillaVariable variable = new PlantillaVariable();
            variable.setIdPlantilla(idPlantilla);
            variable.setNombre(nombre);

            // Solo los campos que aplican a cada origen
            if (sistema.isPresent()) {
                variable.setOrigen(ConstantesPlantilla.ORIGEN_SISTEMA);
                variable.setCampoSistema(sistema.get().getClave());
                variable.setDescripcion(sistema.get().getDescripcion());
            } else {
                variable.setOrigen(ConstantesPlantilla.ORIGEN_MANUAL);
            }

            variable.setDetectada(Constantes.REGISTRO_ACTIVO);
            variable.setOrden(orden++);
            variable.setRegDate(ahora.toLocalDate());
            variable.setRegDatetime(ahora);
            variable.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
            variable.setRegUserId(idUser);
            variable.setActivo(Constantes.REGISTRO_ACTIVO);
            variable.setBorrado(Constantes.REGISTRO_NO_BORRADO);
            guardar.add(variable);
        }

        plantillaVariableRepository.saveAll(guardar);

        logger.info("[Plantilla] variables regeneradas idPlantilla={} eliminadas={} registradas={} (sistema={}, manuales={})",
                idPlantilla, eliminadas, guardar.size(),
                guardar.stream().filter(v -> ConstantesPlantilla.ORIGEN_SISTEMA.equals(v.getOrigen())).count(),
                guardar.stream().filter(v -> ConstantesPlantilla.ORIGEN_MANUAL.equals(v.getOrigen())).count());
    }

    private ResponsePlantillaDetalle armarDetalle(PlantillaDocumento plantilla) {

        List<PlantillaVariable> variables = plantillaVariableRepository
                .findByIdPlantillaAndBorradoOrderByOrdenAscIdVariableAsc(plantilla.getIdPlantilla(), Constantes.REGISTRO_NO_BORRADO);

        List<String> detectadas = new ArrayList<>();
        List<String> advertencias = new ArrayList<>();

        for (PlantillaVariable v : variables) {
            boolean detectada = Constantes.REGISTRO_ACTIVO.equals(v.getDetectada());
            if (detectada) {
                detectadas.add(v.getNombre());
            } else {
                advertencias.add("La variable " + v.getNombre() + " está configurada pero no aparece en el Word vigente");
            }
            if (!detectada || !Constantes.REGISTRO_ACTIVO.equals(v.getActivo())) continue;

            switch (v.getOrigen() == null ? "" : v.getOrigen()) {
                case ConstantesPlantilla.ORIGEN_SISTEMA -> {
                    String clave = noVacio(v.getCampoSistema()) ? v.getCampoSistema() : v.getNombre();
                    if (VariableSistema.buscar(clave).isEmpty()) {
                        advertencias.add("La variable " + v.getNombre() + " apunta al campo de sistema inexistente '" + clave + "': quedará como '...'");
                    }
                }
                case ConstantesPlantilla.ORIGEN_IA -> {
                    if (!noVacio(v.getInstruccionIA())) {
                        advertencias.add("La variable " + v.getNombre() + " es de origen IA pero no tiene instrucción: quedará como '...'");
                    }
                }
                case ConstantesPlantilla.ORIGEN_MANUAL -> {
                    if (!noVacio(v.getValorDefecto())) {
                        advertencias.add("La variable " + v.getNombre() + " no se obtiene del SIJ: quedará como '...'");
                    }
                }
                default -> advertencias.add("La variable " + v.getNombre() + " tiene un origen no válido: " + v.getOrigen());
            }
        }

        return new ResponsePlantillaDetalle(plantilla, variables, detectadas, advertencias);
    }

    private void aplicarConfiguracion(PlantillaVariable variable, InputPlantillaVariable input) {
        variable.setOrigen(input.getOrigen());
        variable.setDescripcion(noVacio(input.getDescripcion()) ? input.getDescripcion().trim() : null);
        variable.setCampoSistema(noVacio(input.getCampoSistema()) ? input.getCampoSistema().trim() : null);
        variable.setValorDefecto(noVacio(input.getValorDefecto()) ? input.getValorDefecto() : null);
        variable.setInstruccionIA(noVacio(input.getInstruccionIA()) ? input.getInstruccionIA().trim() : null);
    }

    private void validarVariable(InputPlantillaVariable input) {
        if (ConstantesPlantilla.ORIGEN_IA.equals(input.getOrigen()) && !noVacio(input.getInstruccionIA())) {
            throw new ValidationServiceException("Las variables de origen IA requieren la instrucción de redacción");
        }
        if (ConstantesPlantilla.ORIGEN_SISTEMA.equals(input.getOrigen())) {
            String clave = noVacio(input.getCampoSistema()) ? input.getCampoSistema().trim() : input.getNombre().trim();
            if (VariableSistema.buscar(clave).isEmpty()) {
                throw new ValidationServiceException("El campo de sistema '" + clave + "' no existe en el catálogo de variables del sistema");
            }
        }
    }

    private byte[] leerArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ValidationServiceException("Debe adjuntar el archivo .docx de la plantilla");
        }
        String nombre = nombreArchivo(archivo);
        if (!nombre.toLowerCase(Locale.ROOT).endsWith(ConstantesPlantilla.EXTENSION_DOCX)) {
            throw new ValidationServiceException("La plantilla debe ser un archivo Word .docx");
        }
        if (archivo.getSize() > ConstantesPlantilla.TAMANIO_MAXIMO_BYTES) {
            throw new ValidationServiceException("La plantilla excede el tamaño máximo de 20 MB");
        }

        try {
            return archivo.getBytes();
        } catch (Exception e) {
            throw new ValidationServiceException("No se pudo leer el archivo de la plantilla: " + e.getMessage());
        }
    }

    private Set<String> detectarVariables(byte[] contenido) {
        try {
            return DocxPlantillaProcessor.cargar(contenido).detectarVariables();
        } catch (Exception e) {
            logger.warn("[Plantilla] archivo .docx inválido: {}", e.getMessage());
            throw new ValidationServiceException("El archivo no es un documento Word .docx válido");
        }
    }

    private Documento validarDocumento(Long idDocumento) {
        Documento documento = documentoAdminRepository.findById(idDocumento).orElse(null);
        if (documento == null || Constantes.REGISTRO_BORRADO.equals(documento.getBorrado())) {
            throw new ValidationServiceException("El Documento " + idDocumento + " no existe");
        }
        return documento;
    }

    private PlantillaDocumento obtenerNoBorrada(Long idPlantilla) {
        PlantillaDocumento plantilla = plantillaDocumentoRepository.findById(idPlantilla).orElse(null);
        if (plantilla == null || Constantes.REGISTRO_BORRADO.equals(plantilla.getBorrado())) {
            throw new ModeloNotFoundException("Plantilla no encontrada: " + idPlantilla);
        }
        return plantilla;
    }

    private PlantillaVariable obtenerVariable(Long idPlantilla, Long idVariable) {
        PlantillaVariable variable = plantillaVariableRepository.findById(idVariable).orElse(null);
        if (variable == null || !idPlantilla.equals(variable.getIdPlantilla())
                || Constantes.REGISTRO_BORRADO.equals(variable.getBorrado())) {
            throw new ModeloNotFoundException("Variable no encontrada: " + idVariable);
        }
        return variable;
    }

    private static void marcarModificacion(PlantillaDocumento plantilla, Long idUser) {
        LocalDateTime ahora = LocalDateTime.now();
        plantilla.setUpdDate(ahora.toLocalDate());
        plantilla.setUpdDatetime(ahora);
        plantilla.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        plantilla.setUpdUserId(idUser);
    }

    private static String nombreArchivo(MultipartFile archivo) {
        String nombre = archivo.getOriginalFilename();
        return nombre == null ? "" : nombre.trim();
    }

    private static String sha256(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (Exception e) {
            return null;
        }
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
}
