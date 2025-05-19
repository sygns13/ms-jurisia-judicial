package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;

import java.util.List;

public interface TipoDocumentoService {

    List<TipoDocumento> listarAll();

    List<TipoDocumento> listarByInstancia(String instancia);

    List<TipoDocumento> listarActivosNoBorrados();
}
