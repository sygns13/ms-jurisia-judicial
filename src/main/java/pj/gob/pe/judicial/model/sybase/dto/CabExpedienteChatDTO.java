package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Data General de Expediente SIJ Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CabExpedienteChatDTO {

    private Long nUnico;
    private String xFormato;
    private Long nIncidente;
    private String tipoExpediente;
    private String codEspecialidad;
    private String codInstancia;
    private String instancia;
    private String organoJurisd;
    private String sede;
    private String indAnulado;
    private String indUltimo;
}
