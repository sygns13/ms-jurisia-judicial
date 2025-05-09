package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.DataInstanciaDTO;
import pj.gob.pe.judicial.service.InstanciaService;

import java.util.List;

@Tag(name = "Service Instancia Controller", description = "Service Instancia Controller")
@RestController
@RequestMapping("/v1/instancias")
@RequiredArgsConstructor
public class InstanciaController {

    private final InstanciaService instanciaService;

    @Operation(summary = "Obtener instancias activas", description = "Obtener instancias activas")
    @GetMapping("/active")
    public ResponseEntity<List<DataInstanciaDTO>> listarInstancias() throws Exception{

        List<DataInstanciaDTO> instancias = instanciaService.findActiveInstancias();

        if(instancias == null) {
            throw new ModeloNotFoundException("Instancias no encontrada");
        }

        return new ResponseEntity<List<DataInstanciaDTO>>(instancias, HttpStatus.OK);
    }
}
