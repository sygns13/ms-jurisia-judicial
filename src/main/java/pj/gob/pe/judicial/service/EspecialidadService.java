package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;

import java.util.List;

public interface EspecialidadService {

    List<DataEspecialidadDTO> findEspecialidades() throws Exception;
}
