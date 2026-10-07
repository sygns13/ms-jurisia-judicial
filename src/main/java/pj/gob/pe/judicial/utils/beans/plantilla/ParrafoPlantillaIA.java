package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Párrafo enviado a la IA para corrección. El texto va partido en segmentos (uno por w:t del Word)
 * y la IA debe devolver la misma cantidad de segmentos: así la corrección se aplica sin perder el
 * formato (negritas, subrayados) de cada tramo.
 */
@Schema(description = "Párrafo para corrección con IA, partido por segmentos de formato")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParrafoPlantillaIA {

    @Schema(description = "Identificador del párrafo dentro de la generación")
    private Integer id;

    @Schema(description = "Segmentos de texto del párrafo, en orden")
    private List<String> segmentos;
}
