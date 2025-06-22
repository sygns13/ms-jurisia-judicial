package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.EspecialidadDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EspecialidadDAOImpl implements EspecialidadDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);


    @Override
    public List<DataEspecialidadDTO> findEspecialidades(UserLogin user) throws Exception {
        List<DataEspecialidadDTO> listEspecialidads = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                " SELECT DISTINCT e.c_especialidad, e.x_desc_especialidad, e.c_cod_especialidad, i.c_instancia \n" +
                        " FROM usuario u \n" +
                        " JOIN usuario_instancia ui ON u.c_usuario = ui.c_usuario \n" +
                        " JOIN instancia i ON i.c_instancia = ui.c_instancia \n" +
                        " JOIN especialidad_instancia ei ON ei.c_instancia = i.c_instancia \n" +
                        " JOIN especialidad e ON e.c_especialidad = ei.c_especialidad\n" +
                        " JOIN sede s ON s.c_sede = i.c_sede \n" +
                        "                                WHERE \n" +
                        "                                ui.l_activo = 'S' AND \n" +
                        "                                trim(u.c_dni)=:dni \n" +
                        "                                AND trim(u.c_usuario)=:username "
        ).setParameter("dni", user.getDocumento())
        .setParameter("username", user.getUsername())
        .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataEspecialidadDTO especialidad = new DataEspecialidadDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3])
                );

                listEspecialidads.add(especialidad);
            });
        }
        return listEspecialidads;
    }

    @Override
    public List<DataEspecialidadDTO> findAllEspecialidades() throws Exception {
        List<DataEspecialidadDTO> listEspecialidads = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        " SELECT DISTINCT e.c_especialidad, e.x_desc_especialidad, e.c_cod_especialidad, i.c_instancia \n" +
                                " FROM usuario u \n" +
                                " JOIN usuario_instancia ui ON u.c_usuario = ui.c_usuario \n" +
                                " JOIN instancia i ON i.c_instancia = ui.c_instancia \n" +
                                " JOIN especialidad_instancia ei ON ei.c_instancia = i.c_instancia \n" +
                                " JOIN especialidad e ON e.c_especialidad = ei.c_especialidad\n" +
                                " JOIN sede s ON s.c_sede = i.c_sede \n" +
                                "                                WHERE \n" +
                                "                                ui.l_activo = 'S' \n"
                )
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataEspecialidadDTO especialidad = new DataEspecialidadDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3])
                );

                listEspecialidads.add(especialidad);
            });
        }
        return listEspecialidads;
    }
}
