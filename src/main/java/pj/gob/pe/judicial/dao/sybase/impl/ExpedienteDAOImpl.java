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
                        "select distinct exp.n_unico as N_UNICO,   " +
                                "  SUBSTRING(exp.n_unico, 1, 4) as ANIO,    " +
                                "  SUBSTRING(exp.n_unico, 5, 5) as EXPNRO,    " +
                                "  exp.x_formato AS NUM_EXP,   " +
                                "  exp.n_nro_exp_origen as N_NRO_EXP_ORIGEN,    " +
                                "  exp.n_ano_exp_origen as N_ANO_EXP_ORIGEN,    " +
                                "  me.c_materia as C_MATERIA,   " +
                                "  sed.x_desc_sede AS SEDE,   " +
                                "  org.x_nom_org_jurisd AS ORGANO,   " +
                                "  esp.x_desc_especialidad AS ESPECIALIDAD,   " +
                                "  inst.x_nom_instancia AS INSTANCIA   " +
                                "from expediente exp   " +
                                "inner join    " +
                                "\tinstancia inst on exp.c_instancia=inst.c_instancia   " +
                                "inner join    " +
                                "    sede sed on inst.c_sede=sed.c_sede    " +
                                "inner join    " +
                                "     organo_jurisdiccional org on inst.c_org_jurisd=org.c_org_jurisd        " +
                                "inner join    " +
                                "     especialidad esp on exp.c_especialidad=esp.c_especialidad   " +
                                "inner join        " +
                                "\texpediente_estado ee on exp.n_unico=ee.n_unico     " +
                                "inner join   " +
                                "\tmateria_expediente me on exp.n_unico=me.n_unico   " +
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
                        String.valueOf(row[10])
                );

                listCabExpedientes.add(expediente);
            });
        }

        return listCabExpedientes;
    }
}
