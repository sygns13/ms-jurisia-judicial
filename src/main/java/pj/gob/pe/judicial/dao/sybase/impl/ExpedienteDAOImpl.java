package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.ExpedienteDAO;
import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;
import pj.gob.pe.judicial.utils.beans.InputCabExpedienteCalifica;
import pj.gob.pe.judicial.utils.beans.UserLogin;

import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Repository
public class ExpedienteDAOImpl implements ExpedienteDAO {

    @PersistenceContext(unitName = "sybase")
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    @Override
    public List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception {

        List<DataCabExpedienteDTO> listCabExpedientes = new ArrayList<>();
        String nroexp = String.format("%05d", input.getNumero());
        String n_unico = String.valueOf(input.getAnio()) + nroexp;

        /*
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "select distinct exp.n_unico as N_UNICO,   \n" +
                                "                                SUBSTRING(exp.n_unico, 1, 4) as ANIO,    \n" +
                                "                                SUBSTRING(exp.n_unico, 5, 5) as EXPNRO,    \n" +
                                "                                exp.x_formato AS NUM_EXP,   \n" +
                                "                                exp.n_nro_exp_origen as N_NRO_EXP_ORIGEN,    \n" +
                                "                                exp.n_ano_exp_origen as N_ANO_EXP_ORIGEN,    \n" +
                                "                                me.c_materia as C_MATERIA,   \n" +
                                "                                sed.x_desc_sede AS SEDE,   \n" +
                                "                                org.x_nom_org_jurisd AS ORGANO,   \n" +
                                "                                esp.x_desc_especialidad AS ESPECIALIDAD,   \n" +
                                "                                inst.c_instancia as C_INSTANCIA, \n" +
                                "                                inst.x_nom_instancia AS INSTANCIA, \n" +
                                "                                ma.X_DESC_MATERIA AS X_DESC_MATERIA,\n" +
                                "                                exp.F_INICIO AS F_INICIO,\n" +
                                "                                em.X_DESC_ESTADO AS X_DESC_ESTADO, \n" +
                                "                                eu.c_ubicacion AS C_UBICACION,\n" +
                                "                                ue.x_desc_ubicacion AS DESC_UBICACION, \n" +
                                "                                CASE \n" +
                                "                                   WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "                                   WHEN ie.l_ind_digital = 'S' THEN 'Electrónico' \n" +
                                "                                   ELSE 'Desconocido' \n" +
                                "                               END AS TIPO_EXPEDIENTE \n" +
                                "                                from expediente exp   \n" +
                                "                                inner join    \n" +
                                "                                instancia inst on exp.c_instancia=inst.c_instancia   \n" +
                                "                                inner join    \n" +
                                "                                  sede sed on inst.c_sede=sed.c_sede    \n" +
                                "                                inner join    \n" +
                                "                                   organo_jurisdiccional org on inst.c_org_jurisd=org.c_org_jurisd        \n" +
                                "                                inner join    \n" +
                                "                                   especialidad esp on exp.c_especialidad=esp.c_especialidad   \n" +
                                "                                inner join        \n" +
                                "                                expediente_estado ee on exp.n_unico=ee.n_unico AND ee.l_ultimo = 'S'    \n" +
                                "                                inner join   \n" +
                                "                                materia_expediente me on exp.n_unico=me.n_unico   \n" +
                                "                                INNER JOIN \n" +
                                "                                materia ma ON me.c_materia = ma.c_materia \n" +
                                "                                INNER JOIN \n" +
                                "                                estado_maestro em ON em.c_estado = ee.c_estado \n" +
                                "                                INNER JOIN \n" +
                                "                                expediente_ubicacion eu ON eu.n_unico = exp.n_unico AND eu.l_ultimo = 'S' \n" +
                                "                                INNER JOIN \n" +
                                "                                ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion \n" +
                                "                                INNER JOIN \n" +
                                "                                instancia_expediente ie ON ie.n_unico = exp.n_unico   " +
                                "where    " +
                                "ee.l_ultimo='S'    " +
                                "AND exp.l_anulado='N'   " +
                                "AND exp.c_instancia=:instancia   " +
                                "AND exp.c_especialidad=:especialidad    " +
                                "AND SUBSTRING(exp.n_unico, 1, 9)=:numUnico"
                )
                .setParameter("instancia", input.getInstancia())
                .setParameter("especialidad", input.getEspecialidad())
                .setParameter("numUnico", n_unico)
                .getResultList();
         */

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
                                "    e.n_unico, \n" +
                                "    SUBSTRING(e.n_unico, 1, 4) as ANIO, \n" +
                                "    SUBSTRING(e.n_unico, 5, 5) as EXPNRO, \n" +
                                "    e.X_FORMATO, \n" +
                                "    ma.c_materia, \n" +
                                "    e.c_especialidad, \n" +
                                "    i.c_instancia, \n" +
                                "    i.x_nom_instancia, \n" +
                                "    ma.X_DESC_MATERIA, \n" +
                                "    e.F_INICIO, \n" +
                                "    em.X_DESC_ESTADO, \n" +
                                "    eu.c_ubicacion, \n" +
                                "    ue.x_desc_ubicacion, \n" +
                                "    CASE \n" +
                                "        WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "        WHEN ie.l_ind_digital = 'S' THEN 'Digital' \n" +
                                "        ELSE 'Desconocido' \n" +
                                "    END AS tipo_expediente, \n" +
                                "    SUBSTRING(e.X_FORMATO,12,1) as n_incidente \n" +
                                "FROM expediente e \n" +
                                "INNER JOIN expediente_estado ee ON ee.n_unico = e.n_unico AND ee.l_ultimo = 'S' and ee.n_incidente=e.n_incidente \n" +
                                "INNER JOIN estado_maestro em ON em.c_estado = ee.c_estado \n" +
                                "INNER JOIN materia_expediente m ON m.n_unico = e.n_unico \n" +
                                "INNER JOIN materia ma ON m.c_materia = ma.c_materia \n" +
                                "INNER JOIN expediente_ubicacion eu ON eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' and eu.n_incidente=e.n_incidente \n" +
                                "INNER JOIN ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion \n" +
                                "INNER JOIN instancia_expediente ie ON ie.n_unico = e.n_unico and ie.l_ultimo='S' \n" +
                                "INNER JOIN instancia i ON ie.c_instancia = i.c_instancia \n" +
                                "WHERE\n" +
                                "    ie.c_instancia in('301','302','701','702','044','118','024', '025') AND \n" +
                                "    ee.l_ultimo='S'     \n" +
                                "AND e.l_anulado='N'    \n" +
                                "AND ie.l_ind_digital IN ('S','N')    \n" +
                                "AND ie.c_instancia= :instancia    \n" +
                                "AND ie.c_especialidad= :especialidad     \n" +
                                "AND SUBSTRING(e.n_unico, 1, 9)= :numUnico   \n" +
                                "ORDER BY SUBSTRING(e.X_FORMATO,12,1) ASC"
                )
                .setParameter("instancia", input.getInstancia())
                .setParameter("especialidad", input.getEspecialidad())
                .setParameter("numUnico", n_unico)
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataCabExpedienteDTO expediente = new DataCabExpedienteDTO(
                        row[0] != null ? ((BigDecimal) row[0]).longValue() : null,
                        row[1] != null ? String.valueOf(row[1]) : null,
                        row[2] != null ? String.valueOf(row[2]) : null,
                        row[3] != null ? String.valueOf(row[3]) : null,
                        0L,
                        0L,
                        row[4] != null ? String.valueOf(row[4]) : null,
                        "",
                        "",
                        row[5] != null ? String.valueOf(row[5]) : null,
                        row[6] != null ? String.valueOf(row[6]) : null,
                        row[7] != null ? String.valueOf(row[7]) : null,
                        row[8] != null ? String.valueOf(row[8]) : null,
                        row[9] != null ? ((Timestamp) row[9]).toLocalDateTime() : null,
                        row[10] != null ? String.valueOf(row[10]) : null,
                        row[11] != null ? String.valueOf(row[11]) : null,
                        row[12] != null ? String.valueOf(row[12]) : null,
                        row[13] != null ? String.valueOf(row[13]) : null,
                        row[14] != null ? String.valueOf(row[14]) : null
                );

                listCabExpedientes.add(expediente);
            });
        }

        return listCabExpedientes;
    }

    @Override
    public List<DataCabExpedienteCalificarDTO> findCabExpedientesCalificar(InputCabExpedienteCalifica input, UserLogin user) throws Exception {

        Map<Long, DataCabExpedienteCalificarDTO> expedienteMap = new LinkedHashMap<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
                                "    e.n_unico, \n" +
                                "    SUBSTRING(e.n_unico, 1, 4) AS ANIO, \n" +
                                "    SUBSTRING(e.n_unico, 5, 5) AS EXPNRO, \n" +
                                "    e.X_FORMATO, \n" +
                                "    m.c_materia, \n" +
                                "    mim.c_motivo_ingreso, \n" +
                                "    pm.c_proceso, \n" +
                                "    e.c_especialidad, \n" +
                                "    i.c_instancia, \n" +
                                "    i.x_nom_instancia, \n" +
                                "    mm.X_DESC_MATERIA, \n" +
                                "    u_juez.c_usuario AS juez, \n" +                  // 11
                                "    u_juez.x_nom_usuario AS nombre_juez, \n" +       // 12
                                "    aa.c_usuario AS especialista, \n" +             // 13
                                "    u_esp.x_nom_usuario AS nombre_especialista, \n" + // 14
                                "    ui.c_usuario, \n" +                             // 15
                                "    dd.cod_tipo_presentacion, \n" +                 // 16
                                "    e.F_INICIO, \n" +                               // 17
                                "    em.X_DESC_ESTADO, \n" +                         // 18
                                "    eu.c_ubicacion, \n" +                          // 19
                                "    ue.x_desc_ubicacion, \n" +                      // 20
                                "    CASE \n" +
                                "        WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "        WHEN ie.l_ind_digital = 'S' THEN 'Digital' \n" +
                                "        ELSE 'Desconocido' \n" +
                                "    END AS tipo_expediente, \n" +                   // 21
                                "    sf.x_ip, \n" +                                  // 22
                                "    sf.c_usuario, \n" +                             // 23
                                "    sf.c_clave, \n" +                               // 24
                                "    dd.x_ruta_archivo, \n" +                        // 25
                                "    dd.x_nombre_archivo, \n" +                      // 26
                                "    CASE \n" +
                                "        WHEN sf.x_ip IS NULL THEN NULL \n" +
                                "        ELSE 'ftp://' + sf.c_usuario + ':' + sf.c_clave + '@' + \n" +
                                "             sf.x_ip + '/' + dd.x_ruta_archivo + '/' + dd.x_nombre_archivo \n" +
                                "    END AS RUTA, \n" +                              // 27
                                "    SUBSTRING(e.X_FORMATO, 12, 1) AS n_incidente \n" + // 28
                                "FROM expediente e \n" +
                                "INNER JOIN instancia_expediente ie \n" +
                                "    ON ie.n_unico = e.n_unico \n" +
                                "   AND ie.c_especialidad = 'FC' \n" +
                                "   AND ie.l_ultimo = 'S' \n" +
                                "   AND ie.n_incidente = '0' \n" +
                                "INNER JOIN instancia i \n" +
                                "    ON ie.c_instancia = i.c_instancia \n" +
                                "INNER JOIN asignado_a aa \n" +
                                "    ON e.n_unico = aa.n_unico \n" +
                                "   AND e.n_incidente = aa.n_incidente \n" +
                                "   AND aa.l_ultimo = 'S' \n" +
                                "INNER JOIN usuario u_esp \n" +
                                "    ON u_esp.c_usuario = aa.c_usuario \n" +
                                "INNER JOIN usuario_instancia ui \n" +
                                "    ON i.c_instancia = ui.c_instancia \n" +
                                "   AND ui.l_activo = 'S' \n" +
                                "LEFT JOIN usuario_instancia uj \n" +
                                "    ON uj.c_usuario = ( \n" +
                                "        SELECT TOP 1 ui2.c_usuario \n" +
                                "        FROM usuario_instancia ui2, usuario u2 \n" +
                                "        WHERE ui2.c_usuario = u2.c_usuario \n" +
                                "          AND ui2.c_instancia = i.c_instancia \n" +
                                "          AND ui2.l_activo = 'S' \n" +
                                "          AND u2.c_perfil = '01' \n" +
                                "        ORDER BY ui2.l_titular DESC \n" +
                                "    ) \n" +
                                "LEFT JOIN usuario u_juez \n" +
                                "    ON u_juez.c_usuario = uj.c_usuario \n" +
                                "INNER JOIN proceso_maestro pm \n" +
                                "    ON ie.c_proceso = pm.c_proceso \n" +
                                "   AND ie.c_proceso = '064' \n" +
                                "INNER JOIN motivo_ingreso_maestro mim \n" +
                                "    ON ie.c_motivo_ingreso = mim.c_motivo_ingreso \n" +
                                "   AND ie.c_motivo_ingreso = 'LO2' \n" +
                                "INNER JOIN materia_expediente m \n" +
                                "    ON m.n_unico = ie.n_unico \n" +
                                "   AND ie.n_incidente = m.n_incidente \n" +
                                "   AND m.l_activo = 'S' \n" +
                                "INNER JOIN materia_maestro mm \n" +
                                "    ON m.c_materia = mm.c_materia \n" +
                                "   AND m.c_materia IN ('551','569','637','652','654') \n" +
                                "INNER JOIN expediente_estado ee \n" +
                                "    ON ee.n_unico = e.n_unico \n" +
                                "   AND ee.n_incidente = e.n_incidente \n" +
                                "   AND ee.l_ultimo = 'S' \n" +
                                "   AND ee.c_estado = '092' \n" +
                                "INNER JOIN estado_maestro em \n" +
                                "    ON em.c_estado = ee.c_estado \n" +
                                "INNER JOIN expediente_ubicacion eu \n" +
                                "    ON eu.n_unico = e.n_unico \n" +
                                "   AND eu.n_incidente = e.n_incidente \n" +
                                "   AND eu.l_ultimo = 'S' \n" +
                                "INNER JOIN ubicacion_expediente ue \n" +
                                "    ON eu.c_ubicacion = ue.c_ubicacion \n" +
                                "LEFT JOIN documento_digital dd \n" +
                                "    ON e.n_unico = dd.n_unico \n" +
                                "   AND e.n_incidente = dd.n_incidente \n" +
                                "   AND dd.l_tipo_doc = 'EXP' \n" +
                                "LEFT JOIN servidor_ftp sf \n" +
                                "    ON sf.n_item = dd.n_servicio_ftp \n" +
                                "   AND sf.n_correlativo_ftp = dd.n_correlativo_ftp \n" +
                                "   AND sf.l_activo = 'S' \n" +
                                "WHERE e.l_anulado = 'N' \n" +
                                "  AND e.l_acumulado = 'N' \n" +
                                "  AND e.n_incidente = 0 \n" +
                                "  AND e.f_inicio >= :fechaInicio \n" +
                                "  AND e.f_inicio < :fechaFinal \n" +
                                "  AND trim(ui.c_usuario) = :username"
                )
                .setParameter("fechaInicio", input.getFechaInicio())
                .setParameter("fechaFinal", input.getFechaFinal())
                .setParameter("username", user.getUsername())
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                Long nunico = row[0] != null ? ((Number) row[0]).longValue() : null;
                if (nunico == null) return;

                String nombreArchivo = row[26] != null ? String.valueOf(row[26]) : null; // antes row[22]

                // Si el expediente ya fue procesado, solo agrega el archivo a la lista
                if (expedienteMap.containsKey(nunico)) {
                    if (nombreArchivo != null) {
                        expedienteMap.get(nunico).getArchivos().add(nombreArchivo);
                    }
                    return;
                }

                DataCabExpedienteCalificarDTO dto = new DataCabExpedienteCalificarDTO();

                dto.setNUnico(nunico);
                dto.setAnio(row[1] != null ? String.valueOf(row[1]) : null);
                dto.setExpNro(row[2] != null ? String.valueOf(row[2]) : null);
                dto.setXFormato(row[3] != null ? String.valueOf(row[3]) : null);
                dto.setCMateria(row[4] != null ? String.valueOf(row[4]) : null);
                // row[5] = c_motivo_ingreso — no mapeado
                // row[6] = c_proceso       — no mapeado
                dto.setCEspecialidad(row[7] != null ? String.valueOf(row[7]) : null);
                dto.setCInstancia(row[8] != null ? String.valueOf(row[8]) : null);
                dto.setXNomInstancia(row[9] != null ? String.valueOf(row[9]) : null);
                dto.setXDescMateria(row[10] != null ? String.valueOf(row[10]) : null);
                // row[11] = juez (c_usuario) — código del juez, no mapeado
                dto.setXDescJuez(row[12] != null ? String.valueOf(row[12]) : null);        // nombre_juez
                // row[13] = especialista (c_usuario) — código del especialista, no mapeado
                dto.setXDescEspecialista(row[14] != null ? String.valueOf(row[14]) : null); // nombre_especialista
                // row[15] = ui.c_usuario (usuario de la instancia) — no mapeado
                // row[16] = dd.cod_tipo_presentacion — no mapeado

                if (row[17] != null) {
                    if (row[17] instanceof Timestamp) {
                        dto.setFInicio(((Timestamp) row[17]).toLocalDateTime());
                    } else {
                        dto.setFInicio(((java.util.Date) row[17]).toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                    }
                }

                dto.setXDescEstado(row[18] != null ? String.valueOf(row[18]) : null);
                dto.setCUbicacion(row[19] != null ? String.valueOf(row[19]) : null);
                dto.setXDescUbicacion(row[20] != null ? String.valueOf(row[20]) : null);
                dto.setTipoExpediente(row[21] != null ? String.valueOf(row[21]) : null);
                dto.setXIp(row[22] != null ? String.valueOf(row[22]) : null);
                dto.setCUsuario(row[23] != null ? String.valueOf(row[23]) : null);   // sf.c_usuario
                dto.setCClave(row[24] != null ? String.valueOf(row[24]) : null);
                dto.setXRutaArchivo(row[25] != null ? String.valueOf(row[25]) : null);
                dto.setXNombreArchivo(nombreArchivo);                                // row[26]
                dto.setRutaCompleta(row[27] != null ? String.valueOf(row[27]) : null);
                dto.setNIncidente(row[28] != null ? String.valueOf(row[28]) : null);

                dto.setArchivos(new ArrayList<>());
                if (nombreArchivo != null) {
                    dto.getArchivos().add(nombreArchivo);
                }
                completarPartes(dto, nunico);
                expedienteMap.put(nunico, dto);
            });
        }

        return new ArrayList<>(expedienteMap.values());
    }

    @Override
    public List<DataCabExpedienteCalificarDTO> findCabExpedientesSentenciarPorNunico(Long nUnico) throws Exception {

        // Basado en la consulta de expedientes por calificar, pero para SENTENCIAR:
        // - No se restringe por ee.c_estado (se traen todos los estados).
        // - Se lista un único expediente, filtrando por el n_unico ya resuelto
        //   (a partir de Sede/Instancia/Especialidad/Número/Año) en el servicio.
        // - No se hace join con documento_digital (los PDF de Resoluciones/Digitalizados
        //   se cargan bajo demanda con findDocumentosDigitales al presionar cada botón).
        Map<Long, DataCabExpedienteCalificarDTO> expedienteMap = new LinkedHashMap<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
                                "    e.n_unico, \n" +                               // 0
                                "    SUBSTRING(e.n_unico, 1, 4) AS ANIO, \n" +      // 1
                                "    SUBSTRING(e.n_unico, 5, 5) AS EXPNRO, \n" +    // 2
                                "    e.X_FORMATO, \n" +                            // 3
                                "    m.c_materia, \n" +                           // 4
                                "    e.c_especialidad, \n" +                      // 5
                                "    i.c_instancia, \n" +                         // 6
                                "    i.x_nom_instancia, \n" +                     // 7
                                "    mm.X_DESC_MATERIA, \n" +                     // 8
                                "    u_juez.x_nom_usuario AS nombre_juez, \n" +   // 9
                                "    u_esp.x_nom_usuario AS nombre_especialista, \n" + // 10
                                "    e.F_INICIO, \n" +                            // 11
                                "    em.X_DESC_ESTADO, \n" +                      // 12
                                "    eu.c_ubicacion, \n" +                        // 13
                                "    ue.x_desc_ubicacion, \n" +                   // 14
                                "    CASE \n" +
                                "        WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "        WHEN ie.l_ind_digital = 'S' THEN 'Digital' \n" +
                                "        ELSE 'Desconocido' \n" +
                                "    END AS tipo_expediente, \n" +                 // 15
                                "    SUBSTRING(e.X_FORMATO, 12, 1) AS n_incidente \n" + // 16
                                "FROM expediente e \n" +
                                "INNER JOIN instancia_expediente ie \n" +
                                "    ON ie.n_unico = e.n_unico \n" +
                                "   AND ie.c_especialidad = 'FC' \n" +
                                "   AND ie.l_ultimo = 'S' \n" +
                                "   AND ie.n_incidente = '0' \n" +
                                "INNER JOIN instancia i \n" +
                                "    ON ie.c_instancia = i.c_instancia \n" +
                                "INNER JOIN asignado_a aa \n" +
                                "    ON e.n_unico = aa.n_unico \n" +
                                "   AND e.n_incidente = aa.n_incidente \n" +
                                "   AND aa.l_ultimo = 'S' \n" +
                                "INNER JOIN usuario u_esp \n" +
                                "    ON u_esp.c_usuario = aa.c_usuario \n" +
                                "INNER JOIN usuario_instancia ui \n" +
                                "    ON i.c_instancia = ui.c_instancia \n" +
                                "   AND ui.l_activo = 'S' \n" +
                                "LEFT JOIN usuario_instancia uj \n" +
                                "    ON uj.c_usuario = ( \n" +
                                "        SELECT TOP 1 ui2.c_usuario \n" +
                                "        FROM usuario_instancia ui2, usuario u2 \n" +
                                "        WHERE ui2.c_usuario = u2.c_usuario \n" +
                                "          AND ui2.c_instancia = i.c_instancia \n" +
                                "          AND ui2.l_activo = 'S' \n" +
                                "          AND u2.c_perfil = '01' \n" +
                                "        ORDER BY ui2.l_titular DESC \n" +
                                "    ) \n" +
                                "LEFT JOIN usuario u_juez \n" +
                                "    ON u_juez.c_usuario = uj.c_usuario \n" +
                                "INNER JOIN proceso_maestro pm \n" +
                                "    ON ie.c_proceso = pm.c_proceso \n" +
                                "   AND ie.c_proceso = '064' \n" +
                                "INNER JOIN motivo_ingreso_maestro mim \n" +
                                "    ON ie.c_motivo_ingreso = mim.c_motivo_ingreso \n" +
                                "   AND ie.c_motivo_ingreso = 'LO2' \n" +
                                "INNER JOIN materia_expediente m \n" +
                                "    ON m.n_unico = ie.n_unico \n" +
                                "   AND ie.n_incidente = m.n_incidente \n" +
                                "   AND m.l_activo = 'S' \n" +
                                "INNER JOIN materia_maestro mm \n" +
                                "    ON m.c_materia = mm.c_materia \n" +
                                "   AND m.c_materia IN ('551','569','637','652','654') \n" +
                                "INNER JOIN expediente_estado ee \n" +
                                "    ON ee.n_unico = e.n_unico \n" +
                                "   AND ee.n_incidente = e.n_incidente \n" +
                                "   AND ee.l_ultimo = 'S' \n" +
                                "   AND ee.c_estado IN (SELECT c_estado FROM estado_maestro) \n" +
                                "INNER JOIN estado_maestro em \n" +
                                "    ON em.c_estado = ee.c_estado \n" +
                                "INNER JOIN expediente_ubicacion eu \n" +
                                "    ON eu.n_unico = e.n_unico \n" +
                                "   AND eu.n_incidente = e.n_incidente \n" +
                                "   AND eu.l_ultimo = 'S' \n" +
                                "INNER JOIN ubicacion_expediente ue \n" +
                                "    ON eu.c_ubicacion = ue.c_ubicacion \n" +
                                "WHERE e.l_anulado = 'N' \n" +
                                "  AND e.l_acumulado = 'N' \n" +
                                "  AND e.n_incidente = 0 \n" +
                                "  AND e.n_unico = :nUnico"
                )
                .setParameter("nUnico", nUnico)
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                Long nunico = row[0] != null ? ((Number) row[0]).longValue() : null;
                if (nunico == null || expedienteMap.containsKey(nunico)) {
                    return;
                }

                DataCabExpedienteCalificarDTO dto = new DataCabExpedienteCalificarDTO();

                dto.setNUnico(nunico);
                dto.setAnio(row[1] != null ? String.valueOf(row[1]) : null);
                dto.setExpNro(row[2] != null ? String.valueOf(row[2]) : null);
                dto.setXFormato(row[3] != null ? String.valueOf(row[3]) : null);
                dto.setCMateria(row[4] != null ? String.valueOf(row[4]) : null);
                dto.setCEspecialidad(row[5] != null ? String.valueOf(row[5]) : null);
                dto.setCInstancia(row[6] != null ? String.valueOf(row[6]) : null);
                dto.setXNomInstancia(row[7] != null ? String.valueOf(row[7]) : null);
                dto.setXDescMateria(row[8] != null ? String.valueOf(row[8]) : null);
                dto.setXDescJuez(row[9] != null ? String.valueOf(row[9]) : null);
                dto.setXDescEspecialista(row[10] != null ? String.valueOf(row[10]) : null);

                if (row[11] != null) {
                    if (row[11] instanceof Timestamp) {
                        dto.setFInicio(((Timestamp) row[11]).toLocalDateTime());
                    } else {
                        dto.setFInicio(((java.util.Date) row[11]).toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                    }
                }

                dto.setXDescEstado(row[12] != null ? String.valueOf(row[12]) : null);
                dto.setCUbicacion(row[13] != null ? String.valueOf(row[13]) : null);
                dto.setXDescUbicacion(row[14] != null ? String.valueOf(row[14]) : null);
                dto.setTipoExpediente(row[15] != null ? String.valueOf(row[15]) : null);
                dto.setNIncidente(row[16] != null ? String.valueOf(row[16]) : null);

                dto.setArchivos(new ArrayList<>());
                completarPartes(dto, nunico);
                expedienteMap.put(nunico, dto);
            });
        }

        return new ArrayList<>(expedienteMap.values());
    }

    @Override
    public List<DataDocumentoDigitalDTO> findDocumentosDigitales(Long nUnico, String nIncidente, List<String> tiposDoc) throws Exception {

        List<DataDocumentoDigitalDTO> documentos = new ArrayList<>();

        int incidente = 0;
        if (nIncidente != null && !nIncidente.trim().isEmpty()) {
            try {
                incidente = Integer.parseInt(nIncidente.trim());
            } catch (NumberFormatException ex) {
                incidente = 0;
            }
        }

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
                                "    e.n_unico, \n" +                                   // 0
                                "    e.X_FORMATO, \n" +                                // 1
                                "    dd.l_tipo_doc, \n" +                              // 2
                                "    dd.f_registro, \n" +                              // 3
                                "    CASE \n" +                                        // 4
                                "        WHEN dd.l_tipo_doc = 'R' THEN dd.x_desc_acto_procesal \n" +
                                "        ELSE '' \n" +
                                "    END AS acto_procesal, \n" +
                                "    dd.x_descripcion AS descripcion, \n" +             // 5
                                "    CASE \n" +                                        // 6
                                "        WHEN dd.l_tipo_doc = 'R' THEN dd.x_sumilla \n" +
                                "        WHEN dd.l_tipo_doc = 'EXP' THEN e.x_sumilla \n" +
                                "        WHEN dd.l_tipo_doc = 'ESC' THEN esc.x_sumilla \n" +
                                "        ELSE '' \n" +
                                "    END AS sumilla, \n" +
                                "    sf.x_ip, \n" +                                    // 7
                                "    sf.c_usuario, \n" +                               // 8
                                "    sf.c_clave, \n" +                                 // 9
                                "    dd.x_ruta_archivo, \n" +                          // 10
                                "    dd.x_nombre_archivo, \n" +                        // 11
                                "    CASE \n" +                                        // 12
                                "        WHEN sf.x_ip IS NULL THEN NULL \n" +
                                "        ELSE 'ftp://' + sf.c_usuario + ':' + sf.c_clave + '@' + \n" +
                                "             sf.x_ip + '/' + dd.x_ruta_archivo + '/' + dd.x_nombre_archivo \n" +
                                "    END AS RUTA \n" +
                                "FROM expediente e \n" +
                                "INNER JOIN documento_digital dd \n" +
                                "    ON e.n_unico = dd.n_unico \n" +
                                "   AND e.n_incidente = dd.n_incidente \n" +
                                "   AND dd.l_tipo_doc IN (:tiposDoc) \n" +
                                "   AND dd.l_estado <> 'A' \n" +
                                "LEFT JOIN escrito esc \n" +
                                "    ON esc.n_sec_ingreso = dd.n_sec_ingreso \n" +
                                "   AND e.n_unico = esc.n_unico \n" +
                                "   AND e.n_incidente = esc.n_incidente \n" +
                                "   AND esc.l_ultimo = 'S' \n" +
                                "LEFT JOIN servidor_ftp sf \n" +
                                "    ON sf.n_item = dd.n_servicio_ftp \n" +
                                "   AND sf.n_correlativo_ftp = dd.n_correlativo_ftp \n" +
                                "   AND sf.l_activo = 'S' \n" +
                                "WHERE e.l_anulado = 'N' \n" +
                                "  AND e.n_unico = :nUnico \n" +
                                "  AND e.n_incidente = :nIncidente \n" +
                                "  AND dd.x_nombre_archivo IS NOT NULL \n" +
                                "ORDER BY dd.f_registro"
                )
                .setParameter("tiposDoc", tiposDoc)
                .setParameter("nUnico", nUnico)
                .setParameter("nIncidente", incidente)
                .getResultList();

        for (Object[] row : resultList) {
            DataDocumentoDigitalDTO dto = new DataDocumentoDigitalDTO();
            dto.setNUnico(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setXFormato(row[1] != null ? String.valueOf(row[1]) : null);
            dto.setLTipoDoc(row[2] != null ? String.valueOf(row[2]) : null);
            dto.setFRegistro(row[3] != null ? String.valueOf(row[3]) : null);
            dto.setActoProcesal(row[4] != null ? String.valueOf(row[4]) : null);
            dto.setDescripcion(row[5] != null ? String.valueOf(row[5]) : null);
            dto.setSumilla(row[6] != null ? String.valueOf(row[6]) : null);
            dto.setXip(row[7] != null ? String.valueOf(row[7]) : null);
            dto.setCusuario(row[8] != null ? String.valueOf(row[8]) : null);
            dto.setCclave(row[9] != null ? String.valueOf(row[9]) : null);
            dto.setXrutaArchivo(row[10] != null ? String.valueOf(row[10]) : null);
            dto.setXnombreArchivo(row[11] != null ? String.valueOf(row[11]) : null);
            dto.setRutaCompleta(row[12] != null ? String.valueOf(row[12]) : null);
            documentos.add(dto);
        }

        return documentos;
    }

    @Override
    public List<DataExpedienteDTO> getDataExpediente(Long nUnico, String numIncidente) throws Exception {

        List<DataExpedienteDTO> listDataExpediente = new ArrayList<>();

        int incidente = 0;
        if (numIncidente != null && !numIncidente.trim().isEmpty()) {
            try { incidente = Integer.parseInt(numIncidente.trim()); }
            catch (NumberFormatException ex) { incidente = 0; }
        }

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
                                "    e.n_unico, \n" +                                                     // 0
                                "    e.X_FORMATO, \n" +                                                   // 1
                                "    i.x_nom_instancia, \n" +                                             // 2
                                "    e.c_especialidad, \n" +                                              // 3
                                "    (SELECT MAX(ma.X_DESC_MATERIA) FROM materia_expediente m \n" +
                                "       INNER JOIN materia ma ON ma.c_materia = m.c_materia \n" +
                                "       WHERE m.n_unico = e.n_unico AND m.n_incidente = e.n_incidente) AS x_desc_materia, \n" + // 4
                                "    e.F_INICIO, \n" +                                                    // 5
                                "    (SELECT MAX(em.X_DESC_ESTADO) FROM expediente_estado ee \n" +
                                "       INNER JOIN estado_maestro em ON em.c_estado = ee.c_estado \n" +
                                "       WHERE ee.n_unico = e.n_unico AND ee.n_incidente = e.n_incidente AND ee.l_ultimo = 'S') AS x_desc_estado, \n" + // 6
                                "    (SELECT MAX(eu.c_ubicacion) FROM expediente_ubicacion eu \n" +
                                "       WHERE eu.n_unico = e.n_unico AND eu.n_incidente = e.n_incidente AND eu.l_ultimo = 'S') AS c_ubicacion, \n" + // 7
                                "    (SELECT MAX(ue.x_desc_ubicacion) FROM expediente_ubicacion eu \n" +
                                "       INNER JOIN ubicacion_expediente ue ON ue.c_ubicacion = eu.c_ubicacion \n" +
                                "       WHERE eu.n_unico = e.n_unico AND eu.n_incidente = e.n_incidente AND eu.l_ultimo = 'S') AS x_desc_ubicacion, \n" + // 8
                                "    (SELECT MAX(ui.c_usuario) FROM usuario_instancia ui \n" +
                                "       WHERE ui.c_instancia = ie.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S') AS usuario_juez, \n" + // 9
                                "    (SELECT MAX(u.x_nom_usuario) FROM usuario_instancia ui \n" +
                                "       INNER JOIN usuario u ON u.c_usuario = ui.c_usuario \n" +
                                "       WHERE ui.c_instancia = ie.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S') AS juez, \n" + // 10
                                "    (SELECT MAX(a.c_usuario) FROM asignado_a a \n" +
                                "       WHERE a.n_unico = e.n_unico AND a.n_incidente = e.n_incidente AND a.l_ultimo = 'S') AS usuario_secretario, \n" + // 11
                                "    (SELECT MAX(us.x_nom_usuario) FROM asignado_a a \n" +
                                "       INNER JOIN usuario us ON us.c_usuario = a.c_usuario \n" +
                                "       WHERE a.n_unico = e.n_unico AND a.n_incidente = e.n_incidente AND a.l_ultimo = 'S') AS secretario, \n" + // 12
                                "    CASE WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "         WHEN ie.l_ind_digital = 'S' THEN 'Electrónico' END AS tipo_expediente, \n" + // 13
                                "    ISNULL(p.x_ape_paterno,'') + ' ' + ISNULL(p.x_ape_materno,'') + ' ' + ISNULL(p.x_nombres,'') AS parte, \n" + // 14
                                "    tp.l_tipo_parte, \n" +                                               // 15
                                "    tp.x_desc_parte, \n" +                                               // 16
                                "    p.x_doc_id, \n" +                                                    // 17
                                "    e.c_sede, \n" +                                                      // 18
                                "    e.c_instancia, \n" +                                                 // 19
                                "    SUBSTRING(e.n_unico, 1, 4) as c_year, \n" +                          // 20
                                "    SUBSTRING(e.n_unico, 5, 5) as c_num, \n" +                           // 21
                                "    (SELECT MAX(m.c_materia) FROM materia_expediente m \n" +
                                "       WHERE m.n_unico = e.n_unico AND m.n_incidente = e.n_incidente) AS c_materia, \n" + // 22
                                "    (SELECT MAX(se.x_desc_sede) FROM sede se WHERE se.c_sede = e.c_sede) AS x_desc_sede, \n" + // 23
                                "    (SELECT MAX(esp.x_desc_especialidad) FROM especialidad esp \n" +
                                "       WHERE esp.c_especialidad = e.c_especialidad) AS x_desc_especialidad, \n" + // 24
                                "    SUBSTRING(e.X_FORMATO, 12, 1) as n_incidente \n" +                   // 25
                                "FROM expediente e \n" +
                                "INNER JOIN instancia_expediente ie ON ie.n_unico = e.n_unico AND ie.n_incidente = e.n_incidente AND ie.l_ultimo = 'S' \n" +
                                "INNER JOIN instancia i ON ie.c_instancia = i.c_instancia \n" +
                                "INNER JOIN parte p ON p.n_unico = e.n_unico AND p.n_incidente = e.n_incidente AND p.l_activo = 'S' \n" +
                                "INNER JOIN tipo_parte tp ON tp.l_tipo_parte = p.l_tipo_parte AND tp.l_activo = 'S' \n" +
                                "WHERE e.n_unico = :numUnico AND e.n_incidente = :numIncidente \n" +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("numUnico", nUnico)
                .setParameter("numIncidente", incidente)
                .getResultList();

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataExpedienteDTO expediente = new DataExpedienteDTO(
                        row[0] != null ? ((BigDecimal) row[0]).longValue() : null,
                        row[1] != null ? String.valueOf(row[1]) : null,
                        row[2] != null ? String.valueOf(row[2]) : null,
                        row[3] != null ? String.valueOf(row[3]) : null,
                        row[4] != null ? String.valueOf(row[4]) : null,
                        row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null,
                        row[6] != null ? String.valueOf(row[6]) : null,
                        row[7] != null ? String.valueOf(row[7]) : null,
                        row[8] != null ? String.valueOf(row[8]) : null,
                        row[9] != null ? String.valueOf(row[9]) : null,
                        row[10] != null ? String.valueOf(row[10]) : null,
                        row[11] != null ? String.valueOf(row[11]) : null,
                        row[12] != null ? String.valueOf(row[12]) : null,
                        row[13] != null ? String.valueOf(row[13]) : null,
                        row[14] != null ? String.valueOf(row[14]) : null,
                        row[15] != null ? String.valueOf(row[15]) : null,
                        row[16] != null ? String.valueOf(row[16]) : null,
                        row[17] != null ? String.valueOf(row[17]) : null,
                        row[18] != null ? String.valueOf(row[18]) : null,
                        row[19] != null ? String.valueOf(row[19]) : null,
                        row[20] != null ? String.valueOf(row[20]) : null,
                        row[21] != null ? String.valueOf(row[21]) : null,
                        row[22] != null ? String.valueOf(row[22]) : null,
                        row[23] != null ? String.valueOf(row[23]) : null,
                        row[24] != null ? String.valueOf(row[24]) : null,
                        row[25] != null ? String.valueOf(row[25]) : null
                );

                listDataExpediente.add(expediente);
            });
        }

        return listDataExpediente;
    }

    @Override
    public List<CabExpedienteChatDTO> findByNumeroExpediente(String numeroExpediente) {

        List<CabExpedienteChatDTO> listDataExpediente = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "  SELECT \n" +
                                "    e.n_unico, \n" +
                                "    e.x_formato, \n" +
                                "    ie.n_incidente, \n" +
                                "    CASE \n" +
                                "        WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "        WHEN ie.l_ind_digital = 'S' THEN 'Digital' \n" +
                                "        ELSE 'Desconocido' \n" +
                                "    END AS tipo_expediente, \n" +
                                "    e.c_especialidad, \n" +
                                "    ie.c_instancia, \n" +
                                "    i.x_nom_instancia, \n" +
                                "    o.x_nom_org_jurisd, \n" +
                                "    s.x_desc_sede, \n" +
                                "    e.l_anulado, \n" +
                                "    ie.l_ultimo \n" +
                                "FROM expediente e \n" +
                                "JOIN instancia_expediente ie ON e.n_unico = ie.n_unico \n" +
                                "JOIN instancia i ON ie.c_instancia = i.c_instancia \n" +
                                "JOIN sede s ON i.c_sede = s.c_sede \n" +
                                "JOIN organo_jurisdiccional o ON i.c_org_jurisd = o.c_org_jurisd \n" +
                                "WHERE \n" +
                                "        e.l_anulado = 'N'\n" +
                                "    AND ie.l_ultimo = 'S'\n" +
                                "    AND i.l_ind_baja = 'N'\n" +
                                "    AND i.c_instancia in('301','302','701','702','044','118','024', '025')--Alcance 02 juzgados de familia y 03 juzgados de paz letrado de Huaraz y civil transitorio huaraz \n" +
                                "    AND e.x_formato = :numeroExpediente"
                ).setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        for (Object[] row : resultList) {
            CabExpedienteChatDTO expediente = new CabExpedienteChatDTO(
                    row[0] != null ? ((BigDecimal) row[0]).longValue() : null,
                    row[1] != null ? String.valueOf(row[1]) : null,
                    row[2] != null ? ((Integer) row[2]).longValue() : null,
                    row[3] != null ? String.valueOf(row[3]) : null,
                    row[4] != null ? String.valueOf(row[4]) : null,
                    row[5] != null ? String.valueOf(row[5]) : null,
                    row[6] != null ? String.valueOf(row[6]) : null,
                    row[7] != null ? String.valueOf(row[7]) : null,
                    row[8] != null ? String.valueOf(row[8]) : null,
                    row[9] != null ? String.valueOf(row[9]) : null,
                    row[10] != null ? String.valueOf(row[10]) : null
            );
            listDataExpediente.add(expediente);
        }

        return listDataExpediente;
    }

    @Override
    public List<DataTipoParteDTO> findPartesByNUnico(Long nUnico) {

        List<DataTipoParteDTO> lista = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT " +
                                "p.c_tipo_persona, " +
                                "tp.x_desc_tipo_persona, " +
                                "p.l_tipo_parte, " +
                                "pt.x_desc_parte, " +
                                "p.x_ape_paterno, " +
                                "p.x_ape_materno, " +
                                "p.x_nombres, " +
                                "p.x_doc_id, " +
                                "tdi.c_tipo, " +
                                "tdi.x_tipo_doc, " +
                                "tdi.x_abrevi, " +
                                "p.l_activo, " +
                                "p.n_unico " +
                                "FROM parte p " +
                                "JOIN tipo_persona tp ON tp.c_tipo_persona = p.c_tipo_persona " +
                                "JOIN tipo_parte pt ON pt.l_tipo_parte = p.l_tipo_parte " +
                                "JOIN tipo_documento_identidad tdi ON tdi.c_tipo = p.c_tipo_doc " +
                                "WHERE p.l_activo = 'S' " +
                                "AND pt.l_activo = 'S' " +
                                "AND pt.c_especialidad IN (SELECT c_especialidad FROM expediente WHERE n_unico = :nUnico) " +
                                "AND p.n_unico = :nUnico"
                )
                .setParameter("nUnico", nUnico)
                .getResultList();

        for (Object[] row : resultList) {
            DataTipoParteDTO dto = new DataTipoParteDTO(
                    row[0] != null ? row[0].toString() : null,
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? row[2].toString() : null,
                    row[3] != null ? row[3].toString() : null,
                    row[4] != null ? row[4].toString() : null,
                    row[5] != null ? row[5].toString() : null,
                    row[6] != null ? row[6].toString() : null,
                    row[7] != null ? row[7].toString() : null,
                    row[8] != null ? row[8].toString() : null,
                    row[9] != null ? row[9].toString() : null,
                    row[10] != null ? row[10].toString() : null,
                    row[11] != null ? row[11].toString() : null,
                    row[12] != null ? ((java.math.BigDecimal) row[12]).longValue() : null
            );

            lista.add(dto);
        }

        return lista;
    }

    @Override
    public List<ResumenExpedienteParteDTO> getResumenExpedienteYPartes(Long nUnico) {

        List<Object[]> cabeceras = entityManager.createNativeQuery(
                        SELECT_CABECERA_RESUMEN +
                                "WHERE e.c_instancia in('301','302','701','702','044','118','024','025') " +
                                "AND e.n_unico = :nUnico"
                )
                .setParameter("nUnico", nUnico)
                .getResultList();

        List<Object[]> partes = entityManager.createNativeQuery(
                        SELECT_PARTES_RESUMEN +
                                "WHERE e.n_unico = :nUnico " +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("nUnico", nUnico)
                .getResultList();

        return combinarResumenYPartes(cabeceras, partes);
    }

    @Override
    public List<ResumenExpedienteParteDTO> getResumenExpedienteYPartesPorNumero(String numeroExpediente) {

        // Variante para el chatbot: filtra por x_formato en vez de n_unico. El número que
        // escribe el ciudadano identifica un incidente concreto (posición 12 del formato),
        // mientras que n_unico agrupa TODOS los incidentes del expediente y mezclaría el
        // detalle de unos con otros.
        //
        // El filtro por instancia repite el que ya aplica findByNumeroExpediente, que en el
        // flujo actual se ejecuta antes. Se duplica a propósito: esta información se publica
        // a la ciudadanía por Telegram/WhatsApp, y si alguien reutilizara este método sin
        // pasar por esa validación se expondrían expedientes fuera del alcance autorizado
        // (las 2 salas de familia, 3 juzgados de paz letrado y el civil transitorio).
        // Se usa ie.c_instancia y no e.c_instancia porque no siempre coinciden.
        List<Object[]> cabeceras = entityManager.createNativeQuery(
                        SELECT_CABECERA_RESUMEN +
                                "WHERE e.x_formato = :numeroExpediente " +
                                "AND ie.c_instancia in('301','302','701','702','044','118','024','025')"
                )
                .setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        // Sin cabecera no hay nada que combinar: evita una segunda ida a la base.
        if (cabeceras.isEmpty()) {
            return new ArrayList<>();
        }

        List<Object[]> partes = entityManager.createNativeQuery(
                        SELECT_PARTES_RESUMEN +
                                "WHERE e.x_formato = :numeroExpediente " +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        return combinarResumenYPartes(cabeceras, partes);
    }

    /**
     * Cabecera del expediente: materia, estado, ubicación, juez, secretario, sede y
     * especialidad. Devuelve una fila por expediente (o una por incidente, si se filtra
     * por n_unico en vez de por x_formato).
     *
     * Los datos 1:1 se resuelven con subconsultas escalares en lugar de JOINs. La versión
     * anterior encadenaba 17 tablas en un solo SELECT DISTINCT y el optimizador no daba
     * con un buen plan: 5 700 ms para devolver 4 filas. Con subconsultas, cada dato es una
     * búsqueda directa por (n_unico, n_incidente) y la misma consulta baja a 8 ms.
     *
     * Además corrige dos defectos de aquella versión:
     *
     * 1) Los accesos a asignado_a, expediente_estado, expediente_ubicacion y
     *    materia_expediente correlacionan por n_incidente. Antes unían solo por n_unico y,
     *    como un expediente tiene una fila por incidente, el producto intermedio se
     *    disparaba: en 00189-2022-0-0201-JR-FP-01 (5 incidentes, 11 partes, 50 filas en
     *    instancia_expediente) la consulta llegó a tardar 974 segundos.
     *
     * 2) El juez titular ya no es obligatorio. Con el INNER JOIN anterior, un juzgado sin
     *    titular registrado hacía que la consulta devolviera CERO filas y el bot
     *    respondiera "No disponible" en todas las opciones. Es el caso real de la instancia
     *    701 (1° Juzgado de Paz Letrado), dentro del alcance del bot, con 33 142
     *    expedientes afectados. Ahora el juez sale vacío y el resto de la información llega.
     *
     * MAX() en las subconsultas es un desempate determinista: estas tablas deberían tener
     * una sola fila vigente por expediente, pero no hay restricción que lo garantice y sin
     * el agregado una fila duplicada haría fallar la consulta entera.
     */
    private static final String SELECT_CABECERA_RESUMEN =
            "SELECT " +
                    "e.X_FORMATO, " +                                                     // 0
                    "i.x_nom_instancia, " +                                               // 1
                    "e.c_especialidad, " +                                                // 2
                    "(SELECT MAX(ma.X_DESC_MATERIA) FROM materia_expediente m " +
                    "   INNER JOIN materia ma ON ma.c_materia = m.c_materia " +
                    "   WHERE m.n_unico = e.n_unico AND m.n_incidente = e.n_incidente) AS x_desc_materia, " + // 3
                    "e.F_INICIO, " +                                                      // 4
                    "(SELECT MAX(em.X_DESC_ESTADO) FROM expediente_estado ee " +
                    "   INNER JOIN estado_maestro em ON em.c_estado = ee.c_estado " +
                    "   WHERE ee.n_unico = e.n_unico AND ee.n_incidente = e.n_incidente AND ee.l_ultimo = 'S') AS x_desc_estado, " + // 5
                    "(SELECT MAX(eu.c_ubicacion) FROM expediente_ubicacion eu " +
                    "   WHERE eu.n_unico = e.n_unico AND eu.n_incidente = e.n_incidente AND eu.l_ultimo = 'S') AS c_ubicacion, " + // 6
                    "(SELECT MAX(ue.x_desc_ubicacion) FROM expediente_ubicacion eu " +
                    "   INNER JOIN ubicacion_expediente ue ON ue.c_ubicacion = eu.c_ubicacion " +
                    "   WHERE eu.n_unico = e.n_unico AND eu.n_incidente = e.n_incidente AND eu.l_ultimo = 'S') AS x_desc_ubicacion, " + // 7
                    "(SELECT MAX(ui.c_usuario) FROM usuario_instancia ui " +
                    "   WHERE ui.c_instancia = ie.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S') AS usuario_juez, " + // 8
                    "(SELECT MAX(u.x_nom_usuario) FROM usuario_instancia ui " +
                    "   INNER JOIN usuario u ON u.c_usuario = ui.c_usuario " +
                    "   WHERE ui.c_instancia = ie.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S') AS juez, " + // 9
                    "(SELECT MAX(a.c_usuario) FROM asignado_a a " +
                    "   WHERE a.n_unico = e.n_unico AND a.n_incidente = e.n_incidente AND a.l_ultimo = 'S') AS usuario_secretario, " + // 10
                    "(SELECT MAX(us.x_nom_usuario) FROM asignado_a a " +
                    "   INNER JOIN usuario us ON us.c_usuario = a.c_usuario " +
                    "   WHERE a.n_unico = e.n_unico AND a.n_incidente = e.n_incidente AND a.l_ultimo = 'S') AS secretario, " + // 11
                    "CASE " +
                    "WHEN ie.l_ind_digital = 'N' THEN 'Físico' " +
                    "WHEN ie.l_ind_digital = 'S' THEN 'Electrónico' " +
                    "ELSE 'Desconocido' " +
                    "END AS tipo_expediente, " +                                          // 12
                    // Descripciones de sede y especialidad (antes solo se enviaba el código)
                    // y número de incidente, para la opción "Información General" del bot.
                    "(SELECT MAX(se.x_desc_sede) FROM sede se WHERE se.c_sede = e.c_sede) AS x_desc_sede, " + // 13
                    "(SELECT MAX(esp.x_desc_especialidad) FROM especialidad esp " +
                    "   WHERE esp.c_especialidad = e.c_especialidad) AS x_desc_especialidad, " + // 14
                    "SUBSTRING(e.X_FORMATO, 12, 1) AS n_incidente " +                     // 15
                    "FROM expediente e " +
                    "INNER JOIN instancia_expediente ie ON ie.n_unico = e.n_unico AND ie.n_incidente = e.n_incidente AND ie.l_ultimo = 'S' " +
                    "INNER JOIN instancia i ON ie.c_instancia = i.c_instancia ";

    /**
     * Partes procesales del expediente.
     *
     * NO se correlaciona por n_incidente a propósito, para que la lista coincida con la que
     * arma findPartesByNUnico en el paso de selección de parte del bot. Si se filtrara aquí,
     * el ciudadano vería en "Información General" una lista distinta de la que acaba de elegir.
     */
    private static final String SELECT_PARTES_RESUMEN =
            "SELECT DISTINCT " +
                    "ISNULL(p.x_ape_paterno, '') + ' ' + ISNULL(p.x_ape_materno, '') + ' ' + ISNULL(p.x_nombres, '') AS parte, " + // 0
                    "tp.l_tipo_parte, " +                                                 // 1
                    "tp.x_desc_parte " +                                                  // 2
                    "FROM expediente e " +
                    "INNER JOIN parte p ON p.n_unico = e.n_unico AND p.l_activo = 'S' " +
                    "INNER JOIN tipo_parte tp ON tp.l_tipo_parte = p.l_tipo_parte AND tp.l_activo = 'S' ";

    /**
     * Arma un DTO por cada combinación de cabecera y parte, que es la forma que espera
     * el resto del sistema (una fila por parte, con la cabecera repetida).
     *
     * Si el expediente no tiene partes activas se devuelve igualmente la cabecera con los
     * campos de parte vacíos, para que estado y ubicación sigan disponibles. En el flujo del
     * bot ese caso no se alcanza: el paso 1 corta antes con "no se hallaron partes procesales".
     */
    private List<ResumenExpedienteParteDTO> combinarResumenYPartes(List<Object[]> cabeceras, List<Object[]> partes) {

        List<ResumenExpedienteParteDTO> lista = new ArrayList<>();

        for (Object[] cab : cabeceras) {
            if (partes.isEmpty()) {
                lista.add(construirResumen(cab, null));
                continue;
            }
            for (Object[] parte : partes) {
                lista.add(construirResumen(cab, parte));
            }
        }

        return lista;
    }

    private ResumenExpedienteParteDTO construirResumen(Object[] cab, Object[] parte) {

        ResumenExpedienteParteDTO dto = new ResumenExpedienteParteDTO();
        dto.setNumeroExpediente(texto(cab[0]));
        dto.setInstancia(texto(cab[1]));
        dto.setCodigoEspecialidad(texto(cab[2]));
        dto.setMateria(texto(cab[3]));
        dto.setFechaInicio(cab[4] != null ? ((Timestamp) cab[4]).toLocalDateTime() : null);
        dto.setEstadoExpediente(texto(cab[5]));
        dto.setCodigoUbicacion(texto(cab[6]));
        dto.setDescripcionUbicacion(texto(cab[7]));
        dto.setUsuarioJuez(texto(cab[8]));
        dto.setNombreJuez(texto(cab[9]));
        dto.setUsuarioSecretario(texto(cab[10]));
        dto.setNombreSecretario(texto(cab[11]));
        dto.setTipoExpediente(texto(cab[12]));
        dto.setDescSede(texto(cab[13]));
        dto.setDescEspecialidad(texto(cab[14]));
        dto.setNIncidente(texto(cab[15]));

        if (parte != null) {
            dto.setParteNombreCompleto(texto(parte[0]));
            dto.setTipoParte(texto(parte[1]));
            dto.setDescTipoParte(texto(parte[2]));
        }

        return dto;
    }

    private String texto(Object valor) {
        return valor != null ? valor.toString().trim() : null;
    }

    // ------------------------------------------------------------------
    // Consultas del chatbot: escritos y audiencias
    //
    // Todas filtran por x_formato y no por n_unico: el ciudadano escribe el número
    // completo del expediente, que además fija el incidente (posición 12 del formato).
    //
    // El tope de filas se aplica con TOP dentro del SQL y no con setMaxResults, porque
    // sobre consultas nativas el dialecto de Sybase no genera una cláusula de límite
    // fiable. LIMITE_REGISTROS_CHATBOT es una constante entera, no entra por parámetro.
    // ------------------------------------------------------------------

    /** Máximo de escritos/audiencias que se envían al chatbot por consulta. */
    private static final int LIMITE_REGISTROS_CHATBOT = 50;

    /**
     * Bloque común a las tres consultas: instancia del expediente y nombre completo del
     * especialista asignado. El LEFT JOIN a usuario evita perder filas si el usuario
     * asignado ya no existe en el maestro; en ese caso se cae al código de usuario.
     */
    private static final String JOINS_CABECERA_CHATBOT =
            "FROM expediente e " +
            "INNER JOIN instancia_expediente iexp ON e.n_unico = iexp.n_unico AND e.n_incidente = iexp.n_incidente AND iexp.l_ultimo = 'S' " +
            "INNER JOIN instancia i ON iexp.c_instancia = i.c_instancia " +
            "INNER JOIN asignado_a a ON e.n_unico = a.n_unico AND e.n_incidente = a.n_incidente AND a.l_ultimo = 'S' " +
            "LEFT JOIN usuario us ON us.c_usuario = a.c_usuario ";

    /**
     * Restringe las consultas del chatbot a las instancias dentro del alcance del servicio:
     * los 2 juzgados de familia, los 3 juzgados de paz letrado y el civil transitorio.
     *
     * Repite el filtro que ya aplica findByNumeroExpediente antes de llegar aquí. Se duplica
     * a propósito: estos datos se publican a la ciudadanía por Telegram y WhatsApp, y si
     * alguien reutilizara estos métodos sin pasar por esa validación previa se expondrían
     * expedientes fuera del alcance autorizado.
     */
    private static final String FILTRO_INSTANCIAS_CHATBOT =
            "AND iexp.c_instancia in('301','302','701','702','044','118','024','025') ";

    @Override
    public List<EscritoExpedienteDTO> findEscritosByExpediente(String numeroExpediente) {

        List<EscritoExpedienteDTO> lista = new ArrayList<>();

        // El nombre del archivo de la resolución se obtiene con una subconsulta y no con un
        // LEFT JOIN a documento_digital: la condición 'RESOLUCION '+x_resolucion = x_descripcion
        // no es 1:1 y llega a multiplicar un mismo escrito hasta 13 veces.
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT TOP " + LIMITE_REGISTROS_CHATBOT + " \n" +
                                "    e.n_unico, \n" +                                        // 0
                                "    e.x_formato, \n" +                                      // 1
                                "    SUBSTRING(e.x_formato, 12, 1) AS n_incidente, \n" +     // 2
                                "    i.x_nom_instancia, \n" +                                // 3
                                "    ISNULL(us.x_nom_usuario, a.c_usuario) AS especialista, \n" + // 4
                                "    CONVERT(VARCHAR(10), esc.n_sec_ingreso) + '-' + CONVERT(VARCHAR(4), esc.n_ano_ingreso) AS nro_escrito, \n" + // 5
                                "    esc.f_ingreso_acto, \n" +                               // 6
                                "    esc.f_respuesta, \n" +                                  // 7
                                "    esc.x_resolucion, \n" +                                 // 8
                                // x_sumilla es varchar(4000). Se recorta en el propio SQL porque
                                // el payload viaja por el proxy institucional en cada consulta y
                                // el bot solo muestra una línea. Promedio real 57 caracteres,
                                // máximo observado 1861: con 300 no se pierde nada en la práctica.
                                "    LEFT(esc.x_sumilla, 300) AS x_sumilla, \n" +            // 9
                                "    (SELECT MAX(dd.x_nombre_archivo) FROM documento_digital dd \n" +
                                "      WHERE dd.n_unico = e.n_unico AND dd.n_incidente = e.n_incidente \n" +
                                "        AND dd.x_descripcion = 'RESOLUCION ' + esc.x_resolucion) AS nombre_archivo \n" + // 10
                                JOINS_CABECERA_CHATBOT +
                                "INNER JOIN escrito esc ON e.n_unico = esc.n_unico AND e.n_incidente = esc.n_incidente " +
                                "WHERE e.x_formato = :numeroExpediente " +
                                FILTRO_INSTANCIAS_CHATBOT +
                                "ORDER BY esc.f_ingreso_acto DESC, esc.n_sec_ingreso DESC"
                )
                .setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        for (Object[] row : resultList) {
            EscritoExpedienteDTO dto = new EscritoExpedienteDTO();
            dto.setNUnico(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setNumeroExpediente(row[1] != null ? row[1].toString().trim() : null);
            dto.setNIncidente(row[2] != null ? row[2].toString().trim() : null);
            dto.setInstancia(row[3] != null ? row[3].toString().trim() : null);
            dto.setEspecialista(row[4] != null ? row[4].toString().trim() : null);
            dto.setNroEscrito(row[5] != null ? row[5].toString().trim() : null);
            dto.setFechaEscrito(row[6] != null ? ((Timestamp) row[6]).toLocalDateTime() : null);
            dto.setFechaAtencion(row[7] != null ? ((Timestamp) row[7]).toLocalDateTime() : null);
            // x_resolucion nulo = escrito aún no proveído. Se deja en null y es el bot
            // quien decide cómo rotularlo, en vez de fijar aquí el texto "PENDIENTE".
            dto.setResolucion(row[8] != null ? row[8].toString().trim() : null);
            dto.setSumilla(row[9] != null ? row[9].toString().trim() : null);
            dto.setNombreArchivo(row[10] != null ? row[10].toString().trim() : null);
            lista.add(dto);
        }

        return lista;
    }

    @Override
    public List<AudienciaExpedienteDTO> findAudienciasRealizadas(String numeroExpediente) {

        List<AudienciaExpedienteDTO> lista = new ArrayList<>();

        // Se filtra por ap.l_estado = 'REAL' en lugar de exigir INNER JOIN con audiencia_video:
        // ese join descarta las audiencias realizadas que no tienen grabación registrada.
        // Los datos de acta, audio y enlace se resuelven con subconsultas (audiencia y
        // audiencia_video admiten más de una fila por programación).
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT TOP " + LIMITE_REGISTROS_CHATBOT + " \n" +
                                "    e.n_unico, \n" +                                        // 0
                                "    e.x_formato, \n" +                                      // 1
                                "    SUBSTRING(e.x_formato, 12, 1) AS n_incidente, \n" +     // 2
                                "    i.x_nom_instancia, \n" +                                // 3
                                "    ISNULL(us.x_nom_usuario, a.c_usuario) AS especialista, \n" + // 4
                                "    ap.n_programacion, \n" +                                // 5
                                "    ap.n_sala, \n" +                                        // 6
                                "    ap.l_estado, \n" +                                      // 7
                                "    ap.x_desc_audiencia, \n" +                              // 8
                                "    ISNULL((SELECT MIN(aud.f_creacion) FROM audiencia aud \n" +
                                "             WHERE aud.n_sala = ap.n_sala AND aud.n_programacion = ap.n_programacion), \n" +
                                "           ap.f_ini_prog) AS f_audiencia, \n" +             // 9
                                "    (SELECT MIN(aud.x_file_acta) FROM audiencia aud \n" +
                                "      WHERE aud.n_sala = ap.n_sala AND aud.n_programacion = ap.n_programacion) AS archivo_acta, \n" + // 10
                                "    (SELECT MIN(av.x_filename) FROM audiencia_video av \n" +
                                "      WHERE av.n_sala = ap.n_sala AND av.n_programacion = ap.n_programacion) AS archivo_audio, \n" + // 11
                                "    (SELECT MIN(av.l_ruta_medio) FROM audiencia_video av \n" +
                                "      WHERE av.n_sala = ap.n_sala AND av.n_programacion = ap.n_programacion AND av.l_url = 'S') AS enlace \n" + // 12
                                JOINS_CABECERA_CHATBOT +
                                "INNER JOIN audiencia_programacion ap ON e.n_unico = ap.n_unico AND e.n_incidente = ap.n_incidente " +
                                "WHERE e.x_formato = :numeroExpediente " +
                                FILTRO_INSTANCIAS_CHATBOT +
                                "  AND ap.l_estado = 'REAL' " +
                                "ORDER BY 10 DESC"
                )
                .setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        for (Object[] row : resultList) {
            lista.add(mapAudiencia(row, "REAL", true));
        }

        return lista;
    }

    @Override
    public List<AudienciaExpedienteDTO> findAudienciasProximas(String numeroExpediente) {

        List<AudienciaExpedienteDTO> lista = new ArrayList<>();

        // l_estado = 'PROG' por sí solo NO significa "próxima": el SIJ deja en PROG las
        // programaciones que nunca se llegaron a realizar, con fechas ya vencidas. Sin el
        // filtro por fecha, al ciudadano se le mostrarían audiencias pasadas como futuras.
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT TOP " + LIMITE_REGISTROS_CHATBOT + " \n" +
                                "    e.n_unico, \n" +                                        // 0
                                "    e.x_formato, \n" +                                      // 1
                                "    SUBSTRING(e.x_formato, 12, 1) AS n_incidente, \n" +     // 2
                                "    i.x_nom_instancia, \n" +                                // 3
                                "    ISNULL(us.x_nom_usuario, a.c_usuario) AS especialista, \n" + // 4
                                "    ap.n_programacion, \n" +                                // 5
                                "    ap.n_sala, \n" +                                        // 6
                                "    ap.l_estado, \n" +                                      // 7
                                "    ap.x_desc_audiencia, \n" +                              // 8
                                "    ap.f_ini_prog AS f_audiencia \n" +                      // 9
                                JOINS_CABECERA_CHATBOT +
                                "INNER JOIN audiencia_programacion ap ON e.n_unico = ap.n_unico AND e.n_incidente = ap.n_incidente " +
                                "WHERE e.x_formato = :numeroExpediente " +
                                FILTRO_INSTANCIAS_CHATBOT +
                                "  AND ap.l_estado = 'PROG' " +
                                "  AND ap.f_ini_prog >= CONVERT(DATE, GETDATE()) " +
                                "ORDER BY ap.f_ini_prog ASC"
                )
                .setParameter("numeroExpediente", numeroExpediente)
                .getResultList();

        for (Object[] row : resultList) {
            lista.add(mapAudiencia(row, "PROG", false));
        }

        return lista;
    }

    /**
     * Mapea una fila de audiencia. Las columnas 0..9 son idénticas en ambas consultas;
     * solo las realizadas traen además acta, audio y enlace (columnas 10..12).
     */
    private AudienciaExpedienteDTO mapAudiencia(Object[] row, String tipoAudiencia, boolean conGrabacion) {
        AudienciaExpedienteDTO dto = new AudienciaExpedienteDTO();
        dto.setNUnico(row[0] != null ? ((Number) row[0]).longValue() : null);
        dto.setNumeroExpediente(row[1] != null ? row[1].toString().trim() : null);
        dto.setNIncidente(row[2] != null ? row[2].toString().trim() : null);
        dto.setInstancia(row[3] != null ? row[3].toString().trim() : null);
        dto.setEspecialista(row[4] != null ? row[4].toString().trim() : null);
        dto.setTipoAudiencia(tipoAudiencia);
        dto.setNProgramacion(row[5] != null ? ((Number) row[5]).intValue() : null);
        dto.setNSala(row[6] != null ? ((Number) row[6]).intValue() : null);
        dto.setEstado(row[7] != null ? row[7].toString().trim() : null);
        dto.setDescripcionAudiencia(row[8] != null ? row[8].toString().trim() : null);
        dto.setFechaAudiencia(row[9] != null ? ((Timestamp) row[9]).toLocalDateTime() : null);

        if (conGrabacion) {
            dto.setArchivoActa(row[10] != null ? row[10].toString().trim() : null);
            dto.setArchivoAudio(row[11] != null ? row[11].toString().trim() : null);
            dto.setEnlace(row[12] != null ? row[12].toString().trim() : null);
        }

        return dto;
    }

    private void completarPartes(DataCabExpedienteCalificarDTO dto, Long nUnico) {
        try {
            List<DataTipoParteDTO> partes = findPartesByNUnico(nUnico);
            if (partes == null || partes.isEmpty()) {
                return;
            }

            String demandados = partes.stream()
                    .filter(p -> esTipoParte(p.getDescTipoParte(), "DEMANDADO"))
                    .map(this::nombreCompleto)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)))
                    .stream()
                    .collect(Collectors.joining(", "));

            String demandantes = partes.stream()
                    .filter(p -> esTipoParte(p.getDescTipoParte(), "DEMANDANTE"))
                    .map(this::nombreCompleto)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)))
                    .stream()
                    .collect(Collectors.joining(", "));

            dto.setXDescDemandado(demandados.isEmpty() ? null : demandados);
            dto.setXDescDemandante(demandantes.isEmpty() ? null : demandantes);
        } catch (Exception ex) {
            logger.warn("No se pudieron cargar las partes del expediente {}: {}", nUnico, ex.getMessage());
        }
    }

    private boolean esTipoParte(String descTipoParte, String tipo) {
        if (descTipoParte == null) {
            return false;
        }
        return descTipoParte.trim().toUpperCase().contains(tipo);
    }

    private String nombreCompleto(DataTipoParteDTO p) {
        StringBuilder sb = new StringBuilder();
        appendSiNoVacio(sb, p.getApePaterno());
        appendSiNoVacio(sb, p.getApeMaterno());
        appendSiNoVacio(sb, p.getNombres());
        return sb.toString().trim();
    }

    private void appendSiNoVacio(StringBuilder sb, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(valor.trim());
        }
    }

}
