package pj.gob.pe.judicial.service.mysql.impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.model.mysql.entities.Sede;
import pj.gob.pe.judicial.repository.mysql.SedeRepository;
import pj.gob.pe.judicial.service.mysql.SedeMySqlService;
import pj.gob.pe.judicial.utils.Constantes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SedeMySqlServiceImpl implements SedeMySqlService {

    @Autowired
    private SedeRepository sedeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Sede> getAllSedes() {
        return sedeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sede> getSedeById(String id) {
        return sedeRepository.findById(id);
    }

    @Override
    @Transactional
    public Sede createSede(Sede sede) {
        // Asignar valores de auditoría antes de guardar
        sede.setRegDatetime(LocalDateTime.now());
        sede.setActivo(Constantes.REGISTRO_ACTIVO);
        sede.setBorrado(Constantes.REGISTRO_NO_BORRADO);

        // Asegurar la consistencia de la relación bidireccional
        if (sede.getInstancias() != null) {
            sede.getInstancias().forEach(instancia -> instancia.setSede(sede));
        }

        return sedeRepository.save(sede);
    }

    @Override
    @Transactional
    public Optional<Sede> updateSede(String id, Sede sedeDetails) {
        return sedeRepository.findById(id).map(sede -> {
            sede.setNombre(sedeDetails.getNombre());
            sede.setDireccion(sedeDetails.getDireccion());
            sede.setCodDistrito(sedeDetails.getCodDistrito());
            sede.setActivo(sedeDetails.getActivo());

            // Actualizar campos de auditoría
            sede.setUpdDatetime(LocalDateTime.now());

            // Aquí se podría agregar lógica para actualizar la lista de instancias si es necesario

            return sedeRepository.save(sede);
        });
    }

    @Override
    @Transactional
    public void deleteSede(String id) {
        sedeRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void upsertSedes(List<Sede> sedesFromRequest) {
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
                dbSede.setActivo(requestSede.getActivo());
                dbSede.setUpdDatetime(LocalDateTime.now());

                updateInstanciasForSede(dbSede, requestSede.getInstancias());
                sedeRepository.save(dbSede);

            } else { // Crear nueva Sede
                requestSede.setRegDatetime(LocalDateTime.now());
                requestSede.setActivo(Constantes.REGISTRO_ACTIVO);
                requestSede.setBorrado(Constantes.REGISTRO_NO_BORRADO);

                if (requestSede.getInstancias() != null) {
                    requestSede.getInstancias().forEach(instancia -> {
                        instancia.setSede(requestSede);
                        instancia.setRegDatetime(LocalDateTime.now());
                        instancia.setActivo(Constantes.REGISTRO_ACTIVO);
                        instancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
                    });
                }
                sedeRepository.save(requestSede);
            }
        }
    }


    private void updateInstanciasForSede(Sede dbSede, List<Instancia> requestInstancias) {
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
                    dbInstancia.setUpdDatetime(LocalDateTime.now());
                    // ... actualizar otros campos necesarios
                } else { // Agregar nueva instancia
                    requestInstancia.setSede(dbSede);
                    requestInstancia.setRegDatetime(LocalDateTime.now());
                    requestInstancia.setActivo(Constantes.REGISTRO_ACTIVO);
                    requestInstancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
                    dbSede.getInstancias().add(requestInstancia);
                }
            }
        }
    }
}
