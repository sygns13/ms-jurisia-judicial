package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.DataCabExpedienteDTO;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

public interface ExpedienteService {

    List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception;
}
