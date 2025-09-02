package pj.gob.pe.judicial.service.mysql;


import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import java.util.List;
import java.util.Optional;

public interface InstanciaService {

    List<Instancia> getAllInstancias();

    Optional<Instancia> getInstanciaById(String id);

    Instancia createInstancia(Instancia instancia);

    Optional<Instancia> updateInstancia(String id, Instancia instanciaDetails);

    void deleteInstancia(String id);
}
