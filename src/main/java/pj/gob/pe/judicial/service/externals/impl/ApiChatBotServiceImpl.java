package pj.gob.pe.judicial.service.externals.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pj.gob.pe.judicial.configuration.ConfigProperties;
import pj.gob.pe.judicial.service.externals.ApiChatBotService;
import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.chatbot.ApiResponseDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.MainConsultaDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.send.MainConsultaSendDTO;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Service
public class ApiChatBotServiceImpl implements ApiChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ApiChatBotServiceImpl.class);

    private final RestClient restClient;
    private final ConfigProperties properties;

    public ApiChatBotServiceImpl(RestClient.Builder builder, ConfigProperties properties) {
        this.properties = properties;

        if(properties.getProxyEnabled()){
            HttpClient httpClient = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress(properties.getProxyURL(), properties.getProxyPort())))
                    //.proxy(ProxySelector.of(new InetSocketAddress("172.17.16.213", 8988)))
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            this.restClient = builder.baseUrl(properties.getUrlApiChatBot())
                    .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                    .build();
        } else {
            this.restClient = builder.baseUrl(properties.getUrlApiChatBot()).build();
        }
    }

    @Override
    public ApiResponseDTO<List<MainConsultaDTO>> getPendingConsultas() {

        String pathGetPendings = properties.getPathGetPendings();
        try {
            // Realiza la petición GET al endpoint especificado.
            return restClient.get()
                    .uri(pathGetPendings) // La URL base ya está en la configuración.
                    .retrieve() // Inicia el proceso de recuperación de la respuesta.
                    .body(new ParameterizedTypeReference<ApiResponseDTO<List<MainConsultaDTO>>>() {});
            // Mapea el cuerpo de la respuesta al DTO genérico.
            // ParameterizedTypeReference es necesario para manejar tipos genéricos como List<T>.

        } catch (Exception e) {
            // Aquí puedes manejar excepciones de red, errores del servidor, etc.
            // Por ejemplo, loggear el error.
            System.err.println("Error al consumir la API de consultas pendientes: " + e.getMessage());
            return null; // O lanzar una excepción personalizada.
        }
    }

    @Override
    public void sendProcessedData(MainConsultaSendDTO data) {
        String pathInfoExpedientes = properties.getPathInfoExpedientes();

        try {
            log.info("Enviando datos procesados para consulta ID: {}", data.getId());
            restClient.post()
                    .uri(pathInfoExpedientes) // Endpoint POST en Laravel
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data)
                    .retrieve()
                    .toBodilessEntity(); // No esperamos un cuerpo de respuesta, solo un código 2xx.

            log.info("Datos para consulta ID: {} enviados exitosamente.", data.getId());
        } catch (Exception e) {
            log.error("Error al enviar datos procesados para consulta ID: {}. Error: {}", data.getId(), e.getMessage());
        }
    }
}
