package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Documento que puede generarse con el flujo por plantilla (tiene plantilla activa)")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDocumentoGenerable {

    private Long idTipoDocumento;
    private String tipoDocumento;
    private String idInstancia;
    private Long idDocumento;
    private String documento;
    private Long idPlantilla;
    private String codigoPlantilla;
    private String nombreOut;
    private Integer versionPlantilla;
}
