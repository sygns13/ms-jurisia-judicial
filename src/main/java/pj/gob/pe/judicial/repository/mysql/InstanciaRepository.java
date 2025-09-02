package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;

/**
 * Repositorio para la entidad Instancia.
 * Extiende JpaRepository para obtener las operaciones CRUD básicas.
 * Esta interfaz actúa como la capa DAO.
 */
@Repository
public interface InstanciaRepository extends JpaRepository<pj.gob.pe.judicial.model.mysql.entities.Instancia, String> {
    // Spring Data JPA proveerá la implementación de los métodos CRUD automáticamente.
    // Puedes añadir métodos de consulta personalizados aquí si los necesitas.
    // Por ejemplo: List<Instancia> findByIdSede(String idSede);
}
