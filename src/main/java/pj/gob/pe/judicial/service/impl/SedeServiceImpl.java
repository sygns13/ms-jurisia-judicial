package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.SedeDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.service.SedeService;

import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeDAO sedeDAO;

    @Override
    public List<DataSedeDTO> findActiveSedes() throws Exception {
        return sedeDAO.findActiveSedes();
    }
}
