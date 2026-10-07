package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Plantilla Word completa (flujo de generación por plantilla, sin secciones). Guarda solo la
 * metadata: el binario .docx vive en {@link PlantillaArchivo} para que los listados no carguen
 * el LONGBLOB. Cada Documento tiene como máximo una plantilla no borrada.
 */
@Schema(description = "Plantilla de Documento Model")
@Entity
@Table(name = "PlantillaDocumento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantillaDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlantilla;

    @Schema(description = "ID del Documento al que pertenece la plantilla")
    @Column(name = "idDocumento", nullable = false)
    private Long idDocumento;

    @Schema(description = "Código único de la plantilla")
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Schema(description = "Nombre del archivo de salida (sin extensión)")
    @Column(name = "nombreOut", nullable = true, length = 150)
    private String nombreOut;

    @Schema(description = "Descripción de la plantilla")
    @Column(name = "descripcion", nullable = true, length = 250)
    private String descripcion;

    @Schema(description = "Nombre original del archivo .docx cargado")
    @Column(name = "nombreArchivo", nullable = true, length = 255)
    private String nombreArchivo;

    @Schema(description = "Tamaño del archivo .docx en bytes")
    @Column(name = "tamanioBytes", nullable = true)
    private Long tamanioBytes;

    @Schema(description = "Hash SHA-256 del archivo .docx")
    @Column(name = "hashArchivo", nullable = true, length = 64)
    private String hashArchivo;

    @Schema(description = "Versión del archivo: se incrementa cada vez que se reemplaza el .docx")
    @Column(name = "version", nullable = true)
    private Integer version;

    @Schema(description = "1 si los párrafos con variables se envían a la IA para corrección gramatical")
    @Column(name = "corregirIA", nullable = true)
    private Integer corregirIA;

    @Schema(description = "Fecha de Creación del Registro")
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name="regDate", nullable = true)
    private LocalDate regDate;

    @Schema(description = "Fecha y Hora de Creación del Registro")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name="regDatetime", nullable = true)
    private LocalDateTime regDatetime;

    @Schema(description = "Epoch de Creación del Registro")
    @Column(name="regTimestamp", nullable = true)
    private Long regTimestamp;

    @Schema(description = "Usuario que insertó el registro")
    @Column(name="regUserId", nullable = true)
    private Long regUserId;

    @Schema(description = "Fecha de Edición del Registro")
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name="updDate", nullable = true)
    private LocalDate updDate;

    @Schema(description = "Fecha y Hora de Edición del Registro")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name="updDatetime", nullable = true)
    private LocalDateTime updDatetime;

    @Schema(description = "Epoch de Edición del Registro")
    @Column(name="updTimestamp", nullable = true)
    private Long updTimestamp;

    @Schema(description = "Usuario que editó el registro")
    @Column(name="updUserId", nullable = true)
    private Long updUserId;

    @Schema(description = "Estado del Registro")
    @Column(name="activo", nullable = true)
    private Integer activo;

    @Schema(description = "Borrado Lógico del Registro")
    @Column(name="borrado", nullable = true)
    private Integer borrado;
}
