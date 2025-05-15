package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.InstanciaDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InstanciaDAOImpl implements InstanciaDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataInstanciaDTO> findActiveInstancias(UserLogin user) throws Exception {

        List<DataInstanciaDTO> listInstancias = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                " SELECT DISTINCT i.c_instancia, i.c_distrito, i.c_provincia, i.c_org_jurisd, i.x_nom_instancia, i.n_instancia, i.x_ubicacion_fisica, i.x_corto, i.c_sede, i.c_ubigeo, i.l_ind_baja \n" +
                        " FROM usuario u \n" +
                        " JOIN usuario_instancia ui ON u.c_usuario = ui.c_usuario \n" +
                        " JOIN instancia i ON i.c_instancia = ui.c_instancia \n" +
                        " JOIN sede s ON s.c_sede = i.c_sede \n" +
                        "                                WHERE \n" +
                        "                                ui.l_activo = 'S' AND \n" +
                        "                                trim(u.c_dni)=:dni \n" +
                        "                                AND trim(u.c_usuario)=:username "
        )
        .setParameter("dni", user.getDocumento())
        .setParameter("username", user.getUsername())
        .getResultList();

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
