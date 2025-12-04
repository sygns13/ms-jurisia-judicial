package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

public interface ExpedienteDAO {

    List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception;

    List<DataExpedienteDTO> getDataExpediente(Long nUnico, String numIncidente) throws Exception;

    List<CabExpedienteChatDTO> findByNumeroExpediente(String numeroExpediente);

    List<DataTipoParteDTO> findPartesByNUnico(Long nUnico);

    List<ResumenExpedienteParteDTO> getResumenExpedienteYPartes(Long nUnico);

}
