package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;

import java.util.List;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    // Una consulta nativa
    @Query(
            value = "select idTipoDocumento, idInstancia, descripcion, regUserId, regDate, regDatetime, regTimestamp, updUserId, " +
                    "updDate, updDatetime, updTimestamp, activo, borrado from JURISDB_JUDICIAL.TipoDocumento where idInstancia =:id_instancia;",
            nativeQuery = true
    )
    List<TipoDocumento> findByInstancia(
            @Param("id_instancia") String idInstancia
    );
}
