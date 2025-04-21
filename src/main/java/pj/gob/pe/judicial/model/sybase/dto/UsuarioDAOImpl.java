package pj.gob.pe.judicial.model.sybase.dto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.UsuarioDAO;

@Repository
@Component
public class UsuarioDAOImpl implements UsuarioDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    @Override
    public UsuarioDTO findByCredentials(String username, String password) throws Exception {
        TypedQuery<UsuarioDTO> query = entityManager.createNamedQuery("UsuarioQuery", UsuarioDTO.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        UsuarioDTO usuario = null;

        try {
            usuario = query.getSingleResult();
        }catch (Exception ignored){
            logger.warn("No se encontró datos");
        }

        return usuario;
    }
}
