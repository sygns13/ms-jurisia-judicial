package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pj.gob.pe.judicial.model.mysql.dto.SedeInstanciaDTO;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.service.mysql.InstanciaMySqlService;
import pj.gob.pe.judicial.utils.beans.InputSedeInstancia;

import java.net.URI;
import java.util.List;

@Tag(name = "Service Instancia MySQL Controller", description = "Mantenimiento de Sedes/Instancias habilitadas en MySQL")
@RestController
@RequestMapping("/v1/admin/instancias")
@RequiredArgsConstructor
public class InstanciaMysqlController {

    private final InstanciaMySqlService instanciaService;

    // Endpoint para obtener todas las instancias
    @GetMapping
    public List<Instancia> getAllInstancias(@RequestHeader("SessionId") String SessionId) {
        return instanciaService.getAllInstancias(SessionId);
    }

    // Endpoint para obtener una instancia por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Instancia> getInstanciaById(@RequestHeader("SessionId") String SessionId, @PathVariable String id) {
        return instanciaService.getInstanciaById(SessionId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para crear una nueva instancia
    @PostMapping
    public ResponseEntity<Instancia> createInstancia(@RequestHeader("SessionId") String SessionId, @RequestBody Instancia instancia) {
        Instancia nuevaInstancia = instanciaService.createInstancia(SessionId, instancia);
        return new ResponseEntity<>(nuevaInstancia, HttpStatus.CREATED);
    }

    // Endpoint para actualizar una instancia existente
    @PutMapping("/{id}")
    public ResponseEntity<Instancia> updateInstancia(@RequestHeader("SessionId") String SessionId, @PathVariable String id, @RequestBody Instancia instanciaDetails) {
        return instanciaService.updateInstancia(SessionId, id, instanciaDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para eliminar una instancia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstancia(@RequestHeader("SessionId") String SessionId, @PathVariable String id) {
        instanciaService.deleteInstancia(SessionId, id);
        return ResponseEntity.noContent().build();
    }

//    ---------------------MANTENIMIENTO DE SEDES/INSTANCIAS-----------------------

    // Endpoint para obtener las combinaciones Sede/Instancia registradas en MySQL
    @Operation(summary = "Listar Sedes/Instancias registradas", description = "Listar las combinaciones Sede/Instancia registradas en MySQL (JURISDB_JUDICIAL)")
    @GetMapping("/sedes-instancias")
    public ResponseEntity<List<SedeInstanciaDTO>> listarSedesInstancias(@RequestHeader("SessionId") String SessionId) {

        List<SedeInstanciaDTO> sedesInstancias = instanciaService.listarSedesInstancias(SessionId);

        return new ResponseEntity<List<SedeInstanciaDTO>>(sedesInstancias, HttpStatus.OK);
    }

    // Endpoint para registrar la combinación Sede/Instancia en MySQL
    @Operation(summary = "Registrar Sede/Instancia", description = "Registra la combinación Sede/Instancia en MySQL. Si la Sede ya existe solo la actualiza y registra la nueva Instancia")
    @PostMapping("/sedes-instancias")
    public ResponseEntity<SedeInstanciaDTO> registrarSedeInstancia(
            @RequestHeader("SessionId") String SessionId,
            @Valid @RequestBody InputSedeInstancia input) {

        SedeInstanciaDTO sedeInstancia = instanciaService.registrarSedeInstancia(SessionId, input);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{idSede}/{idInstancia}")
                .buildAndExpand(sedeInstancia.getIdSede(), sedeInstancia.getIdInstancia()).toUri();

        return ResponseEntity.created(location).body(sedeInstancia);
    }

    // Endpoint para eliminar la combinación Sede/Instancia (solo se elimina la Instancia)
    @Operation(summary = "Eliminar Sede/Instancia", description = "Elimina la combinación Sede/Instancia en MySQL, eliminando únicamente la Instancia")
    @DeleteMapping("/sedes-instancias/{idSede}/{idInstancia}")
    public ResponseEntity<Void> eliminarSedeInstancia(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("idSede") String idSede,
            @PathVariable("idInstancia") String idInstancia) {

        instanciaService.eliminarSedeInstancia(SessionId, idSede, idInstancia);

        return ResponseEntity.noContent().build();
    }
}
