package pj.gob.pe.judicial.service;

import pj.gob.pe.judicial.utils.beans.ResponseDocumentHTML;

public interface GenDocumentoService {

    byte[] generateDocx(String templateCode) throws Exception;

    byte[] generateDocx(Long nUnico, String templateCode) throws Exception;

    ResponseDocumentHTML generateDocxHTML(Long nUnico, String templateCode) throws Exception;
}
