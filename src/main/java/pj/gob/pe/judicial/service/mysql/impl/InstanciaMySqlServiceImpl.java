package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.repository.mysql.InstanciaRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.InstanciaMySqlService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstanciaMySqlServiceImpl implements InstanciaMySqlService {

    private final InstanciaRepository instanciaRepository;

    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public List<Instancia> getAllInstancias(String SessionId) {

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

        return instanciaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instancia> getInstanciaById(String SessionId, String id) {

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

        return instanciaRepository.findById(id);
    }

    @Override
    @Transactional
    public Instancia createInstancia(String SessionId, Instancia instancia) {

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

        // Aquí podrías añadir lógica para buscar y asociar la Sede
        // si en el JSON de entrada solo viene el id de la sede.
        instancia.setRegDatetime(LocalDateTime.now());
        instancia.setRegDate(LocalDate.now());
        instancia.setRegTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        instancia.setActivo(Constantes.REGISTRO_ACTIVO);
        instancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
        instancia.setRegUserId(responseLogin.getUser().getIdUser());

        return instanciaRepository.save(instancia);
    }

    @Override
    @Transactional
    public Optional<Instancia> updateInstancia(String SessionId, String id, Instancia instanciaDetails) {

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

        return instanciaRepository.findById(id).map(instancia -> {
            instancia.setNombre(instanciaDetails.getNombre());
            instancia.setCodDistrito(instanciaDetails.getCodDistrito());
            instancia.setCodProvincia(instanciaDetails.getCodProvincia());
            instancia.setCodOrganoJurisdiccional(instanciaDetails.getCodOrganoJurisdiccional());
            instancia.setNumInstancia(instanciaDetails.getNumInstancia());
            instancia.setUbicacion(instanciaDetails.getUbicacion());
            instancia.setNombreCorto(instanciaDetails.getNombreCorto());
            // Se actualiza la referencia al objeto Sede completo
            instancia.setSede(instanciaDetails.getSede());
            instancia.setCodUbigeo(instanciaDetails.getCodUbigeo());

            instancia.setUpdDate(LocalDate.now());
            instancia.setUpdTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            instancia.setUpdUserId(responseLogin.getUser().getIdUser());

            return instanciaRepository.save(instancia);
        });
    }

    @Override
    @Transactional
    public void deleteInstancia(String SessionId, String id) {

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

        instanciaRepository.deleteById(id);
    }
}
