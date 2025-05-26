package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.ExpedienteDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataCabExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

        if (!resultList.isEmpty()) {
            resultList.forEach(row -> {
                DataCabExpedienteDTO expediente = new DataCabExpedienteDTO(
                        row[0] != null ? ((BigDecimal) row[0]).longValue() : null,
                        row[1] != null ? String.valueOf(row[1]) : null,
                        row[2] != null ? String.valueOf(row[2]) : null,
                        row[3] != null ? String.valueOf(row[3]) : null,
                        row[4] != null ? ((Integer) row[4]).longValue() : null,
                        row[5] != null ? ((Integer) row[5]).longValue() : null,
                        row[6] != null ? String.valueOf(row[6]) : null,
                        row[7] != null ? String.valueOf(row[7]) : null,
                        row[8] != null ? String.valueOf(row[8]) : null,
                        row[9] != null ? String.valueOf(row[9]) : null,
                        row[10] != null ? String.valueOf(row[10]) : null,
                        row[11] != null ? String.valueOf(row[11]) : null,
                        row[12] != null ? String.valueOf(row[12]) : null,
                        row[13] != null ? ((Timestamp) row[13]).toLocalDateTime() : null,
                        row[14] != null ? String.valueOf(row[14]) : null,
                        row[15] != null ? String.valueOf(row[15]) : null,
                        row[16] != null ? String.valueOf(row[16]) : null,
                        row[17] != null ? String.valueOf(row[17]) : null
                );

                listCabExpedientes.add(expediente);
            });
        }

        return listCabExpedientes;
    }

    @Override
    public List<DataExpedienteDTO> getDataExpediente(Long nUnico) throws Exception {

        List<DataExpedienteDTO> listDataExpediente = new ArrayList<>();

        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT DISTINCT \n" +
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
                                "    p.x_doc_id as DNI_PARTE \n" +
                                "FROM expediente e \n" +
                                "INNER JOIN parte p ON p.n_unico = e.n_unico AND p.l_activo = 'S' \n" +
                                "INNER JOIN tipo_parte tp ON tp.l_tipo_parte = p.l_tipo_parte AND tp.l_activo = 'S' \n" +
                                "INNER JOIN instancia i ON e.c_instancia = i.c_instancia \n" +
                                "INNER JOIN usuario_instancia ui ON ui.c_instancia = i.c_instancia AND ui.l_activo = 'S' AND ui.l_titular = 'S' \n" +
                                "INNER JOIN usuario u ON u.c_usuario = ui.c_usuario \n" +
                                "INNER JOIN asignado_a a ON a.n_unico = e.n_unico AND a.l_ultimo = 'S' \n" +
                                "INNER JOIN usuario us ON us.c_usuario = a.c_usuario \n" +
                                "INNER JOIN expediente_estado ee ON ee.n_unico = e.n_unico AND ee.l_ultimo = 'S' \n" +
                                "INNER JOIN estado_maestro em ON em.c_estado = ee.c_estado \n" +
                                "INNER JOIN materia_expediente m ON m.n_unico = e.n_unico \n" +
                                "INNER JOIN materia ma ON m.c_materia = ma.c_materia \n" +
                                "INNER JOIN expediente_ubicacion eu ON eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' \n" +
                                "INNER JOIN ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion \n" +
                                "INNER JOIN instancia_expediente ie ON ie.n_unico = e.n_unico \n" +
                                "WHERE \n" +
                                "    e.n_unico = :numUnico \n" +
                                "ORDER BY tp.l_tipo_parte"
                )
                .setParameter("numUnico", nUnico)
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
                        row[17] != null ? String.valueOf(row[17]) : null
                );

                listDataExpediente.add(expediente);
            });
        }

        return listDataExpediente;
    }
}
