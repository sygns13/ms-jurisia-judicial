package pj.gob.pe.judicial.service.externals.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pj.gob.pe.judicial.configuration.ConfigProperties;
import pj.gob.pe.judicial.exception.AuthOpenAIException;
import pj.gob.pe.judicial.service.externals.ConsultaiaPlantillaService;
import pj.gob.pe.judicial.utils.beans.plantilla.ApiResponseProcesarPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.InputProcesarPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseProcesarPlantillaIA;

/**
 * Consumo de POST /v1/gemini-documento/procesar de ms-jurisia-consultaia. Mismo patrón que
 * {@link ConsultaiaGeminiServiceImpl}: el endpoint responde envuelto en ApiResponse y ante fallos
 * controlados devuelve 500 con el message, que se propaga como RuntimeException.
 */
@Service
public class ConsultaiaPlantillaServiceImpl implements ConsultaiaPlantillaService {

    private final RestClient restClient;
    private final ConfigProperties properties;

    public ConsultaiaPlantillaServiceImpl(RestClient.Builder builder, ConfigProperties properties) {
        this.restClient = builder.baseUrl(properties.getUrlConsultaia()).build();
        this.properties = properties;
    }

    @Override
    public ResponseProcesarPlantillaIA ProcesarPlantillaGemini(InputProcesarPlantillaIA input, String SessionId) {

        ApiResponseProcesarPlantillaIA apiResponse = restClient.post()
                .uri(properties.getPathProcessPlantillaGemini())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("SessionId", SessionId)
                .body(input)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AuthOpenAIException("Error de Procesamiento de IA");
                })
                // Handler vacío: el 500 trae ApiResponse.error(message) y se lee abajo
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                })
                .body(ApiResponseProcesarPlantillaIA.class);

        if (apiResponse == null) {
            throw new RuntimeException("Error del servidor, Comunicarse con el administrador");
        }

        if (!apiResponse.isSuccess() || apiResponse.getResult() == null) {
            String message = apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()
                    ? apiResponse.getMessage()
                    : "Error del servidor, Comunicarse con el administrador";
            throw new RuntimeException(message);
        }

        return apiResponse.getResult();
    }
}
