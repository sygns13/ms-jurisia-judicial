package pj.gob.pe.judicial.dao.mysql;

import java.util.List;

/**
 * Acceso a las instancias habilitadas en MySQL (JURISDB_JUDICIAL.Instancias).
 * Reemplaza los listados de instancias que estaban en duro dentro de las consultas
 * nativas del SIJ (Sybase).
 */
public interface InstanciaHabilitadaDAO {

    /**
     * Instancias registradas en MySQL con activo = 1 y borrado = 0.
     */
    List<String> listarIdInstanciasHabilitadas();

    /**
     * Devuelve los idInstancia habilitados concatenados y entrecomillados, listos para
     * usarse dentro de una cláusula IN de las consultas nativas (ej: '301','302','701').
     */
    String obtenerFiltroInstancias();
}
