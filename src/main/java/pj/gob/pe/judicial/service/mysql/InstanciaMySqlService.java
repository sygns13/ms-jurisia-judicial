package pj.gob.pe.judicial.service.mysql;


import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import java.util.List;
import java.util.Optional;

public interface InstanciaMySqlService {

    List<Instancia> getAllInstancias(String SessionId);

    Optional<Instancia> getInstanciaById(String SessionId, String id);

    Instancia createInstancia(String SessionId, Instancia instancia);

    Optional<Instancia> updateInstancia(String SessionId, String id, Instancia instanciaDetails);

    void deleteInstancia(String SessionId, String id);
}
