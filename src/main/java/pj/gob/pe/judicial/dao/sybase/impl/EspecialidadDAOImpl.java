package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.EspecialidadDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EspecialidadDAOImpl implements EspecialidadDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataEspecialidadDTO> findEspecialidades() throws Exception {
        List<DataEspecialidadDTO> listEspecialidads = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                "select c_especialidad, x_desc_especialidad, c_cod_especialidad from especialidad where l_visualiza='S'"
        ).getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataEspecialidadDTO especialidad = new DataEspecialidadDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2])
                );

                listEspecialidads.add(especialidad);
            });
        }
        return listEspecialidads;
    }
}
