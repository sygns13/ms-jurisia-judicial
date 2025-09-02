package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.model.mysql.entities.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeMySqlService {

    List<Sede> getAllSedes(String SessionId);

    Optional<Sede> getSedeById(String SessionId, String id);

    Sede createSede(String SessionId, Sede sede);

    Optional<Sede> updateSede(String SessionId, String id, Sede sedeDetails);

    void deleteSede(String SessionId, String id);

    /**
     * Sincroniza la base de datos con la lista de sedes proporcionada.
     * Realiza operaciones de creación, actualización y eliminación según sea necesario.
     * @param sedes Lista de sedes para realizar el upsert.
     */
    void upsertSedes(String SessionId, List<Sede> sedes);
}
