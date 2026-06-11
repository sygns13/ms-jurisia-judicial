package pj.gob.pe.judicial.utils.beans;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input Data for search expedientes")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class InputCabExpedienteCalifica {
    @Schema(description = "Fecha de inicio (formato yyyy-MM-dd)", example = "2026-06-01")
    @NotBlank(message = "{input.fechaInicio.notblank}")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "{input.fechaInicio.pattern}")
    private String fechaInicio;

    @Schema(description = "Fecha final (formato yyyy-MM-dd)", example = "2026-06-09")
    @NotBlank(message = "{input.fechaFinal.notblank}")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "{input.fechaFinal.pattern}")
    private String fechaFinal;

}
