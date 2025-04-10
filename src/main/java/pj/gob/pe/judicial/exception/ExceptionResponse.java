package pj.gob.pe.judicial.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExceptionResponse {

    private LocalDateTime fecha;
    private String mensaje;
    private String detalles;

    public ExceptionResponse(LocalDateTime fecha, String mensaje, String detalles) {
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }
}
