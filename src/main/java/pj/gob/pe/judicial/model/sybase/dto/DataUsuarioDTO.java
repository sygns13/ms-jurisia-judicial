package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "Data Usuario SIJ Model")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataUsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="c_dni")
    private String dni;

    @Column(name="c_ape_paterno")
    private String apePaterno;

    @Column(name="c_ape_materno")
    private String apeMaterno;

    @Column(name="c_nombres")
    private String nombres;

    @Column(name="c_usuario")
    private String usuario;

    @Column(name="desper")
    private String desper;

    @Column(name="c_instanciacheck")
    private Short instanciaCheck;
}
