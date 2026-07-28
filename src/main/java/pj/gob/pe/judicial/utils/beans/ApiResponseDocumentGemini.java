package pj.gob.pe.judicial.utils.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Envoltura estándar de los endpoints Gemini de ms-jurisia-consultaia
 * ({@code ApiResponse<T>}: success, message, result, time), tipada para el
 * procesamiento de documentos.
 */
@Schema(description = "ApiResponse de consultaia para el procesamiento de documento con Gemini")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDocumentGemini {

    private boolean success;
    private String message;
    private ResponseDocumentGemini result;
    private double time;
}
