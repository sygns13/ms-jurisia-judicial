package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.model.sybase.dto.SedeBaseDTO;

import java.util.List;

public interface SedeService {

    List<DataSedeDTO> findActiveSedes(String SessionId) throws Exception;

    List<SedeBaseDTO> getMasterSedes(String SessionId) throws Exception;
}
