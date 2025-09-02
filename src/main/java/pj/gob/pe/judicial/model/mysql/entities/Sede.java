package pj.gob.pe.judicial.model.mysql.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa la tabla 'Sedes' en la base de datos.
 * Modela una sede judicial.
 */
@Schema(description = "Sede Model")
@Entity
@Table(name = "Sedes")
@Data // Anotación de Lombok para generar getters, setters, toString, equals y hashCode.
@NoArgsConstructor // Constructor sin argumentos.
@AllArgsConstructor // Constructor con todos los argumentos.
public class Sede {

    @Schema(description = "ID de la Sede")
    @Id
    @Column(name = "idSede", length = 10, nullable = false)
    private String idSede;

    @Schema(description = "Nombre de la Sede")
    @Column(name = "nombre", length = 150)
    private String nombre;

    @Schema(description = "Dirección de la Sede")
    @Column(name = "direccion", length = 150)
    private String direccion;

    @Schema(description = "Código del Distrito de la Sede")
    @Column(name = "codDistrito", length = 10)
    private String codDistrito;

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

    @Schema(description = "Lista de Instancias asociadas a la Sede")
    // fetch = FetchType.EAGER para cargar las instancias junto con la sede.
    // Considera usar LAZY para mejor rendimiento si no siempre necesitas las instancias.
    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Instancia> instancias;
}
