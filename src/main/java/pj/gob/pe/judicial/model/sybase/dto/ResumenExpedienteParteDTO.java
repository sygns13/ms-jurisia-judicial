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

    // --- Campos añadidos para la opción "Información General" del chatbot ---
    // Se agregan al final para no romper el orden posicional del @AllArgsConstructor
    // que usa el DAO, ni el contrato del endpoint /v1/expedientes/resumen-partes/{nUnico}.

    /** Descripción de la sede (sede.x_desc_sede), no solo el código. */
    private String descSede;

    /** Descripción de la especialidad (especialidad.x_desc_especialidad), no solo el código. */
    private String descEspecialidad;

    /** Número de incidente, extraído de la posición 12 del formato del expediente. */
    private String nIncidente;
}

