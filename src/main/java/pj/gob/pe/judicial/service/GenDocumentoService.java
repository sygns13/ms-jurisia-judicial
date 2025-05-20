package pj.gob.pe.judicial.service;

public interface GenDocumentoService {

    byte[] generateDocx(String templateCode) throws Exception;
}
