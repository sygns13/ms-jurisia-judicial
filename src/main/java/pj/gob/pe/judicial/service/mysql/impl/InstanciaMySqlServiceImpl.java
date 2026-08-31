package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.exception.ValidationServiceException;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.dto.SedeInstanciaDTO;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.model.mysql.entities.Sede;
import pj.gob.pe.judicial.repository.mysql.InstanciaRepository;
import pj.gob.pe.judicial.repository.mysql.SedeRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.InstanciaMySqlService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.InputSedeInstancia;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstanciaMySqlServiceImpl implements InstanciaMySqlService {

    private final InstanciaRepository instanciaRepository;

    private final SedeRepository sedeRepository;

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

        Optional<Instancia> instanciaValidate = instanciaRepository.findById(instancia.getIdInstancia());

        if(instanciaValidate.isPresent()){
            errorValidacion = "El juzgado ya se encuentra registrado";
            throw new ValidationServiceException(errorValidacion);
        }

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

    @Override
    @Transactional(readOnly = true)
    public List<SedeInstanciaDTO> listarSedesInstancias(String SessionId) {

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

        List<SedeInstanciaDTO> listSedesInstancias = new ArrayList<>();

        for (Instancia instancia : instanciaRepository.listarInstanciasConSede()) {
            listSedesInstancias.add(convertirSedeInstancia(instancia));
        }

        return listSedesInstancias;
    }

    @Override
    @Transactional
    public SedeInstanciaDTO registrarSedeInstancia(String SessionId, InputSedeInstancia input) {

        LocalDateTime fechaNow = LocalDateTime.now();

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

        String idSede = obtenerValor(input.getIdSede());
        String idInstancia = obtenerValor(input.getIdInstancia());

        if(idSede == null){
            throw new ValidationServiceException("Debe de ingresar la Sede");
        }

        if(idInstancia == null){
            throw new ValidationServiceException("Debe de ingresar la Instancia");
        }

        // Validar que la combinación Sede/Instancia no se encuentre registrada
        Optional<Instancia> instanciaRegistrada = instanciaRepository.findById(idInstancia);

        if(instanciaRegistrada.isPresent()){
            Sede sedeRegistrada = instanciaRegistrada.get().getSede();

            if(sedeRegistrada != null && idSede.equals(sedeRegistrada.getIdSede())){
                throw new ValidationServiceException("La combinación Sede/Instancia ya se encuentra registrada");
            }

            throw new ValidationServiceException("La Instancia " + idInstancia + " ya se encuentra registrada en la Sede " +
                    ((sedeRegistrada == null) ? "(sin Sede asignada)" : sedeRegistrada.getIdSede()));
        }

        // Si la Sede ya existe solo se actualiza, en caso contrario se registra
        Sede sede = sedeRepository.findById(idSede).map(sedeDb -> {
            sedeDb.setNombre(obtenerValor(input.getNombreSede()));
            sedeDb.setDireccion(obtenerValor(input.getDireccionSede()));
            sedeDb.setCodDistrito(obtenerValor(input.getCodDistritoSede()));

            sedeDb.setUpdDate(fechaNow.toLocalDate());
            sedeDb.setUpdDatetime(fechaNow);
            sedeDb.setUpdTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
            sedeDb.setUpdUserId(responseLogin.getUser().getIdUser());

            return sedeRepository.save(sedeDb);
        }).orElseGet(() -> {
            Sede sedeNueva = new Sede();
            sedeNueva.setIdSede(idSede);
            sedeNueva.setNombre(obtenerValor(input.getNombreSede()));
            sedeNueva.setDireccion(obtenerValor(input.getDireccionSede()));
            sedeNueva.setCodDistrito(obtenerValor(input.getCodDistritoSede()));

            sedeNueva.setRegDate(fechaNow.toLocalDate());
            sedeNueva.setRegDatetime(fechaNow);
            sedeNueva.setRegTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
            sedeNueva.setRegUserId(responseLogin.getUser().getIdUser());
            sedeNueva.setActivo(Constantes.REGISTRO_ACTIVO);
            sedeNueva.setBorrado(Constantes.REGISTRO_NO_BORRADO);

            return sedeRepository.save(sedeNueva);
        });

        // Registrar la nueva Instancia asociada a la Sede
        Instancia instancia = new Instancia();
        instancia.setIdInstancia(idInstancia);
        instancia.setNombre(obtenerValor(input.getNombreInstancia()));
        instancia.setCodDistrito(obtenerValor(input.getCodDistrito()));
        instancia.setCodProvincia(obtenerValor(input.getCodProvincia()));
        instancia.setCodOrganoJurisdiccional(obtenerValor(input.getCodOrganoJurisdiccional()));
        instancia.setNumInstancia(input.getNumInstancia());
        instancia.setUbicacion(obtenerValor(input.getUbicacion()));
        instancia.setNombreCorto(obtenerValor(input.getNombreCorto()));
        instancia.setCodUbigeo(obtenerValor(input.getCodUbigeo()));
        instancia.setSede(sede);

        instancia.setRegDate(fechaNow.toLocalDate());
        instancia.setRegDatetime(fechaNow);
        instancia.setRegTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
        instancia.setRegUserId(responseLogin.getUser().getIdUser());
        instancia.setActivo(Constantes.REGISTRO_ACTIVO);
        instancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        return convertirSedeInstancia(instanciaRepository.save(instancia));
    }

    @Override
    @Transactional
    public void eliminarSedeInstancia(String SessionId, String idSede, String idInstancia) {

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

        String codigoSede = obtenerValor(idSede);
        String codigoInstancia = obtenerValor(idInstancia);

        if(codigoSede == null){
            throw new ValidationServiceException("Debe de ingresar la Sede");
        }

        if(codigoInstancia == null){
            throw new ValidationServiceException("Debe de ingresar la Instancia");
        }

        Instancia instancia = instanciaRepository.findById(codigoInstancia)
                .orElseThrow(() -> new ModeloNotFoundException("La combinación Sede/Instancia no se encuentra registrada"));

        Sede sede = instancia.getSede();

        if(sede == null || !codigoSede.equals(sede.getIdSede())){
            throw new ModeloNotFoundException("La combinación Sede/Instancia no se encuentra registrada");
        }

        // Solo se elimina la Instancia, la Sede se mantiene registrada
        if(sede.getInstancias() != null){
            sede.getInstancias().removeIf(instanciaSede -> codigoInstancia.equals(instanciaSede.getIdInstancia()));
        }

        instanciaRepository.delete(instancia);
    }

    private SedeInstanciaDTO convertirSedeInstancia(Instancia instancia) {

        Sede sede = instancia.getSede();

        SedeInstanciaDTO sedeInstancia = new SedeInstanciaDTO();

        if(sede != null){
            sedeInstancia.setIdSede(sede.getIdSede());
            sedeInstancia.setNombreSede(sede.getNombre());
            sedeInstancia.setDireccionSede(sede.getDireccion());
            sedeInstancia.setCodDistritoSede(sede.getCodDistrito());
        }

        sedeInstancia.setIdInstancia(instancia.getIdInstancia());
        sedeInstancia.setNombreInstancia(instancia.getNombre());
        sedeInstancia.setCodDistrito(instancia.getCodDistrito());
        sedeInstancia.setCodProvincia(instancia.getCodProvincia());
        sedeInstancia.setCodOrganoJurisdiccional(instancia.getCodOrganoJurisdiccional());
        sedeInstancia.setNumInstancia(instancia.getNumInstancia());
        sedeInstancia.setUbicacion(instancia.getUbicacion());
        sedeInstancia.setNombreCorto(instancia.getNombreCorto());
        sedeInstancia.setCodUbigeo(instancia.getCodUbigeo());
        sedeInstancia.setActivo(instancia.getActivo());
        sedeInstancia.setBorrado(instancia.getBorrado());
        sedeInstancia.setRegDatetime(instancia.getRegDatetime());
        sedeInstancia.setUpdDatetime(instancia.getUpdDatetime());

        return sedeInstancia;
    }

    private String obtenerValor(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }
}
