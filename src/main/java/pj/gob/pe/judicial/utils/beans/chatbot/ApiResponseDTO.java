package pj.gob.pe.judicial.utils.beans.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private boolean success;
    private boolean itemFound;
    private T data;
    private String message;
}
