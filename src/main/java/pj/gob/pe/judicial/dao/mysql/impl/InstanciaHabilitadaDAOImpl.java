package pj.gob.pe.judicial.dao.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.mysql.InstanciaHabilitadaDAO;
import pj.gob.pe.judicial.repository.mysql.InstanciaRepository;
import pj.gob.pe.judicial.utils.Constantes;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InstanciaHabilitadaDAOImpl implements InstanciaHabilitadaDAO {

    private final InstanciaRepository instanciaRepository;

    @Override
    public List<String> listarIdInstanciasHabilitadas() {

        return instanciaRepository
                .listarIdInstanciasPorEstado(Constantes.REGISTRO_ACTIVO, Constantes.REGISTRO_NO_BORRADO)
                .stream()
                // El idInstancia se concatena dentro del SQL nativo, por eso se descarta
                // cualquier valor vacío o con comillas que rompería la cláusula IN
                .filter(idInstancia -> idInstancia != null && !idInstancia.trim().isEmpty() && !idInstancia.contains("'"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Override
    public String obtenerFiltroInstancias() {

        String filtroInstancias = listarIdInstanciasHabilitadas().stream()
                .map(idInstancia -> "'" + idInstancia + "'")
                .collect(Collectors.joining(","));

        // Sin instancias habilitadas el IN debe seguir siendo válido y no devolver expedientes
        return filtroInstancias.isEmpty() ? "''" : filtroInstancias;
    }
}
