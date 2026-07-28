package pj.gob.pe.judicial.service.externals;

import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocumentGemini;

/**
 * Consumo del procesamiento de documentos con Gemini de ms-jurisia-consultaia
 * (POST /v1/gemini-chat/documento). Equivalente Gemini de {@link ConsultaiaService}.
 */
public interface ConsultaiaGeminiService {

    ResponseDocumentGemini ProcessDocumentGemini(InputDocument inputDocument);
}
