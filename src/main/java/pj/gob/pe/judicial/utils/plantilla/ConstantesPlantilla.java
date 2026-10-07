package pj.gob.pe.judicial.utils.plantilla;

/**
 * Constantes del flujo de generación de documentos por plantilla completa (.docx con variables
 * ${nombre}). Separadas de {@link pj.gob.pe.judicial.utils.Constantes} para no tocar el flujo por
 * secciones.
 */
public final class ConstantesPlantilla {

    private ConstantesPlantilla() {
    }

    // Origen configurado de una variable (PlantillaVariable.origen)
    public static final String ORIGEN_SISTEMA = "SISTEMA";
    public static final String ORIGEN_MANUAL = "MANUAL";
    public static final String ORIGEN_IA = "IA";

    // Tipo resuelto de una variable al generar (se reporta a métricas)
    public static final String TIPO_SIJ = "SIJ";
    public static final String TIPO_CALCULADA = "CALCULADA";
    public static final String TIPO_MANUAL = "MANUAL";
    public static final String TIPO_IA = "IA";
    public static final String TIPO_NO_DEFINIDA = "NO_DEFINIDA";

    /** Valor que se deja cuando una variable no tiene dato (mismo criterio que el flujo por secciones). */
    public static final String VALOR_SIN_DATO = "...";

    public static final String TYPEDOC_DOC = "doc";
    public static final String TYPEDOC_WEB = "web";

    // Estado de la intervención de la IA en la generación
    public static final String IA_NO_APLICA = "NO_APLICA";
    public static final String IA_EXITOSO = "EXITOSO";
    public static final String IA_ERROR = "ERROR";

    public static final String KAFKA_TOPIC = "judicial-documentos-generado-v2";

    public static final String EXTENSION_DOCX = ".docx";
    public static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    /** Tamaño máximo aceptado para un .docx de plantilla (debe ser coherente con spring.servlet.multipart). */
    public static final long TAMANIO_MAXIMO_BYTES = 20L * 1024L * 1024L;

    /** Ciudad por defecto de los documentos (misma que usa el flujo por secciones). */
    public static final String CIUDAD_DEFECTO = "Huaraz";

    public static final String HEADER_ESTADO_IA = "X-Estado-IA";
    public static final String HEADER_VARIABLES_SIN_VALOR = "X-Variables-Sin-Valor";
    public static final String HEADER_SESSION_UID = "X-Session-UID";
}
