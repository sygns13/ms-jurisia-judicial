package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Resumen de la migración del flujo por secciones al flujo por plantilla")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMigracionPlantillas {

    @Schema(description = "true: solo se simuló, no se escribió nada")
    private Boolean simulacion;

    private Integer documentosEvaluados;
    private Integer creadas;
    private Integer simuladas;
    private Integer omitidas;
    private Integer errores;

    private List<ResultadoMigracionDocumento> detalle = new ArrayList<>();
}
