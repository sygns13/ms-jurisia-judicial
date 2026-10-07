package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input de configuración de una Variable de Plantilla")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPlantillaVariable {

    @Schema(description = "Nombre de la variable, sin ${ }", example = "fundamento_admision")
    @NotBlank(message = "{input.plantilla.variable.nombre.notnull}")
    @Pattern(regexp = "^[A-Za-z0-9_.\\-]{1,100}$", message = "{input.plantilla.variable.nombre.pattern}")
    private String nombre;

    @Schema(description = "Descripción para el administrador")
    @Size(max = 250, message = "{input.plantilla.variable.descripcion.size}")
    private String descripcion;

    @Schema(description = "Origen del valor: SISTEMA, MANUAL o IA", example = "IA")
    @NotBlank(message = "{input.plantilla.variable.origen.notnull}")
    @Pattern(regexp = "^(SISTEMA|MANUAL|IA)$", message = "{input.plantilla.variable.origen.pattern}")
    private String origen;

    @Schema(description = "Clave del catálogo de variables del sistema (solo SISTEMA; vacío = mismo nombre)", example = "demandantes")
    @Size(max = 100, message = "{input.plantilla.variable.camposistema.size}")
    private String campoSistema;

    @Schema(description = "Valor por defecto (MANUAL, o SISTEMA cuando el SIJ no trae dato)")
    private String valorDefecto;

    @Schema(description = "Instrucción para que la IA redacte el bloque (obligatoria si el origen es IA)")
    private String instruccionIA;

    @Schema(description = "Orden de aparición")
    private Integer orden;
}
