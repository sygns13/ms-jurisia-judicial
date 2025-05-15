package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.SedeDAO;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.service.SedeService;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {


    private final SedeDAO sedeDAO;
    private final SecurityService securityService;

    @Override
    public List<DataSedeDTO> findActiveSedes(String SessionId) throws Exception {

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

        return sedeDAO.findActiveSedes(responseLogin.getUser());
    }
}
