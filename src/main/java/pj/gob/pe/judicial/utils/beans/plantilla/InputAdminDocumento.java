package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input de mantenimiento de Documento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputAdminDocumento {

    @Schema(description = "ID del Tipo de Documento")
    @NotNull(message = "{input.admin.documento.idtipodocumento.notnull}")
    private Long idTipoDocumento;

    @Schema(description = "Descripción del Documento", example = "AUTO ADMISORIO")
    @NotBlank(message = "{input.admin.documento.descripcion.notnull}")
    @Size(max = 150, message = "{input.admin.documento.descripcion.size}")
    private String descripcion;

    @Schema(description = "Código del template del flujo por secciones (opcional; vacío para documentos del flujo por plantilla)")
    @Size(max = 50, message = "{input.admin.documento.codigotemplate.size}")
    private String codigoTemplate;
}
