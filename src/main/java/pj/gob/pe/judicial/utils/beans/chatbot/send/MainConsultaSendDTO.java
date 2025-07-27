package pj.gob.pe.judicial.utils.beans.chatbot.send;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pj.gob.pe.judicial.model.sybase.dto.CabExpedienteChatDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataTipoParteDTO;
import pj.gob.pe.judicial.model.sybase.dto.ResumenExpedienteParteDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainConsultaSendDTO {
    private Long id;
    private String chatId;
    private Boolean expFound;
    private CabExpedienteChatDTO cabExpedienteChat;
    private List<DataTipoParteDTO> listPartes;
    private List<ResumenExpedienteParteDTO> detailsExp;
}
