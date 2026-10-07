package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaDocumentoRepository extends JpaRepository<PlantillaDocumento, Long> {

    List<PlantillaDocumento> findByBorradoOrderByIdPlantillaDesc(Integer borrado);

    List<PlantillaDocumento> findByIdDocumentoAndBorrado(Long idDocumento, Integer borrado);

    List<PlantillaDocumento> findByActivoAndBorrado(Integer activo, Integer borrado);

    Optional<PlantillaDocumento> findFirstByIdDocumentoAndActivoAndBorradoOrderByIdPlantillaDesc(Long idDocumento, Integer activo, Integer borrado);

    boolean existsByCodigoAndBorrado(String codigo, Integer borrado);

    boolean existsByCodigoAndBorradoAndIdPlantillaNot(String codigo, Integer borrado, Long idPlantilla);
}
