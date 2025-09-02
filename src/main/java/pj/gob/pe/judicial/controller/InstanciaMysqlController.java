package pj.gob.pe.judicial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.service.mysql.InstanciaMySqlService;

import java.util.List;

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
}
