package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataUsuarioDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;

public interface UsuarioDAO {

    DataUsuarioDTO findByCredentials(String username, String password) throws Exception;
}
