package pj.gob.pe.judicial.utils.plantilla;

import lombok.Getter;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

/**
 * Datos del expediente (SIJ) ya normalizados para resolver variables de plantilla y armar las
 * métricas. Se construye una sola vez por generación a partir de las filas de
 * {@code ExpedienteService.getDataExpediente} (una fila por parte procesal).
 */
@Getter
public class ContextoExpediente {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LocalDate fechaGeneracion;

    private final Long nUnico;
    private final String numIncidente;
    private final String expediente;
    private final String juzgado;
    private final String materia;
    private final String juez;
    private final String especialista;
    private final String sede;
    private final String especialidad;
    private final String estado;
    private final String ubicacion;
    private final String numero;
    private final String anio;
    private final String fechaInicio;
    private final String tipoExpediente;

    private final String codSede;
    private final String codInstancia;
    private final String codEspecialidad;
    private final String codMateria;

    private final String demandantes;
    private final String demandados;
    private final String dniDemandantes;
    private final String dniDemandados;
    private final int cantidadDemandantes;
    private final int cantidadDemandados;

    public ContextoExpediente(List<DataExpedienteDTO> filas, LocalDate fechaGeneracion) {

        this.fechaGeneracion = fechaGeneracion;

        DataExpedienteDTO cab = filas.get(0);

        this.nUnico = cab.getNUnico();
        this.numIncidente = limpiar(cab.getNumIncidente());
        this.expediente = limpiar(cab.getFormato());
        this.juzgado = limpiar(cab.getNombreInstancia());
        this.materia = limpiar(cab.getDescMateria());
        this.juez = limpiar(cab.getNombreJuez());
        this.especialista = limpiar(cab.getNombreSecretario());
        this.sede = limpiar(cab.getNombreSede());
        this.especialidad = limpiar(cab.getNombreEspecialidad());
        this.estado = limpiar(cab.getDescEstado());
        this.ubicacion = limpiar(cab.getDescUbicacion());
        this.numero = limpiar(cab.getNumero());
        this.anio = limpiar(cab.getYear());
        this.fechaInicio = cab.getFechaInicio() != null ? cab.getFechaInicio().format(FORMATO_FECHA) : "";
        this.tipoExpediente = limpiar(cab.getTipoExpediente());

        this.codSede = limpiar(cab.getCodSede());
        this.codInstancia = limpiar(cab.getCodInstancia());
        this.codEspecialidad = limpiar(cab.getEspecialidad());
        this.codMateria = limpiar(cab.getCodMateria());

        // Cada DNI se agrupa con su propia parte (DTE con demandantes, DDO con demandados)
        StringJoiner dtes = new StringJoiner(" , ");
        StringJoiner ddos = new StringJoiner(" , ");
        StringJoiner dnisDte = new StringJoiner(" , ");
        StringJoiner dnisDdo = new StringJoiner(" , ");
        int totalDte = 0;
        int totalDdo = 0;

        for (DataExpedienteDTO fila : filas) {
            if (fila == null || fila.getTipoParteCodigo() == null || fila.getNombreParte() == null) continue;

            String nombreParte = fila.getNombreParte().trim();
            String dniParte = limpiar(fila.getDniParte());

            switch (fila.getTipoParteCodigo().trim()) {
                case "DTE":
                    dtes.add(nombreParte);
                    if (!dniParte.isEmpty()) dnisDte.add(dniParte);
                    totalDte++;
                    break;
                case "DDO":
                    ddos.add(nombreParte);
                    if (!dniParte.isEmpty()) dnisDdo.add(dniParte);
                    totalDdo++;
                    break;
                default:
                    // Otros tipos de parte no se usan en las plantillas
                    break;
            }
        }

        this.demandantes = dtes.toString();
        this.demandados = ddos.toString();
        this.dniDemandantes = dnisDte.toString();
        this.dniDemandados = dnisDdo.toString();
        this.cantidadDemandantes = totalDte;
        this.cantidadDemandados = totalDdo;
    }

    /**
     * Resumen del expediente que se envía a la IA como contexto para redactar bloques. No incluye
     * DNIs ni códigos internos: solo lo necesario para redactar.
     */
    public String resumenParaIA() {
        StringBuilder sb = new StringBuilder();
        agregar(sb, "Expediente", expediente);
        agregar(sb, "Juzgado", juzgado);
        agregar(sb, "Sede", sede);
        agregar(sb, "Especialidad", especialidad);
        agregar(sb, "Materia", materia);
        agregar(sb, "Juez", juez);
        agregar(sb, "Especialista", especialista);
        agregar(sb, "Estado", estado);
        agregar(sb, "Fecha de inicio", fechaInicio);
        agregar(sb, "Demandante(s)", demandantes);
        agregar(sb, "Demandado(s)", demandados);
        return sb.toString();
    }

    private static void agregar(StringBuilder sb, String etiqueta, String valor) {
        if (valor != null && !valor.isBlank()) {
            sb.append(etiqueta).append(": ").append(valor).append('\n');
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
