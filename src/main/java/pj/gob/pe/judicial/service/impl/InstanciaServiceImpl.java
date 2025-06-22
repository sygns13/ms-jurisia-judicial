package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.InstanciaDAO;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.service.InstanciaService;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanciaServiceImpl implements InstanciaService {


    private final InstanciaDAO instanciaDAO;
    private final SecurityService securityService;

    @Override
    public List<DataInstanciaDTO> findActiveInstancias(String SessionId) throws Exception {

        String errorValidacion = "";

        if(SessionId == null || SessionId.isEmpty()){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        ResponseLogin responseLogin = securityService.GetSessionData(SessionId);

        if(responseLogin == null || !responseLogin.isSuccess() || !responseLogin.isItemFound() || responseLogin.getUser() == null){
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }

        return instanciaDAO.findActiveInstancias(responseLogin.getUser());
    }

    @Override
    public List<DataInstanciaDTO> findAllActiveInstancias() throws Exception {
        return instanciaDAO.findAllActiveInstancias();
    }
}
