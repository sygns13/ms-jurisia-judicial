package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminDocumento;

import java.util.List;

public interface DocumentoAdminService {

    List<Documento> listar(String SessionId, Long idTipoDocumento);

    Documento listarPorId(String SessionId, Long idDocumento);

    Documento registrar(String SessionId, InputAdminDocumento input);

    Documento modificar(String SessionId, Long idDocumento, InputAdminDocumento input);

    void eliminar(String SessionId, Long idDocumento);

    Documento altabaja(String SessionId, Long idDocumento, Integer valor);
}
