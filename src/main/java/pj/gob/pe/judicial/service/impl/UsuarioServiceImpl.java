package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.UsuarioDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataUsuarioDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;
import pj.gob.pe.judicial.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Override
    public DataUsuarioDTO findByCredentials(String username, String password) throws Exception {
        return usuarioDAO.findByCredentials(username, password);
    }
}
