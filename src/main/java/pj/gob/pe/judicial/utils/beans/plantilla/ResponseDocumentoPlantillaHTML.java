package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Documento generado por plantilla, en HTML para el frontend")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDocumentoPlantillaHTML {

    @Schema(description = "ID de Expediente")
    private Long nUnico;

    @Schema(description = "ID del Documento generado")
    private Long idDocumento;

    @Schema(description = "Código de la plantilla usada")
    private String codigoPlantilla;

    @Schema(description = "Versión de la plantilla usada")
    private Integer versionPlantilla;

    @Schema(description = "Nombre sugerido del archivo")
    private String nombreArchivo;

    @Schema(description = "Contenido HTML del documento")
    private String contentHTML;

    @Schema(description = "Variables que quedaron como '...' por falta de dato")
    private List<String> variablesSinValor;

    @Schema(description = "Intervención de la IA: NO_APLICA, EXITOSO o ERROR")
    private String estadoIA;

    @Schema(description = "Detalle cuando la IA no pudo intervenir (el documento se genera igual, sin corrección)")
    private String mensajeIA;

    @Schema(description = "UUID de la generación (trazabilidad con métricas)")
    private String sessionUID;

    @Schema(description = "Indicador de éxito")
    private Boolean success;
}
