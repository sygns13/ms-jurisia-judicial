package pj.gob.pe.judicial.utils.beans;
import lombok.Data;

@Data
public class InputObtenerPdf {
    private String xip;
    private String cusuario;
    private String cclave;
    private String xrutaArchivo;
    private String xnombreArchivo;
}
