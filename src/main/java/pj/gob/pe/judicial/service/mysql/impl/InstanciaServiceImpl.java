package pj.gob.pe.judicial.service.mysql.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.repository.mysql.InstanciaRepository;
import pj.gob.pe.judicial.service.mysql.InstanciaService;
import pj.gob.pe.judicial.utils.Constantes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InstanciaServiceImpl implements InstanciaService {

    @Autowired
    private InstanciaRepository instanciaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Instancia> getAllInstancias() {
        return instanciaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instancia> getInstanciaById(String id) {
        return instanciaRepository.findById(id);
    }

    @Override
    @Transactional
    public Instancia createInstancia(Instancia instancia) {
        // Aquí podrías añadir lógica para buscar y asociar la Sede
        // si en el JSON de entrada solo viene el id de la sede.
        instancia.setRegDatetime(LocalDateTime.now());
        instancia.setActivo(Constantes.REGISTRO_ACTIVO);
        instancia.setBorrado(Constantes.REGISTRO_NO_BORRADO);
        return instanciaRepository.save(instancia);
    }

    @Override
    @Transactional
    public Optional<Instancia> updateInstancia(String id, Instancia instanciaDetails) {
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
            instancia.setActivo(instanciaDetails.getActivo());
            instancia.setUpdDatetime(LocalDateTime.now());
            return instanciaRepository.save(instancia);
        });
    }

    @Override
    @Transactional
    public void deleteInstancia(String id) {
        instanciaRepository.deleteById(id);
    }
}
