package pj.gob.pe.judicial.service.externals.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pj.gob.pe.judicial.configuration.ConfigProperties;
import pj.gob.pe.judicial.exception.AuthOpenAIException;
import pj.gob.pe.judicial.service.externals.ConsultaiaService;
import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocument;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

@Service
public class ConsultaiaServiceImpl implements ConsultaiaService {

    private final RestClient restClient;
    private final ConfigProperties properties;

    public ConsultaiaServiceImpl(RestClient.Builder builder, ConfigProperties properties) {
        this.restClient = builder.baseUrl(properties.getUrlConsultaia()).build();
        this.properties = properties;
    }

    @Override
    public ResponseDocument ProcessDocument(InputDocument inputDocument) {

        String pathProcessDocument = properties.getPathProcessDocument();

        return restClient.post()
                .uri(pathProcessDocument)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(inputDocument)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AuthOpenAIException("Error de Procesamiento de IA");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RuntimeException("Error del servidor, Comunicarse con el administrador");
                })
                .body(ResponseDocument.class); // Se convierte automáticamente el JSON a la clase Java
    }
}
