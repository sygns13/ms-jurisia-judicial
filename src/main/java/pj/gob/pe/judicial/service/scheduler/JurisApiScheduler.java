package pj.gob.pe.judicial.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pj.gob.pe.judicial.model.sybase.dto.AudienciaExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.CabExpedienteChatDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataTipoParteDTO;
import pj.gob.pe.judicial.model.sybase.dto.EscritoExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.ResumenExpedienteParteDTO;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.service.externals.ApiChatBotService;
import pj.gob.pe.judicial.utils.beans.chatbot.ApiResponseDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.MainConsultaDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.send.MainConsultaSendDTO;

import java.util.ArrayList;
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
                log.info("Procesando consulta ID: {}, Mensaje: {}", consulta.getId(), consulta.getMessage());

                if (consulta == null || consulta.getId() == null || consulta.getMessage() == null || consulta.getMessage().isEmpty()){

                    break;
                }


                // TODO: Añadir aquí la lógica de negocio para procesar cada consulta.
                MainConsultaSendDTO mainConsultaSendDTO = new MainConsultaSendDTO();
                mainConsultaSendDTO.setId(consulta.getId());
                mainConsultaSendDTO.setChatId(consulta.getChatId());
                mainConsultaSendDTO.setExpFound(false);

                String numeroExpediente = consulta.getMessage().trim();

                List<CabExpedienteChatDTO> expedientes = expedienteService.getDataExpedientePorNumero(numeroExpediente);
                if (expedientes != null && !expedientes.isEmpty()) {
                    mainConsultaSendDTO.setExpFound(true);
                    mainConsultaSendDTO.setCabExpedienteChat(expedientes.getFirst());

                    List<DataTipoParteDTO> partes = expedienteService.getPartesByNUnico(mainConsultaSendDTO.getCabExpedienteChat().getNUnico());

                    // Se consulta por número de expediente y no por n_unico: el número fija
                    // también el incidente, mientras que n_unico agrupa todos los incidentes
                    // y mezclaría el detalle de unos con otros.
                    List<ResumenExpedienteParteDTO> details = expedienteService.getResumenExpedienteYPartesPorNumero(numeroExpediente);

                    mainConsultaSendDTO.setListPartes(partes);
                    mainConsultaSendDTO.setDetailsExp(details);

                    // Escritos y audiencias para las opciones "Detalle de Escritos",
                    // "Audiencias Realizadas" y "Próximas Audiencias" del bot.
                    cargarEscritosYAudiencias(mainConsultaSendDTO, numeroExpediente);
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

    /**
     * Carga escritos y audiencias del expediente en el DTO de envío.
     *
     * Cada bloque va en su propio try: si una de las consultas al SIJ falla, el resto de la
     * información igual llega al bot. Fallar por completo dejaría al ciudadano esperando una
     * respuesta que nunca llega, porque este es el único momento en que Laravel recibe datos.
     */
    private void cargarEscritosYAudiencias(MainConsultaSendDTO destino, String numeroExpediente) {

        try {
            List<EscritoExpedienteDTO> escritos = expedienteService.getEscritosPorNumero(numeroExpediente);
            if (escritos != null) {
                destino.setListEscritos(escritos);
            }
        } catch (Exception e) {
            log.error("No se pudieron recuperar los escritos del expediente {}: {}", numeroExpediente, e.getMessage());
        }

        // Realizadas y próximas viajan en una sola lista; el bot las separa por tipoAudiencia.
        List<AudienciaExpedienteDTO> audiencias = new ArrayList<>();

        try {
            List<AudienciaExpedienteDTO> realizadas = expedienteService.getAudienciasRealizadasPorNumero(numeroExpediente);
            if (realizadas != null) {
                audiencias.addAll(realizadas);
            }
        } catch (Exception e) {
            log.error("No se pudieron recuperar las audiencias realizadas del expediente {}: {}", numeroExpediente, e.getMessage());
        }

        try {
            List<AudienciaExpedienteDTO> proximas = expedienteService.getAudienciasProximasPorNumero(numeroExpediente);
            if (proximas != null) {
                audiencias.addAll(proximas);
            }
        } catch (Exception e) {
            log.error("No se pudieron recuperar las próximas audiencias del expediente {}: {}", numeroExpediente, e.getMessage());
        }

        destino.setListAudiencias(audiencias);

        log.info("Expediente {}: {} escritos y {} audiencias recuperados.",
                numeroExpediente, destino.getListEscritos().size(), audiencias.size());
    }
}
