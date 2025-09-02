package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla 'Instancias' en la base de datos.
 * Modela una instancia judicial.
 */
@Schema(description = "Instancia Model")
@Entity
@Table(name = "Instancias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instancia {

    @Schema(description = "ID de la Instancia")
    @Id
    @Column(name = "idInstancia", length = 10, nullable = false)
    private String idInstancia;

    @Schema(description = "código del Distrito de la Instancia")
    @Column(name = "codDistrito", length = 10)
    private String codDistrito;

    @Schema(description = "código de la Provincia de la Instancia")
    @Column(name = "codProvincia", length = 10)
    private String codProvincia;

    @Schema(description = "código del Organo Jurisdiccional de la Instancia")
    @Column(name = "codOrganoJurisdiccional", length = 10)
    private String codOrganoJurisdiccional;

    @Schema(description = "Nombre de la Instancia")
    @Column(name = "nombre", length = 150)
    private String nombre;

    @Schema(description = "Número de la Instancia")
    @Column(name = "numInstancia")
    private Integer numInstancia;

    @Schema(description = "Ubicación de la Instancia")
    @Column(name = "ubicacion", length = 150)
    private String ubicacion;

    @Schema(description = "Nombre Corto de la Instancia")
    @Column(name = "nombreCorto", length = 10)
    private String nombreCorto;

    @Column(name = "codUbigeo", length = 10)
    private String codUbigeo;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSede", referencedColumnName = "idSede")
    @JsonBackReference
    private Sede sede;
}
