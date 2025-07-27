package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.ExpedienteDAO;
import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

@Service
public class ExpedienteServiceImpl implements ExpedienteService {

    @Autowired
    private ExpedienteDAO expedienteDAO;

    @Override
    public List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception {
        return expedienteDAO.findCabExpedientes(input);
    }

    @Override
    public List<DataExpedienteDTO> getDataExpediente(Long nUnico) throws Exception {
        return expedienteDAO.getDataExpediente(nUnico);
    }

    @Override
    public List<CabExpedienteChatDTO> getDataExpedientePorNumero(String numeroExpediente) {
        return expedienteDAO.findByNumeroExpediente(numeroExpediente);
    }

    @Override
    public List<DataTipoParteDTO> getPartesByNUnico(Long nUnico) {
        return expedienteDAO.findPartesByNUnico(nUnico);
    }

    @Override
    public List<ResumenExpedienteParteDTO> getResumenExpedienteYPartes(Long nUnico) {
        return expedienteDAO.getResumenExpedienteYPartes(nUnico);
    }

}
