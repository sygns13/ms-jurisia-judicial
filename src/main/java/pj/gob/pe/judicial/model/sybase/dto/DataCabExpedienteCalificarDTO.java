package pj.gob.pe.judicial.model.sybase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Data Cabecera de Expediente a Calificar SIJ Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCabExpedienteCalificarDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name="N_UNICO")
    private Long nUnico;

    @Column(name = "ANIO")
    private String anio;

    @Column(name = "EXPNRO")
    private String expNro;

    @Column(name = "X_FORMATO")
    private String xFormato;

    @Column(name = "C_MATERIA")
    private String cMateria;

    @Column(name = "C_ESPECIALIDAD")
    private String cEspecialidad;

    @Column(name = "C_INSTANCIA")
    private String cInstancia;

    @Column(name = "X_NOM_INSTANCIA")
    private String xNomInstancia;

    @Column(name = "X_DESC_MATERIA")
    private String xDescMateria;

    @Column(name = "F_INICIO")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fInicio;

    @Column(name = "X_DESC_ESTADO")
    private String xDescEstado;

    @Column(name = "C_UBICACION")
    private String cUbicacion;

    @Column(name = "X_DESC_UBICACION")
    private String xDescUbicacion;

    @Column(name = "TIPO_EXPEDIENTE")
    private String tipoExpediente;

    @Column(name = "X_IP")
    private String xIp;

    @Column(name = "C_USUARIO")
    private String cUsuario;

    @Column(name = "C_CLAVE")
    private String cClave;

    @Column(name = "X_RUTA_ARCHIVO")
    private String xRutaArchivo;

    @Column(name = "X_NOMBRE_ARCHIVO")
    private String xNombreArchivo;

    @Column(name = "RUTA_COMPLETA")
    private String rutaCompleta;

    @Column(name = "N_INCIDENTE")
    private String nIncidente;
}
