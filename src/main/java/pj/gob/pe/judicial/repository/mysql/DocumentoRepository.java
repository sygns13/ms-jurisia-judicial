package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    @Query(
            value = "select idDocumento, idTipoDocumento, descripcion, codigoTemplate, regUserId, regDate, regDatetime, regTimestamp, updUserId, updDate, updDatetime, updTimestamp, activo, borrado \n" +
                    " from JURISDB_JUDICIAL.Documento where activo = 1 and borrado = 0;",
            nativeQuery = true
    )
    List<Documento> findActivosNoBorrados();
}
