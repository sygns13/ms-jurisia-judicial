package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Input Usuario Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputUsuario {

    private String username;
    private String password;
}
