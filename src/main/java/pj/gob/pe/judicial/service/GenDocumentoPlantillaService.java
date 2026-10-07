package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoGenerable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoPlantillaHTML;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;

import java.util.List;

public interface GenDocumentoPlantillaService {

    List<ResponseDocumentoGenerable> listarDocumentosGenerables(String SessionId, String idInstancia);

    ResultadoDocumentoPlantilla generarDocx(Long nUnico, String numIncidente, Long idDocumento, String SessionId) throws Exception;

    ResponseDocumentoPlantillaHTML generarHTML(Long nUnico, String numIncidente, Long idDocumento, String SessionId) throws Exception;
}
