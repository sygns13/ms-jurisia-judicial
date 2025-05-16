package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.repository.mysql.DocumentoRepository;
import pj.gob.pe.judicial.service.mysql.DocumentoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Override
    public List<Documento> listarAll() {
        return documentoRepository.findAll();
    }
}
