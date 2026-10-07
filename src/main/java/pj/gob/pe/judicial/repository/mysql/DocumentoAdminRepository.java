package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Documento;

import java.util.List;

/**
 * Consultas de mantenimiento sobre Documento para el administrador de plantillas. Repositorio
 * separado de {@link DocumentoRepository} para no modificar las consultas del flujo existente.
 */
@Repository
public interface DocumentoAdminRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByBorradoOrderByIdTipoDocumentoAscDescripcionAsc(Integer borrado);

    List<Documento> findByIdTipoDocumentoAndBorradoOrderByDescripcionAsc(Long idTipoDocumento, Integer borrado);

    List<Documento> findByIdTipoDocumentoInAndActivoAndBorrado(List<Long> idsTipoDocumento, Integer activo, Integer borrado);

    long countByIdTipoDocumentoAndBorrado(Long idTipoDocumento, Integer borrado);

    boolean existsByIdTipoDocumentoAndDescripcionAndBorrado(Long idTipoDocumento, String descripcion, Integer borrado);

    boolean existsByIdTipoDocumentoAndDescripcionAndBorradoAndIdDocumentoNot(Long idTipoDocumento, String descripcion, Integer borrado, Long idDocumento);
}
