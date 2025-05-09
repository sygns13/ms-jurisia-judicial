package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.InstanciaDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.service.InstanciaService;

import java.util.List;

@Service
public class InstanciaServiceImpl implements InstanciaService {

    @Autowired
    private InstanciaDAO instanciaDAO;

    @Override
    public List<DataInstanciaDTO> findActiveInstancias() throws Exception {
        return instanciaDAO.findActiveInstancias();
    }
}
