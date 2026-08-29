package pj.gob.pe.judicial.model.sybase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "Documento digital (Resolución / Digitalizado) de un expediente SIJ")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDocumentoDigitalDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("nUnico")
    private Long nUnico;

    @JsonProperty("xFormato")
    private String xFormato;

    @JsonProperty("lTipoDoc")
    private String lTipoDoc;

    @JsonProperty("fRegistro")
    private String fRegistro;

    @JsonProperty("actoProcesal")
    private String actoProcesal;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("sumilla")
    private String sumilla;

    @JsonProperty("xip")
    private String xip;

    @JsonProperty("cusuario")
    private String cusuario;

    @JsonProperty("cclave")
    private String cclave;

    @JsonProperty("xrutaArchivo")
    private String xrutaArchivo;

    @JsonProperty("xnombreArchivo")
    private String xnombreArchivo;

    @JsonProperty("rutaCompleta")
    private String rutaCompleta;
}