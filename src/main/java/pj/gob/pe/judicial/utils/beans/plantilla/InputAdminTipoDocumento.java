package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input de mantenimiento de Tipo de Documento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputAdminTipoDocumento {

    @Schema(description = "Código de Instancia del SIJ", example = "701")
    @NotBlank(message = "{input.admin.tipodocumento.idinstancia.notnull}")
    @Size(max = 10, message = "{input.admin.tipodocumento.idinstancia.size}")
    private String idInstancia;

    @Schema(description = "Descripción del Tipo de Documento", example = "AUTO")
    @NotBlank(message = "{input.admin.tipodocumento.descripcion.notnull}")
    @Size(max = 150, message = "{input.admin.tipodocumento.descripcion.size}")
    private String descripcion;
}
