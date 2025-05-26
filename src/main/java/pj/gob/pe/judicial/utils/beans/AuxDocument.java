package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Aux Document")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuxDocument {

    private String nombreDoc;
}
