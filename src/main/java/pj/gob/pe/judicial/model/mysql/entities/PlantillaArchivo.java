package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Binario .docx de la plantilla vigente (relación 1 a 1 con {@link PlantillaDocumento}, misma PK).
 * Se separa de la metadata para que los listados de plantillas no carguen el LONGBLOB.
 */
@Schema(description = "Archivo .docx de Plantilla Model")
@Entity
@Table(name = "PlantillaArchivo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantillaArchivo {

    @Id
    @Schema(description = "ID de la Plantilla (misma PK que PlantillaDocumento)")
    @Column(name = "idPlantilla", nullable = false)
    private Long idPlantilla;

    @Schema(description = "Contenido binario del archivo .docx")
    @ToString.Exclude
    @Lob
    @Column(name = "archivo", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] archivo;

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
}
