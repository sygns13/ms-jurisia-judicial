package pj.gob.pe.judicial.utils.plantilla;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de resolver una variable ${nombre} de la plantilla en una generación concreta. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolucionVariable {

    /** Nombre de la variable tal como aparece en el Word. */
    private String nombre;

    /** Tipo resuelto: SIJ, CALCULADA, MANUAL, IA o NO_DEFINIDA. */
    private String tipo;

    /** Clave del catálogo de sistema usada (si aplica). */
    private String campoSistema;

    /** Instrucción de la IA (solo tipo IA). */
    private String instruccionIA;

    /** Valor final que se escribe en el documento. */
    private String valor;

    /** false cuando se dejó "..." por falta de dato. */
    private boolean tieneValor;
}
