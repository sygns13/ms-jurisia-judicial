package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.DataCabExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

public interface ExpedienteDAO {

    List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception;

    List<DataExpedienteDTO> getDataExpediente(Long nUnico) throws Exception;
}
