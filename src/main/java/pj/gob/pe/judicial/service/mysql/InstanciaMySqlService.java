package pj.gob.pe.judicial.service.mysql;


import pj.gob.pe.judicial.model.mysql.dto.SedeInstanciaDTO;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.utils.beans.InputSedeInstancia;

import java.util.List;
import java.util.Optional;

public interface InstanciaMySqlService {

    List<Instancia> getAllInstancias(String SessionId);

    Optional<Instancia> getInstanciaById(String SessionId, String id);

    Instancia createInstancia(String SessionId, Instancia instancia);

    Optional<Instancia> updateInstancia(String SessionId, String id, Instancia instanciaDetails);

    void deleteInstancia(String SessionId, String id);

    /**
     * Lista las combinaciones Sede/Instancia registradas en MySQL (JURISDB_JUDICIAL).
     */
    List<SedeInstanciaDTO> listarSedesInstancias(String SessionId);

    /**
     * Registra la combinación Sede/Instancia en MySQL. Si la Sede ya existe solo la actualiza,
     * y en cualquier caso registra la nueva Instancia. Si la combinación ya se encuentra
     * registrada, o la Instancia pertenece a otra Sede, se rechaza el registro.
     */
    SedeInstanciaDTO registrarSedeInstancia(String SessionId, InputSedeInstancia input);

    /**
     * Elimina la combinación Sede/Instancia en MySQL, eliminando únicamente la Instancia.
     */
    void eliminarSedeInstancia(String SessionId, String idSede, String idInstancia);
}
