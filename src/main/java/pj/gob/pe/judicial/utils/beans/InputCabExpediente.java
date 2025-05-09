package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input Data for search expedientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputCabExpediente {

    @Schema(description = "Sede")
    @NotNull( message = "{input.sede.notnull}")
    @Size(min = 1, max = 20, message = "{input.sede.size}")
    private String sede;

    @Schema(description = "Instancia")
    @NotNull( message = "{input.instancia.notnull}")
    @Size(min = 1, max = 20, message = "{input.instancia.size}")
    private String instancia;

    @Schema(description = "Instancia")
    @NotNull( message = "{input.especialidad.notnull}")
    @Size(min = 1, max = 20, message = "{input.especialidad.size}")
    private String especialidad;

    @Schema(description = "Numero")
    @NotNull( message = "{input.numero.notnull}")
    private int numero;

    @Schema(description = "Año")
    @NotNull( message = "{input.anio.notnull}")
    private int anio;
}
