package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "Data de Sedes SIJ Model")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSedeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="c_sede")
    private String codigoSede;

    @Column(name="x_desc_sede")
    private String sede;

    @Column(name="l_activo")
    private String activo;

    @Column(name="c_distrito")
    private String codigoDistrito;

    @Column(name="x_direccion")
    private String direccion;
}
