package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;

import java.util.List;

/**
 * Consultas de mantenimiento sobre TipoDocumento para el administrador de plantillas. Repositorio
 * separado de {@link TipoDocumentoRepository} para no modificar las consultas del flujo existente.
 */
@Repository
public interface TipoDocumentoAdminRepository extends JpaRepository<TipoDocumento, Long> {

    List<TipoDocumento> findByBorradoOrderByIdInstanciaAscDescripcionAsc(Integer borrado);

    List<TipoDocumento> findByIdInstanciaAndBorradoOrderByDescripcionAsc(String idInstancia, Integer borrado);

    List<TipoDocumento> findByIdInstanciaAndActivoAndBorradoOrderByDescripcionAsc(String idInstancia, Integer activo, Integer borrado);

    List<TipoDocumento> findByActivoAndBorradoOrderByIdInstanciaAscDescripcionAsc(Integer activo, Integer borrado);

    boolean existsByIdInstanciaAndDescripcionAndBorrado(String idInstancia, String descripcion, Integer borrado);

    boolean existsByIdInstanciaAndDescripcionAndBorradoAndIdTipoDocumentoNot(String idInstancia, String descripcion, Integer borrado, Long idTipoDocumento);
}
