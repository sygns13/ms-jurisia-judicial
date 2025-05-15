package pj.gob.pe.judicial.model.sybase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Data Cabecera de Expediente SIJ Model")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCabExpedienteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="N_UNICO")
    private Long nUnico;

    @Column(name="ANIO")
    private String anio;

    @Column(name="EXPNRO")
    private String numeroExpediente;

    @Column(name="NUM_EXP")
    private String fullNumeroExpediente;

    @Column(name="N_NRO_EXP_ORIGEN")
    private Long numExpOrigen;

    @Column(name="N_ANO_EXP_ORIGEN")
    private Long numAnoExpOrigen;

    @Column(name="C_MATERIA")
    private String materia;

    @Column(name="SEDE")
    private String sede;

    @Column(name="ORGANO")
    private String organo;

    @Column(name="ESPECIALIDAD")
    private String especialidad;

    @Column(name="INSTANCIA")
    private String instancia;

    @Column(name="X_DESC_MATERIA")
    private String descMateria;

    @Column(name="F_INICIO")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column(name="X_DESC_ESTADO")
    private String descEstado;

    @Column(name="C_UBICACION")
    private String ubicacion;

    @Column(name="DESC_UBICACION")
    private String descUbicacion;

    @Column(name="TIPO_EXPEDIENTE")
    private String tipoExpediente;
}
