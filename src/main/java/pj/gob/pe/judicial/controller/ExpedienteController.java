package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.DataCabExpedienteDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataExpedienteDTO;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;

import java.util.List;

@Tag(name = "Service Expediente Controller", description = "Service Expediente Controller")
@RestController
@RequestMapping("/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @Operation(summary = "Obtener Cabeceras de expedientes", description = "Obtener Cabeceras de expedientes")
    @PostMapping("/listar/cabeceras")
    public ResponseEntity<List<DataCabExpedienteDTO>> listarExpedientes(@Valid @RequestBody InputCabExpediente input) throws Exception{

        List<DataCabExpedienteDTO> expedientes = expedienteService.findCabExpedientes(input);

        if(expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }

        return new ResponseEntity<List<DataCabExpedienteDTO>>(expedientes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener Data de expedientes", description = "Obtener Data de expedientes")
    @GetMapping("/getData/{nUnico}")
    public ResponseEntity<List<DataExpedienteDTO>> listarExpedientes(@PathVariable("nUnico") Long  nUnico) throws Exception{

        List<DataExpedienteDTO> expedientes = expedienteService.getDataExpediente(nUnico);

        if(expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }

        return new ResponseEntity<List<DataExpedienteDTO>>(expedientes, HttpStatus.OK);
    }
}
