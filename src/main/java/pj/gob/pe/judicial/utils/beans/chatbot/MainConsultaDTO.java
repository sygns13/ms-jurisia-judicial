package pj.gob.pe.judicial.utils.beans.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainConsultaDTO {
    private Long id;
    private int status;
    private int step;
    private String service;
    private String chatId;
    private String message;
    private String regDate;
    private String regDatetime;
    private Long regTimestamp;
    private String updDate;
    private String updDatetime;
    private Long updTimestamp;
}
