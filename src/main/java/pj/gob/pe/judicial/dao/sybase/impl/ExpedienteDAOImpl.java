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
                                "    ie.c_instancia in('301','302','701','702','044','118') AND \n" +
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
    public List<DataExpedienteDTO> getDataExpediente(Long nUnico, String numIncidente) throws Exception {

        List<DataExpedienteDTO> listDataExpediente = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        " SELECT DISTINCT \n" +
                                "    e.n_unico as N_UNICO, \n" +
                                "    e.X_FORMATO, \n" +
                                "    i.x_nom_instancia, \n" +
                                "    e.c_especialidad, \n" +
                                "    ma.X_DESC_MATERIA, \n" +
                                "    e.F_INICIO, \n" +
                                "    em.X_DESC_ESTADO, \n" +
                                "    eu.c_ubicacion, \n" +
                                "    ue.x_desc_ubicacion, \n" +
                                "    ui.c_usuario AS usuario_juez, \n" +
                                "    u.x_nom_usuario AS juez, \n" +
                                "    a.c_usuario AS usuario_secretario, \n" +
                                "    us.x_nom_usuario AS secretario, \n" +
                                "    CASE \n" +
                                "        WHEN ie.l_ind_digital = 'N' THEN 'Físico' \n" +
                                "        WHEN ie.l_ind_digital = 'S' THEN 'Electrónico' \n" +
                                "    END AS tipo_expediente, \n" +
                                "    ISNULL(p.x_ape_paterno, '') + ' ' + ISNULL(p.x_ape_materno, '') + ' ' + ISNULL(p.x_nombres, '') AS parte, \n" +
                                "    tp.l_tipo_parte, \n" +
                                "    tp.x_desc_parte, \n" +
                                "    p.x_doc_id as DNI_PARTE, \n" +
                                "    e.c_sede, \n" +
                                "    e.c_instancia, \n" +
                                "    SUBSTRING(e.n_unico, 1, 4) as c_year, \n" +
                                "    SUBSTRING(e.n_unico, 5, 5) as c_num, \n" +
                                "    m.c_materia, \n" +
                                "    se.x_desc_sede, \n" +
                                "    esp.x_desc_especialidad, \n" +
                                "    SUBSTRING(e.X_FORMATO,12,1) as n_incidente \n" +
                                "FROM expediente e \n" +
                                "INNER JOIN parte p \n" +
                                "        ON p.n_unico = e.n_unico \n" +
                                "       AND p.l_activo = 'S' \n" +
                                "INNER JOIN tipo_parte tp \n" +
                                "        ON tp.l_tipo_parte = p.l_tipo_parte \n" +
                                "       AND tp.l_activo = 'S' \n" +
                                "INNER JOIN asignado_a a \n" +
                                "        ON a.n_unico = e.n_unico \n" +
                                "       AND a.l_ultimo = 'S' \n" +
                                "INNER JOIN usuario us \n" +
                                "        ON us.c_usuario = a.c_usuario \n" +
                                "INNER JOIN expediente_estado ee \n" +
                                "        ON ee.n_unico     = e.n_unico \n" +
                                "       AND ee.n_incidente = e.n_incidente \n" +
                                "       AND ee.l_ultimo    = 'S' \n" +
                                "INNER JOIN estado_maestro em \n" +
                                "        ON em.c_estado = ee.c_estado \n" +
                                "INNER JOIN materia_expediente m \n" +
                                "        ON m.n_unico = e.n_unico \n" +
                                "INNER JOIN materia ma \n" +
                                "        ON m.c_materia = ma.c_materia \n" +
                                "INNER JOIN expediente_ubicacion eu \n" +
                                "        ON eu.n_unico     = e.n_unico \n" +
                                "       AND eu.n_incidente = e.n_incidente \n" +
                                "       AND eu.l_ultimo    = 'S' \n" +
                                "INNER JOIN ubicacion_expediente ue \n" +
                                "        ON ue.c_ubicacion = eu.c_ubicacion \n" +
                                "INNER JOIN instancia_expediente ie \n" +
                                "        ON ie.n_unico     = e.n_unico \n" +
                                "       AND ie.n_incidente = e.n_incidente \n" +
                                "       AND ie.l_ultimo    = 'S' \n" +
                                "INNER JOIN instancia i \n" +
                                "        ON ie.c_instancia = i.c_instancia \n" +
                                "INNER JOIN usuario_instancia ui \n" +
                                "        ON ui.c_instancia = ie.c_instancia \n" +
                                "       AND ui.l_activo   = 'S' \n" +
                                "       AND ui.l_titular  = 'S' \n" +
                                "INNER JOIN usuario u \n" +
                                "        ON u.c_usuario = ui.c_usuario \n" +
                                "INNER JOIN sede se \n" +
                                "        ON se.c_sede = e.c_sede \n" +
                                "INNER JOIN especialidad esp \n" +
                                "        ON esp.c_especialidad = e.c_especialidad \n" +
                                "WHERE \n" +
                                "    e.n_unico = :numUnico \n" +
                                "    AND e.n_incidente = :numIncidente1 and p.n_incidente = :numIncidente2 and p.l_activo='S' \n" +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("numUnico", nUnico)
                .setParameter("numIncidente1", numIncidente)
                .setParameter("numIncidente2", numIncidente)
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
                                "    AND i.c_instancia in('301','302','701','702','044','118')--Alcance 02 juzgados de familia y 03 juzgados de paz letrado de Huaraz y civil transitorio huaraz \n" +
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

        List<ResumenExpedienteParteDTO> lista = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT " +
                                "e.X_FORMATO, " +
                                "i.x_nom_instancia, " +
                                "e.c_especialidad, " +
                                "ma.X_DESC_MATERIA, " +
                                "e.F_INICIO, " +
                                "em.X_DESC_ESTADO, " +
                                "eu.c_ubicacion, " +
                                "ue.x_desc_ubicacion, " +
                                "ui.c_usuario AS usuario_juez, " +
                                "u.x_nom_usuario AS juez, " +
                                "a.c_usuario AS usuario_secretario, " +
                                "us.x_nom_usuario AS secretario, " +
                                "CASE " +
                                "WHEN ie.l_ind_digital = 'N' THEN 'Físico' " +
                                "WHEN ie.l_ind_digital = 'S' THEN 'Electrónico' " +
                                "ELSE 'Desconocido' " +
                                "END AS tipo_expediente, " +
                                "ISNULL(p.x_ape_paterno, '') + ' ' + ISNULL(p.x_ape_materno, '') + ' ' + ISNULL(p.x_nombres, '') AS parte, " +
                                "tp.l_tipo_parte, " +
                                "tp.x_desc_parte " +
                                "FROM expediente e " +
                                "INNER JOIN parte p ON p.n_unico = e.n_unico AND p.l_activo = 'S' " +
                                "INNER JOIN tipo_parte tp ON tp.l_tipo_parte = p.l_tipo_parte AND tp.l_activo = 'S' " +
                                "INNER JOIN asignado_a a ON a.n_unico = e.n_unico AND a.l_ultimo = 'S' " +
                                "INNER JOIN usuario us ON us.c_usuario = a.c_usuario " +
                                "INNER JOIN expediente_estado ee ON ee.n_unico = e.n_unico AND ee.l_ultimo = 'S' " +
                                "INNER JOIN estado_maestro em ON em.c_estado = ee.c_estado " +
                                "INNER JOIN materia_expediente m ON m.n_unico = e.n_unico " +
                                "INNER JOIN materia ma ON m.c_materia = ma.c_materia " +
                                "INNER JOIN expediente_ubicacion eu ON eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' " +
                                "INNER JOIN ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion " +
                                "INNER JOIN instancia_expediente ie ON ie.n_unico = e.n_unico " +
                                "INNER JOIN instancia i ON ie.c_instancia = i.c_instancia " +
                                "INNER JOIN usuario_instancia ui ON ui.c_instancia = ie.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S' " +
                                "INNER JOIN usuario u ON u.c_usuario = ui.c_usuario " +
                                "WHERE e.c_instancia in('301','302','701','702','044','118') " +
                                "AND e.n_unico = :nUnico " +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("nUnico", nUnico)
                .getResultList();

        for (Object[] row : resultList) {
            ResumenExpedienteParteDTO dto = new ResumenExpedienteParteDTO(
                    row[0] != null ? row[0].toString() : null,
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? row[2].toString() : null,
                    row[3] != null ? row[3].toString() : null,
                    row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null,
                    row[5] != null ? row[5].toString() : null,
                    row[6] != null ? row[6].toString() : null,
                    row[7] != null ? row[7].toString() : null,
                    row[8] != null ? row[8].toString() : null,
                    row[9] != null ? row[9].toString() : null,
                    row[10] != null ? row[10].toString() : null,
                    row[11] != null ? row[11].toString() : null,
                    row[12] != null ? row[12].toString() : null,
                    row[13] != null ? row[13].toString() : null,
                    row[14] != null ? row[14].toString() : null,
                    row[15] != null ? row[15].toString() : null
            );
            lista.add(dto);
        }

        return lista;
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
