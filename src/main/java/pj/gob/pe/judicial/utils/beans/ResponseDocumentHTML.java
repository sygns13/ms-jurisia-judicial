package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response Document Frontend HTML")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDocumentHTML {

    @Schema(description = "ID de Expediente")
    private Long nUnico;

    @Schema(description = "Template Code")
    private String codeTemplate;

    @Schema(description = "Contenido HTML del Documento")
    private String contentHTML;

    @Schema(description = "Mensaje de Respuesta")
    private Boolean success;
}
