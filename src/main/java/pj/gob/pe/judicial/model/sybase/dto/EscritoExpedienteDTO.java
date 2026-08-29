package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Escrito presentado en un expediente, para la opción "Detalle de Escritos" del chatbot.
 *
 * No se expone la ruta FTP completa ({@code ftp://usuario:clave@ip/...}) que sí construye la
 * consulta original del SIJ: esa cadena lleva las credenciales del servidor FTP institucional
 * embebidas y estos datos terminan almacenados en la base de datos del hosting compartido.
 * Solo se envía el nombre del archivo de la resolución que atendió el escrito.
 */
@Schema(description = "Escrito presentado en un expediente SIJ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscritoExpedienteDTO {

    // OJO con los nombres en el JSON: Jackson serializa getNUnico() como "nunico" y
    // getNIncidente() como "nincidente" (minúsculas), por el mangling de mayúsculas
    // iniciales consecutivas. Es la misma convención que ya consume ApiController de
    // Laravel para cabExpedienteChat y listPartes.
    //
    // No se anotan con @JsonProperty: sobre el campo, y con los getters que genera Lombok,
    // Jackson termina emitiendo la clave DOS veces ("nunico" y "nUnico").
    private Long nUnico;

    private String numeroExpediente;

    private String nIncidente;

    private String instancia;
    private String especialista;

    /** Número de escrito: secuencia de ingreso + año de ingreso (ej. "25820-2026"). */
    private String nroEscrito;

    private LocalDateTime fechaEscrito;

    /** Fecha en que el juzgado atendió el escrito; null mientras siga pendiente. */
    private LocalDateTime fechaAtencion;

    /** Resolución que atendió el escrito; null cuando aún no ha sido proveído. */
    private String resolucion;

    private String sumilla;

    /** Nombre del archivo de la resolución de atención, si está digitalizada. */
    private String nombreArchivo;
}
