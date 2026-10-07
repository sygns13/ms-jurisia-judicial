package pj.gob.pe.judicial.utils.beans.plantilla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** Campo result de POST /v1/gemini-documento/procesar de ms-jurisia-consultaia. */
@Schema(description = "Response del procesamiento de documento por plantilla con Gemini")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProcesarPlantillaIA {

    private String sessionUID;

    @Schema(description = "Solo los párrafos que la IA corrigió (mismo id y misma cantidad de segmentos)")
    private List<ParrafoPlantillaIA> parrafos;

    @Schema(description = "Bloques redactados por la IA")
    private List<BloquePlantillaIA> bloques;

    private Integer parrafosCorregidos;
    private Integer bloquesGenerados;

    private String model;
    private String roleSystem;
    private BigDecimal temperature;
    private Integer configurationsId;
    private String finishReason;

    private Integer promptTokens;
    private Integer candidatesTokens;
    private Integer thoughtsTokens;
    private Integer cachedTokens;
    private Integer totalTokens;

    @Schema(description = "Tiempo de la llamada a Gemini en segundos")
    private Double timeSeconds;
}
