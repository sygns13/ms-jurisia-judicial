package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.model.mysql.entities.Sede;
import pj.gob.pe.judicial.repository.mysql.SedeRepository;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.service.mysql.SedeMySqlService;
import pj.gob.pe.judicial.utils.Constantes;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeMySqlServiceImpl implements SedeMySqlService {


    private final SedeRepository sedeRepository;

    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public List<Sede> getAllSedes(String SessionId) {

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

        return sedeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sede> getSedeById(String SessionId, String id) {

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

        return sedeRepository.findById(id);
    }

    @Override
    @Transactional
    public Sede createSede(String SessionId, Sede sede) {

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

        // Asignar valores de auditoría antes de guardar
        sede.setRegDatetime(LocalDateTime.now());
        sede.setRegDate(LocalDate.now());
        sede.setRegTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        sede.setActivo(Constantes.REGISTRO_ACTIVO);
        sede.setBorrado(Constantes.REGISTRO_NO_BORRADO);
        sede.setRegUserId(responseLogin.getUser().getIdUser());

        // Asegurar la consistencia de la relación bidireccional
        if (sede.getInstancias() != null) {
            sede.getInstancias().forEach(instancia -> instancia.setSede(sede));
        }

        return sedeRepository.save(sede);
    }

    @Override
    @Transactional
    public Optional<Sede> updateSede(String SessionId, String id, Sede sedeDetails) {

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

        return sedeRepository.findById(id).map(sede -> {
            sede.setNombre(sedeDetails.getNombre());
            sede.setDireccion(sedeDetails.getDireccion());
            sede.setCodDistrito(sedeDetails.getCodDistrito());

            // Actualizar campos de auditoría
            sede.setUpdDatetime(LocalDateTime.now());
            sede.setUpdDate(LocalDate.now());
            sede.setUpdTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            sede.setUpdUserId(responseLogin.getUser().getIdUser());

            // Aquí se podría agregar lógica para actualizar la lista de instancias si es necesario

            return sedeRepository.save(sede);
        });
    }

    @Override
    @Transactional
    public void deleteSede(String SessionId, String id) {

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

        sedeRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void upsertSedes(String SessionId, List<Sede> sedesFromRequest) {

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

        Map<String, Sede> sedesFromRequestMap = sedesFromRequest.stream()
                .collect(Collectors.toMap(Sede::getIdSede, Function.identity()));

        List<Sede> sedesFromDb = sedeRepository.findAll();

        // 1. Eliminar Sedes que no vienen en la petición
        List<Sede> sedesToDelete = sedesFromDb.stream()
                .filter(dbSede -> !sedesFromRequestMap.containsKey(dbSede.getIdSede()))
                .collect(Collectors.toList());
        if (!sedesToDelete.isEmpty()) {
            sedeRepository.deleteAll(sedesToDelete);
        }

        // 2. Crear y Actualizar Sedes
        for (Sede requestSede : sedesFromRequest) {
            Optional<Sede> dbSedeOpt = sedeRepository.findById(requestSede.getIdSede());

            if (dbSedeOpt.isPresent()) { // Actualizar Sede existente
                Sede dbSede = dbSedeOpt.get();
                dbSede.setNombre(requestSede.getNombre());
                dbSede.setDireccion(requestSede.getDireccion());
                dbSede.setCodDistrito(requestSede.getCodDistrito());

                dbSede.setUpdDatetime(fechaNow);
                dbSede.setUpdDate(fechaNow.toLocalDate());
                dbSede.setUpdTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
                dbSede.setUpdUserId(responseLogin.getUser().getIdUser());

                updateInstanciasForSede(dbSede, requestSede.getInstancias(), responseLogin.getUser().getIdUser(), fechaNow);
                sedeRepository.save(dbSede);

            } else { // Crear nueva Sede
                requestSede.setRegDatetime(fechaNow);
                requestSede.setRegDate(fechaNow.toLocalDate());
                requestSede.setRegTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
                requestSede.setActivo(Constantes.REGISTRO_ACTIVO);
                requestSede.setBorrado(Constantes.REGISTRO_NO_BORRADO);
                requestSede.setRegUserId(responseLogin.getUser().getIdUser());

                if (requestSede.getInstancias() != null) {
                    requestSede.getInstancias().forEach(instancia -> {
                        instancia.setSede(requestSede);
                        instancia.setRegDatetime(fechaNow);
                        instancia.setRegDate(fechaNow.toLocalDate());
                        instancia.setRegTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
                        instancia.setActivo(Constantes.REGISTRO_ACTIVO);
                        instancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
                        instancia.setRegUserId(responseLogin.getUser().getIdUser());
                    });
                }
                sedeRepository.save(requestSede);
            }
        }
    }


    private void updateInstanciasForSede(Sede dbSede, List<Instancia> requestInstancias, Long iduser, LocalDateTime fechaNow) {
        Map<String, Instancia> requestInstanciasMap = (requestInstancias == null) ? Collections.emptyMap() :
                requestInstancias.stream().collect(Collectors.toMap(Instancia::getIdInstancia, Function.identity()));

        // Eliminar instancias que ya no están en la petición (gracias a orphanRemoval=true)
        dbSede.getInstancias().removeIf(dbInstancia -> !requestInstanciasMap.containsKey(dbInstancia.getIdInstancia()));

        // Actualizar existentes y agregar nuevas
        if (requestInstancias != null) {
            for (Instancia requestInstancia : requestInstancias) {
                Optional<Instancia> dbInstanciaOpt = dbSede.getInstancias().stream()
                        .filter(i -> i.getIdInstancia().equals(requestInstancia.getIdInstancia()))
                        .findFirst();

                if (dbInstanciaOpt.isPresent()) { // Actualizar instancia
                    Instancia dbInstancia = dbInstanciaOpt.get();
                    dbInstancia.setNombre(requestInstancia.getNombre());
                    dbInstancia.setCodDistrito(requestInstancia.getCodDistrito());
                    dbInstancia.setCodProvincia(requestInstancia.getCodProvincia());

                    dbInstancia.setUpdDatetime(fechaNow);
                    dbInstancia.setUpdDate(fechaNow.toLocalDate());
                    dbInstancia.setUpdTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
                    dbInstancia.setUpdUserId(iduser);
                    // ... actualizar otros campos necesarios
                } else { // Agregar nueva instancia
                    requestInstancia.setSede(dbSede);
                    requestInstancia.setRegDatetime(fechaNow);
                    requestInstancia.setRegDate(fechaNow.toLocalDate());
                    requestInstancia.setRegTimestamp(fechaNow.toEpochSecond(ZoneOffset.UTC));
                    requestInstancia.setActivo(Constantes.REGISTRO_ACTIVO);
                    requestInstancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
                    requestInstancia.setRegUserId(iduser);
                    dbSede.getInstancias().add(requestInstancia);
                }
            }
        }
    }
}
