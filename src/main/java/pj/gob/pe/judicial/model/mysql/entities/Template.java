package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Templates Model")
@Entity
@Table(name = "Templates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Codigo de Templates")
    @Column(name = "codigo", nullable = true, length = 50)
    private String codigo;

    @Schema(description = "Nombre de salida del Documento")
    @Column(name = "nombreOut", nullable = true, length = 100)
    private String nombreOut;

    @Schema(description = "Descripcion de Templates")
    @Column(name = "descripcion", nullable = true, length = 200)
    private String descripcion;

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
