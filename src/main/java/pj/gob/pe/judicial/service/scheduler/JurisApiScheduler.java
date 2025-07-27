package pj.gob.pe.judicial.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.CabExpedienteChatDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataTipoParteDTO;
import pj.gob.pe.judicial.model.sybase.dto.ResumenExpedienteParteDTO;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.service.externals.ApiChatBotService;
import pj.gob.pe.judicial.utils.beans.chatbot.ApiResponseDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.MainConsultaDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.send.MainConsultaSendDTO;

import java.util.List;

@Component
public class JurisApiScheduler {

    // Se utiliza un logger para un registro más profesional y configurable.
    private static final Logger log = LoggerFactory.getLogger(JurisApiScheduler.class);

    private final ApiChatBotService apiChatBotService;
    private final ExpedienteService expedienteService;

    public JurisApiScheduler(ApiChatBotService apiChatBotService, ExpedienteService expedienteService) {
        this.apiChatBotService = apiChatBotService;
        this.expedienteService = expedienteService;
    }

    /**
     * Tarea programada que se ejecuta a una tasa fija.
     * fixedRate = 1000 significa que se ejecutará cada 1000 milisegundos (1 segundo)
     * después de que la ejecución anterior haya comenzado.
     */
    @Scheduled(fixedDelay = 1500)
    public void checkPendingConsultas() {
        log.info("Ejecutando scheduler para verificar consultas pendientes...");

        // Llama al método del servicio que hace la petición a la API.
        ApiResponseDTO<List<MainConsultaDTO>> response = apiChatBotService.getPendingConsultas();

        // Valida si la respuesta no es nula y si se encontraron items.
        if (response != null && response.isItemFound()) {
            log.info("¡Se encontraron {} consultas pendientes para procesar!", response.getData().size());

            // Aquí puedes iterar sobre los resultados y procesarlos.
            for (MainConsultaDTO consulta : response.getData()) {
                log.debug("Procesando consulta ID: {}, Mensaje: {}", consulta.getId(), consulta.getMessage());
                // TODO: Añadir aquí la lógica de negocio para procesar cada consulta.
                MainConsultaSendDTO mainConsultaSendDTO = new MainConsultaSendDTO();
                mainConsultaSendDTO.setId(consulta.getId());
                mainConsultaSendDTO.setChatId(consulta.getChatId());
                mainConsultaSendDTO.setExpFound(false);

                List<CabExpedienteChatDTO> expedientes = expedienteService.getDataExpedientePorNumero(consulta.getMessage().trim());
                if (expedientes != null && !expedientes.isEmpty()) {
                    mainConsultaSendDTO.setExpFound(true);
                    mainConsultaSendDTO.setCabExpedienteChat(expedientes.getFirst());

                    List<DataTipoParteDTO> partes = expedienteService.getPartesByNUnico(mainConsultaSendDTO.getCabExpedienteChat().getNUnico());
                    List<ResumenExpedienteParteDTO> details = expedienteService.getResumenExpedienteYPartes(mainConsultaSendDTO.getCabExpedienteChat().getNUnico());

                    mainConsultaSendDTO.setListPartes(partes);
                    mainConsultaSendDTO.setDetailsExp(details);
                }
                apiChatBotService.sendProcessedData(mainConsultaSendDTO);
            }

        } else if (response != null) {
            log.info("No se encontraron consultas pendientes en esta ejecución.");
        } else {
            // Esto ocurriría si el método del servicio devuelve null (por un error de red, etc.)
            log.error("La respuesta de la API fue nula. Verifique el servicio JurisApiService.");
        }
    }
}
