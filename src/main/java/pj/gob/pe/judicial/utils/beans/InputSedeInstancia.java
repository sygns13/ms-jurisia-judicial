package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input que une los datos de Sede e Instancia seleccionados en los combos anidados del SIJ,
 * para su registro en MySQL (JURISDB_JUDICIAL).
 */
@Schema(description = "Input Sede/Instancia Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputSedeInstancia {

    @Schema(description = "ID de la Sede (c_sede del SIJ)")
    @NotNull(message = "{input.sede.notnull}")
    @Size(min = 1, max = 10, message = "{input.sede.size}")
    private String idSede;

    @Schema(description = "Nombre de la Sede (x_desc_sede del SIJ)")
    @NotNull(message = "Debe de ingresar el nombre de la Sede")
    @Size(min = 1, max = 150, message = "El nombre de la Sede debe de tener minimo 01 caracter")
    private String nombreSede;

    @Schema(description = "Direccion de la Sede (x_direccion del SIJ)")
    private String direccionSede;

    @Schema(description = "Codigo de Distrito de la Sede (c_distrito del SIJ)")
    private String codDistritoSede;

    @Schema(description = "ID de la Instancia (c_instancia del SIJ)")
    @NotNull(message = "{input.instancia.notnull}")
    @Size(min = 1, max = 10, message = "{input.instancia.size}")
    private String idInstancia;

    @Schema(description = "Nombre de la Instancia (x_nom_instancia del SIJ)")
    @NotNull(message = "Debe de ingresar el nombre de la Instancia")
    @Size(min = 1, max = 150, message = "El nombre de la Instancia debe de tener minimo 01 caracter")
    private String nombreInstancia;

    @Schema(description = "Codigo de Distrito de la Instancia (c_distrito del SIJ)")
    private String codDistrito;

    @Schema(description = "Codigo de Provincia de la Instancia (c_provincia del SIJ)")
    private String codProvincia;

    @Schema(description = "Codigo del Organo Jurisdiccional de la Instancia (c_org_jurisd del SIJ)")
    private String codOrganoJurisdiccional;

    @Schema(description = "Numero de la Instancia (n_instancia del SIJ)")
    private Integer numInstancia;

    @Schema(description = "Ubicacion fisica de la Instancia (x_ubicacion_fisica del SIJ)")
    private String ubicacion;

    @Schema(description = "Nombre corto de la Instancia (x_corto del SIJ)")
    private String nombreCorto;

    @Schema(description = "Codigo de Ubigeo de la Instancia (c_ubigeo del SIJ)")
    private String codUbigeo;
}
