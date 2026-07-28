package pj.gob.pe.judicial.service.externals.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pj.gob.pe.judicial.configuration.ConfigProperties;
import pj.gob.pe.judicial.exception.AuthOpenAIException;
import pj.gob.pe.judicial.service.externals.ConsultaiaGeminiService;
import pj.gob.pe.judicial.utils.beans.ApiResponseDocumentGemini;
import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocumentGemini;

/**
 * Consumo de POST /v1/gemini-chat/documento de ms-jurisia-consultaia. Mismo patrón que
 * {@link ConsultaiaServiceImpl} (RestClient sobre la URL base de consultaia), con la
 * diferencia de que el endpoint Gemini responde envuelto en ApiResponse
 * (success/message/result/time), por lo que aquí se desenvuelve y se valida el success.
 */
@Service
public class ConsultaiaGeminiServiceImpl implements ConsultaiaGeminiService {

    private final RestClient restClient;
    private final ConfigProperties properties;

    public ConsultaiaGeminiServiceImpl(RestClient.Builder builder, ConfigProperties properties) {
        this.restClient = builder.baseUrl(properties.getUrlConsultaia()).build();
        this.properties = properties;
    }

    @Override
    public ResponseDocumentGemini ProcessDocumentGemini(InputDocument inputDocument) {

        String pathProcessDocumentGemini = properties.getPathProcessDocumentGemini();

        ApiResponseDocumentGemini apiResponse = restClient.post()
                .uri(pathProcessDocumentGemini)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(inputDocument)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AuthOpenAIException("Error de Procesamiento de IA");
                })
                // El endpoint Gemini devuelve 500 con ApiResponse.error(message) ante fallos
                // controlados: handler vacío para no cortar aquí y poder leer el message del body.
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                })
                .body(ApiResponseDocumentGemini.class);

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
