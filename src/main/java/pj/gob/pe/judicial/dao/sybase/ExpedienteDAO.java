package pj.gob.pe.judicial.dao.sybase;

import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.utils.beans.UserLogin;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;
import pj.gob.pe.judicial.utils.beans.InputCabExpedienteCalifica;

import java.util.List;

public interface ExpedienteDAO {

    List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception;

    List<DataCabExpedienteCalificarDTO> findCabExpedientesCalificar(InputCabExpedienteCalifica input, UserLogin user) throws Exception;

    List<DataCabExpedienteCalificarDTO> findCabExpedientesSentenciarPorNunico(Long nUnico) throws Exception;

    List<DataDocumentoDigitalDTO> findDocumentosDigitales(Long nUnico, String nIncidente, java.util.List<String> tiposDoc) throws Exception;

    List<DataExpedienteDTO> getDataExpediente(Long nUnico, String numIncidente) throws Exception;

    List<CabExpedienteChatDTO> findByNumeroExpediente(String numeroExpediente);

    List<DataTipoParteDTO> findPartesByNUnico(Long nUnico);

    List<ResumenExpedienteParteDTO> getResumenExpedienteYPartes(Long nUnico);

    /** Igual que el anterior, pero identificando el expediente por su número completo. */
    List<ResumenExpedienteParteDTO> getResumenExpedienteYPartesPorNumero(String numeroExpediente);

    // --- Consultas del chatbot. Se identifican por número de expediente (x_formato)
    //     porque es el dato que ingresa el ciudadano y además fija el incidente. ---

    List<EscritoExpedienteDTO> findEscritosByExpediente(String numeroExpediente);

    List<AudienciaExpedienteDTO> findAudienciasRealizadas(String numeroExpediente);

    List<AudienciaExpedienteDTO> findAudienciasProximas(String numeroExpediente);

}
