package pj.gob.pe.judicial.model.sybase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Data General de Expediente SIJ Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataExpedienteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="N_UNICO")
    private Long nUnico;

    @Column(name = "X_FORMATO")
    private String formato;

    @Column(name = "x_nom_instancia")
    private String nombreInstancia;

    @Column(name = "c_especialidad")
    private String especialidad;

    @Column(name = "X_DESC_MATERIA")
    private String descMateria;

    @Column(name = "F_INICIO")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;

    @Column(name = "X_DESC_ESTADO")
    private String descEstado;

    @Column(name = "c_ubicacion")
    private String ubicacionCodigo;

    @Column(name = "x_desc_ubicacion")
    private String descUbicacion;

    @Column(name = "usuario_juez")
    private String usuarioJuezCodigo;

    @Column(name = "juez")
    private String nombreJuez;

    @Column(name = "usuario_secretario")
    private String usuarioSecretarioCodigo;

    @Column(name = "secretario")
    private String nombreSecretario;

    @Column(name = "tipo_expediente")
    private String tipoExpediente;

    @Column(name = "parte")
    private String nombreParte;

    @Column(name = "l_tipo_parte")
    private String tipoParteCodigo;

    @Column(name = "x_desc_parte")
    private String descParte;

    @Column(name="DNI_PARTE")
    private String dniParte;

    @Column(name="c_sede")
    private String codSede;

    @Column(name="c_instancia")
    private String codInstancia;

    @Column(name="c_year")
    private String year;

    @Column(name="c_num")
    private String numero;

    @Column(name="c_materia")
    private String codMateria;

    @Column(name="x_desc_sede")
    private String nombreSede;

    @Column(name="x_desc_especialidad")
    private String nombreEspecialidad;
}
