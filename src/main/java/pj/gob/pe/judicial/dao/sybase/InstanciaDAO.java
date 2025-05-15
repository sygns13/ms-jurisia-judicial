package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.List;

public interface InstanciaDAO {

    List<DataInstanciaDTO> findActiveInstancias(UserLogin user) throws Exception;
}
