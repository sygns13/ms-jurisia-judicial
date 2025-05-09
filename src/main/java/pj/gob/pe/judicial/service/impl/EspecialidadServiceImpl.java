package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.EspecialidadDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.service.EspecialidadService;

import java.util.List;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    @Autowired
    private EspecialidadDAO especialidadDAO;

    @Override
    public List<DataEspecialidadDTO> findEspecialidades() throws Exception {
        return especialidadDAO.findEspecialidades();
    }
}
