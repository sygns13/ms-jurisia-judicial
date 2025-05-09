package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;

import java.util.List;

public interface InstanciaDAO {

    List<DataInstanciaDTO> findActiveInstancias() throws Exception;
}
