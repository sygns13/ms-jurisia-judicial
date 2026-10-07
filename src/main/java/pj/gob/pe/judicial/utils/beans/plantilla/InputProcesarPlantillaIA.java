package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Solicitud a POST /v1/gemini-documento/procesar de ms-jurisia-consultaia (una sola llamada por documento). */
@Schema(description = "Input de procesamiento de documento por plantilla con Gemini")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputProcesarPlantillaIA {

    private String sessionUID;
    private Long nUnico;
    private Long idUser;
    private Long idDocumento;
    private String documento;
    private Long idPlantilla;
    private String codigoPlantilla;
    private Integer versionPlantilla;

    @Schema(description = "Resumen del expediente para redactar los bloques de IA")
    private String contextoExpediente;

    @Schema(description = "Párrafos con variables ya reemplazadas, para corrección gramatical")
    private List<ParrafoPlantillaIA> parrafos;

    @Schema(description = "Bloques que la IA debe redactar")
    private List<BloquePlantillaIA> bloques;
}
