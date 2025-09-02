package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.SedeDAO;
import pj.gob.pe.judicial.model.sybase.dto.*;
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

    @Override
    public List<SedeBaseDTO> getMasterSedes() throws Exception {

        List<SedeBaseDTO> listSedes = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        " SELECT s.c_sede, s.x_desc_sede, s.l_activo, s.c_distrito, s.x_direccion from sede s where s.l_activo = 'S' order by c_sede "
                )
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                SedeBaseDTO sede = new SedeBaseDTO(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3]),
                        String.valueOf(row[4]),
                        null
                );

                listSedes.add(sede);
            });
        }
        //Get Instancias Master
        List<InstanciaBaseDTO> instancias = this.getInstanciasMaster();

        //Asignar Instancias a Sedes
        for(SedeBaseDTO sede : listSedes){
            List<InstanciaBaseDTO> instanciasSede = new ArrayList<>();
            for(InstanciaBaseDTO instancia : instancias){
                if(sede.getCodigoSede().equals(instancia.getCodigoSede())){
                    instanciasSede.add(instancia);
                }
            }
            sede.setInstancias(instanciasSede);
        }

        return listSedes;
    }

    private List<InstanciaBaseDTO> getInstanciasMaster() throws Exception {

        List<InstanciaBaseDTO> listInstancias = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        " SELECT i.c_instancia, i.c_distrito, i.c_provincia, i.c_org_jurisd, i.x_nom_instancia, i.n_instancia, i.x_ubicacion_fisica, i.x_corto, i.c_sede, \n" +
                                "i.c_ubigeo, i.l_ind_baja from instancia i where i.l_ind_baja = 'S' \n" +
                                "order by c_instancia "
                )
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                InstanciaBaseDTO instancia = new InstanciaBaseDTO(
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
