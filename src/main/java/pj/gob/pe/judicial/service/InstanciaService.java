package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.model.sybase.dto.InstanciaBaseDTO;

import java.util.List;

public interface InstanciaService {

    List<DataInstanciaDTO> findActiveInstancias(String SessionId) throws Exception;

    List<DataInstanciaDTO> findAllActiveInstancias() throws Exception;

    List<InstanciaBaseDTO> findInstanciasSijPorSede(String SessionId, String codigoSede) throws Exception;
}
