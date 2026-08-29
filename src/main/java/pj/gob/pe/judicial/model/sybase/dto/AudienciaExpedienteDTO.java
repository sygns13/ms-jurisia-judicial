package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audiencia de un expediente, tanto realizada como programada.
 *
 * Ambos casos comparten estructura y se distinguen por {@link #tipoAudiencia}:
 * <ul>
 *   <li>{@code REAL} → audiencia ya realizada ({@code audiencia_programacion.l_estado = 'REAL'}).</li>
 *   <li>{@code PROG} → audiencia programada a futuro.</li>
 * </ul>
 *
 * Al igual que en {@link EscritoExpedienteDTO}, no se expone la ruta FTP del audio/video.
 * El {@link #enlace} sí se envía porque es una URL pública (se registra en el SIJ solo
 * cuando {@code audiencia_video.l_url = 'S'}).
 */
@Schema(description = "Audiencia realizada o programada de un expediente SIJ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudienciaExpedienteDTO {

    // Ver la nota de EscritoExpedienteDTO: en el JSON estos campos viajan como
    // "nunico", "nincidente", "nprogramacion" y "nsala", todo en minúsculas.
    private Long nUnico;

    private String numeroExpediente;

    private String nIncidente;

    private String instancia;
    private String especialista;

    /** "REAL" (realizada) o "PROG" (programada). */
    private String tipoAudiencia;

    private Integer nProgramacion;

    private Integer nSala;

    private String estado;
    private String descripcionAudiencia;

    /**
     * Fecha y hora de la audiencia. Para las realizadas es la fecha de creación del acta
     * y, si no existe acta, la fecha programada; para las próximas, la fecha programada.
     */
    private LocalDateTime fechaAudiencia;

    /** Nombre del archivo del acta. Solo aplica a audiencias realizadas. */
    private String archivoActa;

    /** Nombre del archivo de audio/video. Solo aplica a audiencias realizadas. */
    private String archivoAudio;

    /** URL pública de la grabación, cuando el SIJ la tiene registrada como enlace. */
    private String enlace;
}
