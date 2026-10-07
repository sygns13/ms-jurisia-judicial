package pj.gob.pe.judicial.model.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detalle por variable de un documento generado por plantilla. No viaja el valor (ya está en la
 * cabecera lo relevante y se evita replicar datos personales): solo cómo se resolvió.
 */
@Schema(description = "Variable resuelta en la generación de un documento por plantilla")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariableDocumentoToKafka {

    private String nombre;
    private String tipo;
    private String campoSistema;
    private Boolean tieneValor;
}
