package pj.gob.pe.judicial.utils.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input para listar documentos digitales (Resoluciones / Digitalizados) de un expediente")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputDocumentoDigital {

    @Schema(description = "Número único del expediente", example = "2026001660201130")
    @NotNull(message = "El nUnico es obligatorio")
    @JsonProperty("nUnico")
    private Long nUnico;

    @Schema(description = "Número de incidente", example = "0")
    @JsonProperty("nIncidente")
    private String nIncidente = "0";
}
