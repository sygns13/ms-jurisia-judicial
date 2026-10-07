package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationServiceException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.repository.mysql.DocumentoAdminRepository;
import pj.gob.pe.judicial.repository.mysql.PlantillaDocumentoRepository;
import pj.gob.pe.judicial.repository.mysql.TipoDocumentoAdminRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.DocumentoAdminService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminDocumento;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoAdminServiceImpl implements DocumentoAdminService {

    private final DocumentoAdminRepository documentoAdminRepository;
    private final TipoDocumentoAdminRepository tipoDocumentoAdminRepository;
    private final PlantillaDocumentoRepository plantillaDocumentoRepository;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public List<Documento> listar(String SessionId, Long idTipoDocumento) {

        validarSesion(SessionId);

        if (idTipoDocumento != null && idTipoDocumento > 0) {
            return documentoAdminRepository.findByIdTipoDocumentoAndBorradoOrderByDescripcionAsc(idTipoDocumento, Constantes.REGISTRO_NO_BORRADO);
        }
        return documentoAdminRepository.findByBorradoOrderByIdTipoDocumentoAscDescripcionAsc(Constantes.REGISTRO_NO_BORRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public Documento listarPorId(String SessionId, Long idDocumento) {

        validarSesion(SessionId);

        return obtenerNoBorrado(idDocumento);
    }

    @Override
    @Transactional
    public Documento registrar(String SessionId, InputAdminDocumento input) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        validarTipoDocumento(input.getIdTipoDocumento());

        String descripcion = input.getDescripcion().trim();

        if (documentoAdminRepository.existsByIdTipoDocumentoAndDescripcionAndBorrado(input.getIdTipoDocumento(), descripcion, Constantes.REGISTRO_NO_BORRADO)) {
            throw new ValidationServiceException("Ya existe el Documento " + descripcion + " para el Tipo de Documento indicado");
        }

        LocalDateTime ahora = LocalDateTime.now();

        Documento documento = new Documento();
        documento.setIdTipoDocumento(input.getIdTipoDocumento());
        documento.setDescripcion(descripcion);
        documento.setCodigoTemplate(vacioANulo(input.getCodigoTemplate()));
        documento.setRegDate(ahora.toLocalDate());
        documento.setRegDatetime(ahora);
        documento.setRegTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        documento.setRegUserId(responseLogin.getUser().getIdUser());
        documento.setActivo(Constantes.REGISTRO_ACTIVO);
        documento.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        return documentoAdminRepository.save(documento);
    }

    @Override
    @Transactional
    public Documento modificar(String SessionId, Long idDocumento, InputAdminDocumento input) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        Documento documento = obtenerNoBorrado(idDocumento);

        validarTipoDocumento(input.getIdTipoDocumento());

        String descripcion = input.getDescripcion().trim();

        if (documentoAdminRepository.existsByIdTipoDocumentoAndDescripcionAndBorradoAndIdDocumentoNot(
                input.getIdTipoDocumento(), descripcion, Constantes.REGISTRO_NO_BORRADO, idDocumento)) {
            throw new ValidationServiceException("Ya existe el Documento " + descripcion + " para el Tipo de Documento indicado");
        }

        LocalDateTime ahora = LocalDateTime.now();

        documento.setIdTipoDocumento(input.getIdTipoDocumento());
        documento.setDescripcion(descripcion);
        documento.setCodigoTemplate(vacioANulo(input.getCodigoTemplate()));
        documento.setUpdDate(ahora.toLocalDate());
        documento.setUpdDatetime(ahora);
        documento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        documento.setUpdUserId(responseLogin.getUser().getIdUser());

        return documentoAdminRepository.save(documento);
    }

    @Override
    @Transactional
    public void eliminar(String SessionId, Long idDocumento) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        Documento documento = obtenerNoBorrado(idDocumento);

        if (!plantillaDocumentoRepository.findByIdDocumentoAndBorrado(idDocumento, Constantes.REGISTRO_NO_BORRADO).isEmpty()) {
            throw new ValidationServiceException("No se puede eliminar el Documento: tiene una plantilla registrada. Elimine primero la plantilla");
        }

        LocalDateTime ahora = LocalDateTime.now();

        documento.setActivo(Constantes.REGISTRO_INACTIVO);
        documento.setBorrado(Constantes.REGISTRO_BORRADO);
        documento.setUpdDate(ahora.toLocalDate());
        documento.setUpdDatetime(ahora);
        documento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        documento.setUpdUserId(responseLogin.getUser().getIdUser());

        documentoAdminRepository.save(documento);
    }

    @Override
    @Transactional
    public Documento altabaja(String SessionId, Long idDocumento, Integer valor) {

        ResponseLogin responseLogin = validarSesion(SessionId);

        if (!Constantes.REGISTRO_ACTIVO.equals(valor) && !Constantes.REGISTRO_INACTIVO.equals(valor)) {
            throw new ValidationServiceException("El valor de activación debe ser 0 o 1");
        }

        Documento documento = obtenerNoBorrado(idDocumento);

        LocalDateTime ahora = LocalDateTime.now();

        documento.setActivo(valor);
        documento.setUpdDate(ahora.toLocalDate());
        documento.setUpdDatetime(ahora);
        documento.setUpdTimestamp(ahora.toEpochSecond(ZoneOffset.UTC));
        documento.setUpdUserId(responseLogin.getUser().getIdUser());

        return documentoAdminRepository.save(documento);
    }

    private void validarTipoDocumento(Long idTipoDocumento) {
        TipoDocumento tipoDocumento = tipoDocumentoAdminRepository.findById(idTipoDocumento).orElse(null);
        if (tipoDocumento == null || Constantes.REGISTRO_BORRADO.equals(tipoDocumento.getBorrado())) {
            throw new ValidationServiceException("El Tipo de Documento " + idTipoDocumento + " no existe");
        }
    }

    private Documento obtenerNoBorrado(Long idDocumento) {
        Documento documento = documentoAdminRepository.findById(idDocumento).orElse(null);
        if (documento == null || Constantes.REGISTRO_BORRADO.equals(documento.getBorrado())) {
            throw new ModeloNotFoundException("Documento no encontrado: " + idDocumento);
        }
        return documento;
    }

    private static String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
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
