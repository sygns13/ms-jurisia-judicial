package pj.gob.pe.judicial.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaVariableRepository extends JpaRepository<PlantillaVariable, Long> {

    List<PlantillaVariable> findByIdPlantillaAndBorradoOrderByOrdenAscIdVariableAsc(Long idPlantilla, Integer borrado);

    Optional<PlantillaVariable> findFirstByIdPlantillaAndNombreAndBorrado(Long idPlantilla, String nombre, Integer borrado);
}
