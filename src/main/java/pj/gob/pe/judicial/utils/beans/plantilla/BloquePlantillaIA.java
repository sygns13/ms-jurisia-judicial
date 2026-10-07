package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Bloque que la IA debe redactar para una variable de origen IA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloquePlantillaIA {

    @Schema(description = "Nombre de la variable")
    private String variable;

    @Schema(description = "Instrucción de redacción (en la solicitud)")
    private String instruccion;

    @Schema(description = "Texto redactado por la IA (en la respuesta)")
    private String contenido;
}
