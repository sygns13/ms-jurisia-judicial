package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;

import java.util.List;

public interface SedeDAO {

    List<DataSedeDTO> findActiveSedes() throws Exception;
}
