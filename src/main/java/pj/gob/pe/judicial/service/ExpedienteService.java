package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

public interface ExpedienteService {

    List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception;

    List<DataExpedienteDTO> getDataExpediente(Long nUnico,String numIncidente) throws Exception;

    List<CabExpedienteChatDTO> getDataExpedientePorNumero(String numeroExpediente);

    List<DataTipoParteDTO> getPartesByNUnico(Long nUnico);

    List<ResumenExpedienteParteDTO> getResumenExpedienteYPartes(Long nUnico);

}
