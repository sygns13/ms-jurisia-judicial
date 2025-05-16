package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}
