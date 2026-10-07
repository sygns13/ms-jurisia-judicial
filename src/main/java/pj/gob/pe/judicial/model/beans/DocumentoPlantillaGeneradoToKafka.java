package pj.gob.pe.judicial.model.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Evento de documento generado por plantilla (tópico judicial-documentos-generado-v2). Lo consume
 * ms-jurisia-metricas en CabDocumentoPlantillaGenerado + DetDocumentoPlantillaVariable. Se publica
 * tanto en generaciones exitosas como fallidas (status).
 *
 * Contrato compartido con pj.gob.pe.metricas.model.beans.DocumentoPlantillaGeneradoToKafka: un
 * cambio de campo debe aplicarse en ambos lados.
 */
@Schema(description = "Evento Kafka de documento generado por plantilla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoPlantillaGeneradoToKafka {

    // ---- Generación ----
    private String sessionUID;
    private String typedoc;
    private Integer status;
    private String mensajeError;

    // ---- Usuario ----
    private Long userId;
    private String username;
    private String nombreUsuario;
    private String cargo;
    private Long idDependencia;
    private String dependencia;

    // ---- Expediente (SIJ) ----
    private Long nUnico;
    private String numIncidente;
    private String codSede;
    private String sede;
    private String codInstancia;
    private String instancia;
    private String codEspecialidad;
    private String especialidad;
    private String codMateria;
    private String materia;
    private String codNumero;
    private String codYear;
    private String xFormato;
    private String ubicacion;
    private String juez;
    private String especialista;
    private String estado;
    private String dniDemandante;
    private String demandante;
    private String dniDemandado;
    private String demandado;
    private Integer cantidadDemandantes;
    private Integer cantidadDemandados;

    // ---- Documento / plantilla ----
    private Long idTipoDocumento;
    private String tipoDocumento;
    private Long idDocumento;
    private String documento;
    private Long idPlantilla;
    private String codigoPlantilla;
    private String nombreOutPlantilla;
    private Integer versionPlantilla;
    private Integer corregirIA;
    private Long tamanioPlantillaBytes;
    private Long tamanioSalidaBytes;

    // ---- Variables ----
    private Integer totalVariables;
    private Integer variablesSij;
    private Integer variablesCalculadas;
    private Integer variablesManuales;
    private Integer variablesIA;
    private Integer variablesNoDefinidas;
    private Integer variablesSinValor;
    private Integer reemplazosRealizados;

    // ---- IA ----
    private String estadoIA;
    private String mensajeIA;
    private Integer parrafosEnviadosIA;
    private Integer parrafosCorregidosIA;
    private Integer bloquesSolicitadosIA;
    private Integer bloquesGeneradosIA;
    private String model;
    private String roleSystem;
    private BigDecimal temperature;
    private Integer configurationsId;
    private String finishReason;
    private Integer promptTokens;
    private Integer candidatesTokens;
    private Integer thoughtsTokens;
    private Integer cachedTokens;
    private Integer totalTokens;

    // ---- Tiempos ----
    private Long tiempoSijMs;
    private Long tiempoIAMs;
    private Long tiempoTotalMs;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate regDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDatetime;
    private Long regTimestamp;

    private List<VariableDocumentoToKafka> variables = new ArrayList<>();
}
