package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.DataSedeDTO;
import pj.gob.pe.judicial.model.sybase.dto.DataUsuarioDTO;
import pj.gob.pe.judicial.service.SedeService;
import pj.gob.pe.judicial.utils.beans.InputUsuario;

import java.util.List;

@Tag(name = "Service Sede Controller", description = "Service Sede Controller")
@RestController
@RequestMapping("/v1/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @Operation(summary = "Obtener sedes activas", description = "Obtener sedes activas")
    @GetMapping("/active")
    public ResponseEntity<List<DataSedeDTO>> listarSedes() throws Exception{

        List<DataSedeDTO> sedes = sedeService.findActiveSedes();

        if(sedes == null) {
            throw new ModeloNotFoundException("Sedes no encontrada");
        }

        return new ResponseEntity<List<DataSedeDTO>>(sedes, HttpStatus.OK);
    }
}
