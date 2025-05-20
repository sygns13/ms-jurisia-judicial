package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;

import java.util.List;

@Repository
public interface SectionTemplateRepository extends JpaRepository<SectionTemplate, Long> {

    @Query(
            value = "select id, idTemplate, codigo, content, descripcion, isFinal, isBold, isSendIA, isSaltoLinea, regUserId, regDate, regDatetime, regTimestamp, updUserId, updDate, updDatetime, updTimestamp, activo, borrado" +
                    " from JURISDB_JUDICIAL.SectionTemplates where idTemplate =:id_template and activo = 1 and borrado = 0;",
            nativeQuery = true
    )
    List<SectionTemplate> findTemplateSections(@Param("id_template") Long idTemplate);
}
