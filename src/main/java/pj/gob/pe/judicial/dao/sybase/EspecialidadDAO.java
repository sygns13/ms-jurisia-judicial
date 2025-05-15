package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.List;

public interface EspecialidadDAO {

    List<DataEspecialidadDTO> findEspecialidades(UserLogin user) throws Exception;

}
