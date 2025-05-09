package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "Data de Especialidad SIJ Model")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEspecialidadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="c_especialidad")
    private String codigoEspecialidad;

    @Column(name="x_desc_especialidad")
    private String especialidad;

    @Column(name="c_cod_especialidad")
    private String codigoCodEspecialidad;
}
