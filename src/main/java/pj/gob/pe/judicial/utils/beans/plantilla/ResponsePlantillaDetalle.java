package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;

import java.util.List;

@Schema(description = "Plantilla con sus variables configuradas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePlantillaDetalle {

    @Schema(description = "Metadata de la plantilla")
    private PlantillaDocumento plantilla;

    @Schema(description = "Variables configuradas (no borradas)")
    private List<PlantillaVariable> variables;

    @Schema(description = "Variables ${...} encontradas en el .docx vigente, en orden de aparición")
    private List<String> variablesDetectadas;

    @Schema(description = "Observaciones para el administrador (variables sin definir, IA sin instrucción, etc.)")
    private List<String> advertencias;
}
