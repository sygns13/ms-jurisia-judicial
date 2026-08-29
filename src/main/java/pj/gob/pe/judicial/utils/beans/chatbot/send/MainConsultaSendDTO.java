package pj.gob.pe.judicial.utils.beans.chatbot.send;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pj.gob.pe.judicial.model.sybase.dto.AudienciaExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.CabExpedienteChatDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataTipoParteDTO;
import pj.gob.pe.judicial.model.sybase.dto.EscritoExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.ResumenExpedienteParteDTO;

import java.util.ArrayList;
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

    // El ciudadano elige la opción de consulta recién en el paso 4, pero la API de Laravel
    // solo recibe datos en este único envío (paso 1). Por eso se precargan escritos y
    // audiencias aunque la consulta termine usando solo una de las opciones.
    //
    // Se inicializan vacías: si la consulta al SIJ falla, el POST viaja igual con listas
    // vacías en lugar de null, y el validador de Laravel las acepta como array.
    private List<EscritoExpedienteDTO> listEscritos = new ArrayList<>();

    /** Audiencias realizadas y próximas en una sola lista, diferenciadas por tipoAudiencia. */
    private List<AudienciaExpedienteDTO> listAudiencias = new ArrayList<>();
}
