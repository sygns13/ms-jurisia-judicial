package pj.gob.pe.judicial.service.externals;

import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocument;
import pj.gob.pe.judicial.utils.beans.chatbot.ApiResponseDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.MainConsultaDTO;
import pj.gob.pe.judicial.utils.beans.chatbot.send.MainConsultaSendDTO;

import java.util.List;

public interface ApiChatBotService {

    ApiResponseDTO<List<MainConsultaDTO>> getPendingConsultas();

    void sendProcessedData(MainConsultaSendDTO data);
}
