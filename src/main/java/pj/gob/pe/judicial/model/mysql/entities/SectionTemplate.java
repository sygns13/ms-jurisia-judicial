package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Sections of Template Model")
@Entity
@Table(name = "SectionTemplates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID de Template")
    @Column(name = "idTemplate", nullable = false)
    private Long idTemplate;

    @Schema(description = "Codigo de Section")
    @Column(name = "codigo", nullable = true, length = 50)
    private String codigo;

    @Schema(description = "Content de Section")
    @Column(name = "content", nullable = true)
    private String content;

    @Schema(description = "Descripcion de Section")
    @Column(name = "descripcion", nullable = true, length = 200)
    private String descripcion;

    @Schema(description = "Si es final su valor se reemplaza directamente en el documento")
    @Column(name="isFinal", nullable = true)
    private Integer isFinal;

    @Schema(description = "Si es bold para retornar al frontend")
    @Column(name="isBold", nullable = true)
    private Integer isBold;

    @Schema(description = "Indicador si se va a enviar a la IA para revisión")
    @Column(name="isSendIA", nullable = true)
    private Integer isSendIA;

    @Schema(description = "Indicador si luego de pintarlo se hace un salto de línea para retornar al frontend")
    @Column(name="isSaltoLinea", nullable = true)
    private Integer isSaltoLinea;

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
