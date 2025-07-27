package pj.gob.pe.judicial.model.sybase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTipoParteDTO {
    private String tipoPersona;
    private String descTipoPersona;
    private String tipoParte;
    private String descTipoParte;
    private String apePaterno;
    private String apeMaterno;
    private String nombres;
    private String docId;
    private String tipoDoc;
    private String descTipoDoc;
    private String abreviaturaTipoDoc;
    private String activo;
    private Long nUnico;
}

