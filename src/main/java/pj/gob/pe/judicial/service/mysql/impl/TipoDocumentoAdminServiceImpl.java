package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationServiceException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.repository.mysql.DocumentoAdminRepository;
import pj.gob.pe.judicial.repository.mysql.TipoDocumentoAdminRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.TipoDocumentoAdminService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminTipoDocumento;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoAdminServiceImpl implements TipoDocumentoAdminService {

    private final TipoDocumentoAdminRepository tipoDocumentoAdminRepository;
    private final DocumentoAdminRepository documentoAdminRepository;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumento> listar(String SessionId, String idInstancia) {

        validarSesion(SessionId);

        if (idInstancia != null && !idInstancia.isBlank()) {
            return tipoDocumentoAdminRepository.findByIdInstanciaAndBorradoOrderByDescripcionAsc(idInstancia.trim(), Constantes.REGISTRO_NO_BORRADO);
        }
        return tipoDocumentoAdminRepository.findByBorradoOrderByIdInstanciaAscDescripcionAsc(Constantes.REGISTRO_NO_BORRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDocumento listarPorId(String SessionId, Long idTipoDocumento) {

        validarSesion(SessionId);

        return obtenerNoBorrado(idTipoDocumento);
    }

    @Override
    @Transactional
    public TipoDocumento registrar(String SessionId, InputAdminTipoDocumento input) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        String idInstancia = input.getIdInstancia().trim();
        String descripcion = input.getDescripcion().trim();

        if (tipoDocumentoAdminRepository.existsByIdInstanciaAndDescripcionAndBorrado(idInstancia, descripcion, Constantes.REGISTRO_NO_BORRADO)) {
            throw new ValidationServiceException("Ya existe el Tipo de Documento " + descripcion + " para la instancia " + idInstancia);
        }

        LocalDateTime ahora = LocalDateTime.now();

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setIdInstancia(idInstancia);
        tipoDocumento.setDescripcion(descripcion);
        tipoDocumento.setRegDate(ahora.toLocalDate());
        tipoDocumento.setRegDatetime(ahora);
        tipoDocumento.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        tipoDocumento.setRegUserId(responseLogin.getUser().getIdUser());
        tipoDocumento.setActivo(Constantes.REGISTRO_ACTIVO);
        tipoDocumento.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        return tipoDocumentoAdminRepository.save(tipoDocumento);
    }

    @Override
    @Transactional
    public TipoDocumento modificar(String SessionId, Long idTipoDocumento, InputAdminTipoDocumento input) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        TipoDocumento tipoDocumento = obtenerNoBorrado(idTipoDocumento);

        String idInstancia = input.getIdInstancia().trim();
        String descripcion = input.getDescripcion().trim();

        if (tipoDocumentoAdminRepository.existsByIdInstanciaAndDescripcionAndBorradoAndIdTipoDocumentoNot(
                idInstancia, descripcion, Constantes.REGISTRO_NO_BORRADO, idTipoDocumento)) {
            throw new ValidationServiceException("Ya existe el Tipo de Documento " + descripcion + " para la instancia " + idInstancia);
        }

        LocalDateTime ahora = LocalDateTime.now();

        tipoDocumento.setIdInstancia(idInstancia);
        tipoDocumento.setDescripcion(descripcion);
        tipoDocumento.setUpdDate(ahora.toLocalDate());
        tipoDocumento.setUpdDatetime(ahora);
        tipoDocumento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        tipoDocumento.setUpdUserId(responseLogin.getUser().getIdUser());

        return tipoDocumentoAdminRepository.save(tipoDocumento);
    }

    @Override
    @Transactional
    public void eliminar(String SessionId, Long idTipoDocumento) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        TipoDocumento tipoDocumento = obtenerNoBorrado(idTipoDocumento);

        long documentos = documentoAdminRepository.countByIdTipoDocumentoAndBorrado(idTipoDocumento, Constantes.REGISTRO_NO_BORRADO);
        if (documentos > 0) {
            throw new ValidationServiceException("No se puede eliminar el Tipo de Documento: tiene " + documentos + " documento(s) registrados");
        }

        LocalDateTime ahora = LocalDateTime.now();

        tipoDocumento.setActivo(Constantes.REGISTRO_INACTIVO);
        tipoDocumento.setBorrado(Constantes.REGISTRO_BORRADO);
        tipoDocumento.setUpdDate(ahora.toLocalDate());
        tipoDocumento.setUpdDatetime(ahora);
        tipoDocumento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        tipoDocumento.setUpdUserId(responseLogin.getUser().getIdUser());

        tipoDocumentoAdminRepository.save(tipoDocumento);
    }

    @Override
    @Transactional
    public TipoDocumento altabaja(String SessionId, Long idTipoDocumento, Integer valor) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        if (!Constantes.REGISTRO_ACTIVO.equals(valor) && !Constantes.REGISTRO_INACTIVO.equals(valor)) {
            throw new ValidationServiceException("El valor de activación debe ser 0 o 1");
        }

        TipoDocumento tipoDocumento = obtenerNoBorrado(idTipoDocumento);

        LocalDateTime ahora = LocalDateTime.now();

        tipoDocumento.setActivo(valor);
        tipoDocumento.setUpdDate(ahora.toLocalDate());
        tipoDocumento.setUpdDatetime(ahora);
        tipoDocumento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        tipoDocumento.setUpdUserId(responseLogin.getUser().getIdUser());

        return tipoDocumentoAdminRepository.save(tipoDocumento);
    }

    private TipoDocumento obtenerNoBorrado(Long idTipoDocumento) {
        TipoDocumento tipoDocumento = tipoDocumentoAdminRepository.findById(idTipoDocumento).orElse(null);
        if (tipoDocumento == null || Constantes.REGISTRO_BORRADO.equals(tipoDocumento.getBorrado())) {
            throw new ModeloNotFoundException("Tipo de Documento no encontrado: " + idTipoDocumento);
        }
        return tipoDocumento;
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
