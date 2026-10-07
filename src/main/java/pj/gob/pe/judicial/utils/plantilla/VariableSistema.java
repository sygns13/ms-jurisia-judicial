package pj.gob.pe.judicial.utils.plantilla;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Catálogo de variables que el sistema resuelve solo, sin configuración por plantilla: basta con
 * escribir ${clave} en el Word. Reemplaza a los métodos ReemplazarSeccionesTemplateN del flujo por
 * secciones. Las claves "title.*" / "top.*" se mantienen por compatibilidad con los nombres que ya
 * conocen quienes mantienen las plantillas.
 */
public enum VariableSistema {

    // ---- Datos del expediente (SIJ) ----
    JUZGADO("juzgado", ConstantesPlantilla.TIPO_SIJ, "Nombre del juzgado (instancia) del expediente", ContextoExpediente::getJuzgado),
    EXPEDIENTE("expediente", ConstantesPlantilla.TIPO_SIJ, "Número de expediente con formato (X_FORMATO)", ContextoExpediente::getExpediente),
    MATERIA("materia", ConstantesPlantilla.TIPO_SIJ, "Materia del expediente", ContextoExpediente::getMateria),
    JUEZ("juez", ConstantesPlantilla.TIPO_SIJ, "Nombre del juez", ContextoExpediente::getJuez),
    ESPECIALISTA("especialista", ConstantesPlantilla.TIPO_SIJ, "Nombre del especialista / secretario", ContextoExpediente::getEspecialista),
    SEDE("sede", ConstantesPlantilla.TIPO_SIJ, "Nombre de la sede", ContextoExpediente::getSede),
    ESPECIALIDAD("especialidad", ConstantesPlantilla.TIPO_SIJ, "Nombre de la especialidad", ContextoExpediente::getEspecialidad),
    ESTADO("estado", ConstantesPlantilla.TIPO_SIJ, "Estado del expediente", ContextoExpediente::getEstado),
    UBICACION("ubicacion", ConstantesPlantilla.TIPO_SIJ, "Ubicación actual del expediente", ContextoExpediente::getUbicacion),
    NUMERO_EXPEDIENTE("numero_expediente", ConstantesPlantilla.TIPO_SIJ, "Número correlativo del expediente", ContextoExpediente::getNumero),
    ANIO_EXPEDIENTE("anio_expediente", ConstantesPlantilla.TIPO_SIJ, "Año del expediente", ContextoExpediente::getAnio),
    INCIDENTE("incidente", ConstantesPlantilla.TIPO_SIJ, "Número de incidente", ContextoExpediente::getNumIncidente),
    FECHA_INICIO("fecha_inicio", ConstantesPlantilla.TIPO_SIJ, "Fecha de inicio del expediente (dd/MM/yyyy)", ContextoExpediente::getFechaInicio),
    TIPO_EXPEDIENTE("tipo_expediente", ConstantesPlantilla.TIPO_SIJ, "Tipo de expediente", ContextoExpediente::getTipoExpediente),
    DEMANDANTES("demandantes", ConstantesPlantilla.TIPO_SIJ, "Demandante(s) separados por coma", ContextoExpediente::getDemandantes),
    DEMANDADOS("demandados", ConstantesPlantilla.TIPO_SIJ, "Demandado(s) separados por coma", ContextoExpediente::getDemandados),
    DNI_DEMANDANTES("dni_demandantes", ConstantesPlantilla.TIPO_SIJ, "DNI del/los demandante(s)", ContextoExpediente::getDniDemandantes),
    DNI_DEMANDADOS("dni_demandados", ConstantesPlantilla.TIPO_SIJ, "DNI del/los demandado(s)", ContextoExpediente::getDniDemandados),

    // ---- Calculadas ----
    CIUDAD("ciudad", ConstantesPlantilla.TIPO_CALCULADA, "Ciudad del documento (Huaraz)", ctx -> ConstantesPlantilla.CIUDAD_DEFECTO),
    FECHA_ACTUAL("fecha_actual", ConstantesPlantilla.TIPO_CALCULADA, "Fecha de generación (dd/MM/yyyy)",
            ctx -> ctx.getFechaGeneracion().format(Formatos.FECHA_CORTA)),
    FECHA_DIA_MES("fecha_dia_mes", ConstantesPlantilla.TIPO_CALCULADA, "Día y mes en letras (ej. 26 de septiembre)",
            ctx -> ctx.getFechaGeneracion().format(Formatos.DIA_MES)),
    FECHA_ANIO("fecha_anio", ConstantesPlantilla.TIPO_CALCULADA, "Año de generación (ej. 2026)",
            ctx -> ctx.getFechaGeneracion().format(Formatos.ANIO)),
    FECHA_LARGA("fecha_larga", ConstantesPlantilla.TIPO_CALCULADA, "Fecha completa en letras (ej. 26 de septiembre de 2026)",
            ctx -> ctx.getFechaGeneracion().format(Formatos.FECHA_LARGA)),
    FECHA_LARGA_DEL("fecha_larga_del", ConstantesPlantilla.TIPO_CALCULADA, "Fecha completa con 'del' (ej. 26 de septiembre del 2026), formato del flujo por secciones",
            ctx -> ctx.getFechaGeneracion().format(Formatos.FECHA_LARGA_DEL)),

    // ---- Compatibilidad con los nombres del flujo por secciones ----
    TITLE_JUZGADO("title.juzgado", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${juzgado}", ContextoExpediente::getJuzgado),
    TITLE_EXPEDIENTE("title.expediente", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${expediente}", ContextoExpediente::getExpediente),
    TITLE_MATERIA("title.materia", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${materia}", ContextoExpediente::getMateria),
    TITLE_JUEZ("title.juez", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${juez}", ContextoExpediente::getJuez),
    TITLE_ESPECIALISTA("title.especialista", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${especialista}", ContextoExpediente::getEspecialista),
    TITLE_DEMANDANTE("title.demandante", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${demandantes}", ContextoExpediente::getDemandantes),
    TITLE_DEMANDADO("title.demandado", ConstantesPlantilla.TIPO_SIJ, "Compatibilidad: igual a ${demandados}", ContextoExpediente::getDemandados),
    TOP_CIUDAD("top.ciudad", ConstantesPlantilla.TIPO_CALCULADA, "Compatibilidad: igual a ${ciudad}", ctx -> ConstantesPlantilla.CIUDAD_DEFECTO),
    TOP_DIAMESLETTER("top.diamesletter", ConstantesPlantilla.TIPO_CALCULADA, "Compatibilidad: igual a ${fecha_dia_mes}",
            ctx -> ctx.getFechaGeneracion().format(Formatos.DIA_MES)),
    TOP_ANIOLETTER("top.anioletter", ConstantesPlantilla.TIPO_CALCULADA, "Compatibilidad: 'Del año' + año (ej. Del año 2026)",
            ctx -> ctx.getFechaGeneracion().format(Formatos.DEL_ANIO));

    private static final Map<String, VariableSistema> POR_CLAVE = Arrays.stream(values())
            .collect(Collectors.toMap(VariableSistema::getClave, Function.identity()));

    private final String clave;
    private final String tipo;
    private final String descripcion;
    private final Function<ContextoExpediente, String> resolver;

    VariableSistema(String clave, String tipo, String descripcion, Function<ContextoExpediente, String> resolver) {
        this.clave = clave;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.resolver = resolver;
    }

    public String getClave() {
        return clave;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String resolver(ContextoExpediente contexto) {
        String valor = resolver.apply(contexto);
        return valor == null ? "" : valor.trim();
    }

    public static Optional<VariableSistema> buscar(String clave) {
        return clave == null ? Optional.empty() : Optional.ofNullable(POR_CLAVE.get(clave.trim()));
    }

    /** Formateadores de fecha en español de Perú (holder para inicializarlos antes que el enum los use). */
    private static final class Formatos {
        private static final Locale ES_PE = Locale.of("es", "PE");
        private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd 'de' MMMM", ES_PE);
        private static final DateTimeFormatter ANIO = DateTimeFormatter.ofPattern("yyyy");
        private static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ES_PE);
        private static final DateTimeFormatter FECHA_LARGA_DEL = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", ES_PE);
        private static final DateTimeFormatter DEL_ANIO = DateTimeFormatter.ofPattern("'Del año' yyyy", ES_PE);
    }
}
