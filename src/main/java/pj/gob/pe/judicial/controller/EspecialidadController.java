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
import pj.gob.pe.judicial.model.sybase.dto.DataEspecialidadDTO;
import pj.gob.pe.judicial.service.EspecialidadService;

import java.util.List;

@Tag(name = "Service Especialidad Controller", description = "Service Especialidad Controller")
@RestController
@RequestMapping("/v1/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @Operation(summary = "Obtener especialidades", description = "Obtener especialidades")
    @GetMapping()
    public ResponseEntity<List<DataEspecialidadDTO>> listarEspecialidads() throws Exception{

        List<DataEspecialidadDTO> especialidades = especialidadService.findEspecialidades();

        if(especialidades == null) {
            throw new ModeloNotFoundException("Especialidads no encontrada");
        }

        return new ResponseEntity<List<DataEspecialidadDTO>>(especialidades, HttpStatus.OK);
    }
}
