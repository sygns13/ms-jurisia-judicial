package pj.gob.pe.judicial.utils.beans.plantilla;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Resultado interno de la generación en DOCX (el controller arma la descarga con esto). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoDocumentoPlantilla {

    private byte[] contenido;
    private String nombreArchivo;
    private List<String> variablesSinValor;
    private String estadoIA;
    private String sessionUID;
}
