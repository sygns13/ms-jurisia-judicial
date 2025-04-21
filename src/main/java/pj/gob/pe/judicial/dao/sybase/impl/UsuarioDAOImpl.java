package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.UsuarioDAO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;

import java.util.List;

@Repository
public class UsuarioDAOImpl implements UsuarioDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    @Override
    public UsuarioDTO findByCredentials(String username, String password) throws Exception {
        /*
        TypedQuery<UsuarioDTO> query = entityManager.createNamedQuery(
                "UsuarioDTO.findByCredentials",
                UsuarioDTO.class
        );
         */
        /*
        Query query = entityManager.createNativeQuery(
                "SELECT u.c_dni, u.c_ape_paterno, … FROM usuario u " +
                        "WHERE u.l_activo='S' AND u.c_dni=:password AND u.c_usuario=:username",
                "UsuarioFullMapping"
        );
        query.setParameter("username", username);
        query.setParameter("password", password);
        UsuarioDTO usuario = null;

        try {
            return (UsuarioDTO) query.getSingleResult();
        }catch (Exception ignored){
            logger.warn("No se encontró datos");
        }
        */

        UsuarioDTO usuario = null;

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT trim(u.c_dni), u.c_ape_paterno, u.c_ape_materno, u.c_nombres, trim(u.c_dni) " +
                                "FROM usuario u WHERE u.l_activo='S' AND trim(u.c_dni)=:password AND trim(u.c_usuario)=:username"
                )
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();

        if (!resultList.isEmpty()) {
            Object[] row = resultList.get(0);
            usuario = new UsuarioDTO(
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    (String) row[3],
                    (String) row[4]
            );
            return usuario;
        }

        return usuario;
    }
}
