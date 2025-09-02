package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeMySqlService {

    List<Sede> getAllSedes();

    Optional<Sede> getSedeById(String id);

    Sede createSede(Sede sede);

    Optional<Sede> updateSede(String id, Sede sedeDetails);

    void deleteSede(String id);

    /**
     * Sincroniza la base de datos con la lista de sedes proporcionada.
     * Realiza operaciones de creación, actualización y eliminación según sea necesario.
     * @param sedes Lista de sedes para realizar el upsert.
     */
    void upsertSedes(List<Sede> sedes);
}
