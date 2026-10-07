package pj.gob.pe.judicial.utils.beans.plantilla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Envoltorio ApiResponse (success/message/result/time) que devuelve consultaia. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseProcesarPlantillaIA {

    private boolean success;
    private String message;
    private ResponseProcesarPlantillaIA result;
    private double time;
}
