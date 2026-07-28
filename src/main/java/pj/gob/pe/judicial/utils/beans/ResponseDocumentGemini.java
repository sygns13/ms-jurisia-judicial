package pj.gob.pe.judicial.utils.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado del procesamiento de documento por secciones con Gemini, tal como lo devuelve
 * ms-jurisia-consultaia en el campo {@code result} de POST /v1/gemini-chat/documento
 * (equivalente Gemini de {@link ResponseDocument}, sin los campos de tokens de OpenAI).
 */
@Schema(description = "Response del procesamiento de documento con Gemini (consultaia)")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDocumentGemini {

    @Schema(description = "ID de Expediente")
    private Long nUnico;

    @Schema(description = "Template Code")
    private String codeTemplate;

    @Schema(description = "Section Templates con el contenido corregido")
    private List<SectionTemplate> sectionTemplates;

    private String model;
    private String roleSystem;
    private BigDecimal temperature;

    @Schema(description = "Cantidad de secciones enviadas a la IA en esta corrida")
    private Integer seccionesProcesadasIA;

    @Schema(description = "Tiempo total de las llamadas a la IA en segundos")
    private Double timeSeconds;

    private Integer configurationsId;

    @Schema(description = "UUID de la corrida de procesamiento")
    private String sessionUID;
}
