package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaArchivo;

@Repository
public interface PlantillaArchivoRepository extends JpaRepository<PlantillaArchivo, Long> {
}
