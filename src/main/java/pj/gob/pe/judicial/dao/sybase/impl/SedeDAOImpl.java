package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.SedeDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SedeDAOImpl implements SedeDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataSedeDTO> findActiveSedes() throws Exception {

        List<DataSedeDTO> listSedes = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "select c_sede, x_desc_sede, l_activo, c_distrito, x_direccion   from sede where l_activo = 'S'"
                ).getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataSedeDTO sede = new DataSedeDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3]),
                        String.valueOf(row[4])
                );

                listSedes.add(sede);
            });
        }
        return listSedes;
    }
}
