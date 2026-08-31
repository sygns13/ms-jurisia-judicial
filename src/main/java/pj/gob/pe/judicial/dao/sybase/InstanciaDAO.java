package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.model.sybase.dto.InstanciaBaseDTO;
import pj.gob.pe.judicial.model.sybase.dto.SedeBaseDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.List;

public interface InstanciaDAO {

    List<DataInstanciaDTO> findActiveInstancias(UserLogin user) throws Exception;

    List<DataInstanciaDTO> findAllActiveInstancias() throws Exception;

    List<InstanciaBaseDTO> findInstanciasSijPorSede(String codigoSede) throws Exception;
}
