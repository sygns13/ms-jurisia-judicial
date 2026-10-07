package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminTipoDocumento;

import java.util.List;

public interface TipoDocumentoAdminService {

    List<TipoDocumento> listar(String SessionId, String idInstancia);

    TipoDocumento listarPorId(String SessionId, Long idTipoDocumento);

    TipoDocumento registrar(String SessionId, InputAdminTipoDocumento input);

    TipoDocumento modificar(String SessionId, Long idTipoDocumento, InputAdminTipoDocumento input);

    void eliminar(String SessionId, Long idTipoDocumento);

    TipoDocumento altabaja(String SessionId, Long idTipoDocumento, Integer valor);
}
