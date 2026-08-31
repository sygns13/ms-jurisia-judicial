package pj.gob.pe.judicial.model.mysql.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO que une los datos de Sede e Instancia registrados en MySQL (JURISDB_JUDICIAL).
 * Es la data que se muestra en la tabla de mantenimiento de Sedes/Instancias del frontend.
 */
@Schema(description = "Data de Sede e Instancia registrada en MySQL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SedeInstanciaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID de la Sede (equivale a c_sede del SIJ)")
    private String idSede;

    @Schema(description = "Nombre de la Sede")
    private String nombreSede;

    @Schema(description = "Direccion de la Sede")
    private String direccionSede;

    @Schema(description = "Codigo de Distrito de la Sede")
    private String codDistritoSede;

    @Schema(description = "ID de la Instancia (equivale a c_instancia del SIJ)")
    private String idInstancia;

    @Schema(description = "Nombre de la Instancia")
    private String nombreInstancia;

    @Schema(description = "Codigo de Distrito de la Instancia")
    private String codDistrito;

    @Schema(description = "Codigo de Provincia de la Instancia")
    private String codProvincia;

    @Schema(description = "Codigo del Organo Jurisdiccional de la Instancia")
    private String codOrganoJurisdiccional;

    @Schema(description = "Numero de la Instancia")
    private Integer numInstancia;

    @Schema(description = "Ubicacion fisica de la Instancia")
    private String ubicacion;

    @Schema(description = "Nombre corto de la Instancia")
    private String nombreCorto;

    @Schema(description = "Codigo de Ubigeo de la Instancia")
    private String codUbigeo;

    @Schema(description = "Estado del Registro de la Instancia")
    private Integer activo;

    @Schema(description = "Borrado Logico del Registro de la Instancia")
    private Integer borrado;

    @Schema(description = "Fecha y Hora de Creacion del Registro de la Instancia")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDatetime;

    @Schema(description = "Fecha y Hora de Edicion del Registro de la Instancia")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDatetime;
}
