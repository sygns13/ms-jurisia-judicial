package pj.gob.pe.judicial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.model.mysql.entities.Instancia;
import pj.gob.pe.judicial.service.mysql.InstanciaService;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/instancias")
@RequiredArgsConstructor
public class InstanciaMysqlController {

    private final InstanciaService instanciaService;

    // Endpoint para obtener todas las instancias
    @GetMapping
    public List<Instancia> getAllInstancias() {
        return instanciaService.getAllInstancias();
    }

    // Endpoint para obtener una instancia por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Instancia> getInstanciaById(@PathVariable String id) {
        return instanciaService.getInstanciaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para crear una nueva instancia
    @PostMapping
    public ResponseEntity<Instancia> createInstancia(@RequestBody Instancia instancia) {
        Instancia nuevaInstancia = instanciaService.createInstancia(instancia);
        return new ResponseEntity<>(nuevaInstancia, HttpStatus.CREATED);
    }

    // Endpoint para actualizar una instancia existente
    @PutMapping("/{id}")
    public ResponseEntity<Instancia> updateInstancia(@PathVariable String id, @RequestBody Instancia instanciaDetails) {
        return instanciaService.updateInstancia(id, instanciaDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para eliminar una instancia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstancia(@PathVariable String id) {
        instanciaService.deleteInstancia(id);
        return ResponseEntity.noContent().build();
    }
}
