package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.List;

public interface SedeDAO {

    List<DataSedeDTO> findActiveSedes(UserLogin user) throws Exception;
}
