package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;

import java.util.List;

public interface SedeService {

    List<DataSedeDTO> findActiveSedes() throws Exception;
}
