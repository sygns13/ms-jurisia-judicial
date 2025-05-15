package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.SedeDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SedeDAOImpl implements SedeDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataSedeDTO> findActiveSedes(UserLogin user) throws Exception {

        List<DataSedeDTO> listSedes = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT s.c_sede, s.x_desc_sede, s.l_activo, s.c_distrito, s.x_direccion \n" +
                                " FROM usuario u \n" +
                                " JOIN usuario_instancia ui ON u.c_usuario = ui.c_usuario \n" +
                                " JOIN instancia i ON i.c_instancia = ui.c_instancia \n" +
                                " JOIN sede s ON s.c_sede = i.c_sede \n" +
                                "                                WHERE \n" +
                                "                                ui.l_activo = 'S' \n" +
                                "                                AND trim(u.c_dni)=:dni \n" +
                                "                                AND trim(u.c_usuario)=:username "
                )
                .setParameter("dni", user.getDocumento())
                .setParameter("username", user.getUsername())
                .getResultList();

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
