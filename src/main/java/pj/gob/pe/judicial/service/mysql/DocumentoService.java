package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.Documento;

import java.util.List;

public interface DocumentoService {

    List<Documento> listarAll();

    List<Documento> listarActivosNoBorrados();
}
