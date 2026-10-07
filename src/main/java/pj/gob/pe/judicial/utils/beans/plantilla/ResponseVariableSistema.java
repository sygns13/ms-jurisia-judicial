package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Variable del catálogo del sistema, utilizable como ${clave} en cualquier plantilla")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVariableSistema {

    @Schema(description = "Clave a escribir en el Word como ${clave}", example = "juzgado")
    private String clave;

    @Schema(description = "SIJ o CALCULADA", example = "SIJ")
    private String tipo;

    @Schema(description = "Descripción")
    private String descripcion;
}
