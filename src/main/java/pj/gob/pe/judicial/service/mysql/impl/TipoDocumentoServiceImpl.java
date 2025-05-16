package pj.gob.pe.judicial.service.mysql.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.repository.mysql.TipoDocumentoRepository;
import pj.gob.pe.judicial.service.mysql.TipoDocumentoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository repo;

    @Override
    public List<TipoDocumento> listarAll() {
        return repo.findAll();
    }

    @Override
    public List<TipoDocumento> listarByInstancia(String idInstancia) {
        return repo.findByInstancia(idInstancia);
    }
}
