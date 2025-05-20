package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Template;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query(
            value = "select id, codigo, nombreOut, descripcion, regUserId, regDate, regDatetime, regTimestamp, updUserId, updDate, updDatetime, updTimestamp, activo, borrado" +
                    " from JURISDB_JUDICIAL.Templates where activo = 1 and borrado = 0;",
            nativeQuery = true
    )
    List<Template> findActivosNoBorrados();

    @Query(
            value = "select id, codigo, nombreOut, descripcion, regUserId, regDate, regDatetime, regTimestamp, updUserId, updDate, updDatetime, updTimestamp, activo, borrado" +
                    " from JURISDB_JUDICIAL.Templates where codigo=:codigo limit 1;",
            nativeQuery = true
    )
    Template findByCode(@Param("codigo") String codigo);
}
