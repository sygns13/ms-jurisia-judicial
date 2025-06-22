package pj.gob.pe.judicial.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.EspecialidadDAO;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.service.EspecialidadService;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadDAO especialidadDAO;
    private final SecurityService securityService;

    @Override
    public List<DataEspecialidadDTO> findEspecialidades(String SessionId) throws Exception {

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

        return especialidadDAO.findEspecialidades(responseLogin.getUser());
    }

    @Override
    public List<DataEspecialidadDTO> findAllEspecialidades() throws Exception {
        return especialidadDAO.findAllEspecialidades();
    }
}
