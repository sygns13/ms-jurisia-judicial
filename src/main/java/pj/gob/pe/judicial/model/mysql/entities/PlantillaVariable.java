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
 * Variable ${nombre} de una plantilla y de dónde se obtiene su valor:
 * SISTEMA (catálogo de variables del SIJ o calculadas), MANUAL (valorDefecto o "...") o
 * IA (bloque redactado por Gemini según instruccionIA). Se registran automáticamente al cargar
 * el .docx y el administrador puede ajustarlas.
 */
@Schema(description = "Variable de Plantilla Model")
@Entity
@Table(name = "PlantillaVariable")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantillaVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVariable;

    @Schema(description = "ID de la Plantilla")
    @Column(name = "idPlantilla", nullable = false)
    private Long idPlantilla;

    @Schema(description = "Nombre de la variable tal como aparece en el Word, sin ${ }")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Descripción de la variable para el administrador")
    @Column(name = "descripcion", nullable = true, length = 250)
    private String descripcion;

    @Schema(description = "Origen del valor: SISTEMA, MANUAL o IA")
    @Column(name = "origen", nullable = false, length = 20)
    private String origen;

    @Schema(description = "Clave del catálogo de variables del sistema (solo origen SISTEMA; vacío = mismo nombre)")
    @Column(name = "campoSistema", nullable = true, length = 100)
    private String campoSistema;

    @Schema(description = "Valor por defecto (MANUAL, o SISTEMA cuando el SIJ no trae dato)")
    @Column(name = "valorDefecto", nullable = true, columnDefinition = "TEXT")
    private String valorDefecto;

    @Schema(description = "Instrucción para que la IA redacte el bloque (solo origen IA)")
    @Column(name = "instruccionIA", nullable = true, columnDefinition = "TEXT")
    private String instruccionIA;

    @Schema(description = "1 si la variable está presente en el archivo .docx vigente")
    @Column(name = "detectada", nullable = true)
    private Integer detectada;

    @Schema(description = "Orden de aparición en el documento")
    @Column(name = "orden", nullable = true)
    private Integer orden;

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
