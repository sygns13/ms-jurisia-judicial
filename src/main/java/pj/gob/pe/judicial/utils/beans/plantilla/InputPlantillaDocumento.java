package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input de metadata de Plantilla de Documento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPlantillaDocumento {

    @Schema(description = "ID del Documento al que pertenece la plantilla")
    @NotNull(message = "{input.plantilla.iddocumento.notnull}")
    private Long idDocumento;

    @Schema(description = "Código único de la plantilla", example = "plantilla_auto_admisorio")
    @NotBlank(message = "{input.plantilla.codigo.notnull}")
    @Pattern(regexp = "^[A-Za-z0-9_\\-]{3,50}$", message = "{input.plantilla.codigo.pattern}")
    private String codigo;

    @Schema(description = "Nombre del archivo de salida, sin extensión", example = "AUTO ADMISORIO")
    @Size(max = 150, message = "{input.plantilla.nombreout.size}")
    private String nombreOut;

    @Schema(description = "Descripción de la plantilla")
    @Size(max = 250, message = "{input.plantilla.descripcion.size}")
    private String descripcion;

    @Schema(description = "1 = los párrafos con variables se envían a la IA para corrección gramatical; 0 = no", example = "1")
    @Min(value = 0, message = "{input.plantilla.corregiria.valor}")
    @Max(value = 1, message = "{input.plantilla.corregiria.valor}")
    private Integer corregirIA;
}
