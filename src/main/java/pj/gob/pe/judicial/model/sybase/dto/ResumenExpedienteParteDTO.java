package pj.gob.pe.judicial.model.sybase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenExpedienteParteDTO {
    private String numeroExpediente;
    private String instancia;
    private String codigoEspecialidad;
    private String materia;
    private LocalDateTime fechaInicio;
    private String estadoExpediente;
    private String codigoUbicacion;
    private String descripcionUbicacion;
    private String usuarioJuez;
    private String nombreJuez;
    private String usuarioSecretario;
    private String nombreSecretario;
    private String tipoExpediente;
    private String parteNombreCompleto;
    private String tipoParte;
    private String descTipoParte;
}

