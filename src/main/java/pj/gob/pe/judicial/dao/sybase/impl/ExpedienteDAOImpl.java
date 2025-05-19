package pj.gob.pe.judicial.dao.sybase.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pj.gob.pe.judicial.dao.sybase.ExpedienteDAO;
import pj.gob.pe.judicial.model.sybase.dto.DataCabExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
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
                        ((BigDecimal) row[0]).longValue(),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3]),
                        ((Integer) row[4]).longValue(),
                        ((Integer) row[5]).longValue(),
                        String.valueOf(row[6]),
                        String.valueOf(row[7]),
                        String.valueOf(row[8]),
                        String.valueOf(row[9]),
                        String.valueOf(row[10]),
                        String.valueOf(row[11]),
                        String.valueOf(row[12]),
                        row[13] != null ? ((Timestamp) row[13]).toLocalDateTime() : null,
                        String.valueOf(row[14]),
                        String.valueOf(row[15]),
                        String.valueOf(row[16]),
                        String.valueOf(row[17])
                );

                listCabExpedientes.add(expediente);
            });
        }

        return listCabExpedientes;
    }
}
