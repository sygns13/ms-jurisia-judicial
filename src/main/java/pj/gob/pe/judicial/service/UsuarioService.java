package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;

public interface UsuarioService {

    UsuarioDTO findByCredentials(String username, String password) throws Exception;
}
