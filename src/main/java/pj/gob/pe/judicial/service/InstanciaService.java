package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;

import java.util.List;

public interface InstanciaService {

    List<DataInstanciaDTO> findActiveInstancias() throws Exception;
}
