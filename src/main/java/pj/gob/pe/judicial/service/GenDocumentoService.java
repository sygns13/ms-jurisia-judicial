package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.utils.beans.AuxDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocumentHTML;

public interface GenDocumentoService {

    byte[] generateDocx(String templateCode) throws Exception;

    byte[] generateDocx(Long nUnico, String numIncidente, String templateCode, AuxDocument auxDocument, String SessionId, Long idDocumento) throws Exception;

    ResponseDocumentHTML generateDocxHTML(Long nUnico, String numIncidente, String templateCode, String SessionId, Long idDocumento) throws Exception;
}
