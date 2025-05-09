package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.InstanciaDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InstanciaDAOImpl implements InstanciaDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataInstanciaDTO> findActiveInstancias() throws Exception {

        List<DataInstanciaDTO> listInstancias = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                "select c_instancia, c_distrito, c_provincia, c_org_jurisd, x_nom_instancia, n_instancia, x_ubicacion_fisica, x_corto, c_sede, c_ubigeo, l_ind_baja \n" +
                        "from instancia\n" +
                        "where l_ind_baja='N'"
        ).getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataInstanciaDTO instancia = new DataInstanciaDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3]),
                        String.valueOf(row[4]),
                        ((Integer) row[5]).longValue(),
                        String.valueOf(row[6]),
                        String.valueOf(row[7]),
                        String.valueOf(row[8]),
                        String.valueOf(row[9]),
                        String.valueOf(row[10])
                );

                listInstancias.add(instancia);
            });
        }
        return listInstancias;
    }
}
