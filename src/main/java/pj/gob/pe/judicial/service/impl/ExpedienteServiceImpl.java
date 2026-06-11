package pj.gob.pe.judicial.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.dao.sybase.ExpedienteDAO;
import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;
import pj.gob.pe.judicial.utils.beans.InputCabExpedienteCalifica;
import pj.gob.pe.judicial.utils.beans.ResponseLogin;
import pj.gob.pe.judicial.service.externals.SecurityService;
import pj.gob.pe.judicial.exception.ValidationSessionServiceException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.ByteArrayOutputStream;
import pj.gob.pe.judicial.utils.beans.InputObtenerPdf;

import java.util.List;

@Service
public class ExpedienteServiceImpl implements ExpedienteService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ExpedienteDAO expedienteDAO;


    @Override
    public List<DataCabExpedienteDTO> findCabExpedientes(InputCabExpediente input) throws Exception {
        return expedienteDAO.findCabExpedientes(input);
    }

    @Override
    public List<DataCabExpedienteCalificarDTO> findCabExpedientesCalificar(String SessionId, InputCabExpedienteCalifica input) throws Exception {
        String errorValidacion = "";
        if (SessionId == null || SessionId.isEmpty()) {
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }
        ResponseLogin responseLogin = securityService.GetSessionData(SessionId);
        if (responseLogin == null || !responseLogin.isSuccess() || !responseLogin.isItemFound() || responseLogin.getUser() == null) {
            errorValidacion = "La sessión remitida es inválida";
            throw new ValidationSessionServiceException(errorValidacion);
        }
        return expedienteDAO.findCabExpedientesCalificar(input, responseLogin.getUser());
    }

    @Override
    public List<DataExpedienteDTO> getDataExpediente(Long nUnico,String numIncidente) throws Exception {
        return expedienteDAO.getDataExpediente(nUnico, numIncidente);
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

    @Override
    public byte[] obtenerPdfDesdeFtp(InputObtenerPdf input) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(input.getXip(), 21);
            ftpClient.login(input.getCusuario(), input.getCclave());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remotePath = "/" + input.getXrutaArchivo() + "/" + input.getXnombreArchivo();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean success = ftpClient.retrieveFile(remotePath, outputStream);

            if (!success) {
                throw new RuntimeException("No se pudo recuperar el archivo: " + remotePath);
            }

            return outputStream.toByteArray();
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }

}
