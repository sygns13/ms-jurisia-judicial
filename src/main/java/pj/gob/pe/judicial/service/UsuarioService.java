package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataUsuarioDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;

public interface UsuarioService {

    DataUsuarioDTO findByCredentials(String username, String password) throws Exception;
}
