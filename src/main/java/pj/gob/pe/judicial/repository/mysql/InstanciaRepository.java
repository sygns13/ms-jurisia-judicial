package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;

import java.util.List;

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

    /**
     * Lista las instancias registradas en MySQL junto con su Sede, para la consulta
     * del módulo administrable de Sedes/Instancias.
     */
    @Query("SELECT i FROM Instancia i LEFT JOIN FETCH i.sede s ORDER BY s.idSede ASC, i.idInstancia ASC")
    List<Instancia> listarInstanciasConSede();

    /**
     * Lista los idInstancia habilitados, usados como filtro de alcance en las consultas
     * nativas del SIJ (Sybase).
     */
    @Query("SELECT i.idInstancia FROM Instancia i WHERE i.activo = :activo AND i.borrado = :borrado ORDER BY i.idInstancia ASC")
    List<String> listarIdInstanciasPorEstado(@Param("activo") Integer activo, @Param("borrado") Integer borrado);
}
