package pj.gob.pe.judicial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.model.mysql.entities.Sede;
import pj.gob.pe.judicial.service.mysql.SedeMySqlService;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/sedes")
@RequiredArgsConstructor
public class SedeMysqlController {

    private final SedeMySqlService sedeMySqlService;

    // Endpoint para obtener todas las sedes
    @GetMapping
    public List<Sede> getAllSedes(@RequestHeader("SessionId") String SessionId) {
        return sedeMySqlService.getAllSedes(SessionId);
    }

    // Endpoint para obtener una sede por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Sede> getSedeById(@RequestHeader("SessionId") String SessionId, @PathVariable String id) {
        return sedeMySqlService.getSedeById(SessionId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para crear una nueva sede
    @PostMapping
    public ResponseEntity<Sede> createSede(@RequestHeader("SessionId") String SessionId, @RequestBody Sede sede) {
        Sede nuevaSede = sedeMySqlService.createSede(SessionId, sede);
        return new ResponseEntity<>(nuevaSede, HttpStatus.CREATED);
    }

    // Endpoint para actualizar una sede existente
    @PutMapping("/{id}")
    public ResponseEntity<Sede> updateSede(@RequestHeader("SessionId") String SessionId, @PathVariable String id, @RequestBody Sede sedeDetails) {
        return sedeMySqlService.updateSede(SessionId, id, sedeDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para eliminar una sede
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSede(@RequestHeader("SessionId") String SessionId, @PathVariable String id) {
        // Se podría verificar si la sede existe antes de intentar borrar
        sedeMySqlService.deleteSede(SessionId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para crear, actualizar o eliminar sedes e instancias en una sola operación.
     * Recibe una lista completa de sedes y sincroniza la base de datos con esta lista.
     * - Si una sede de la lista no existe en la BD, se crea con sus instancias.
     * - Si una sede de la lista ya existe en la BD, se actualiza junto con sus instancias.
     * - Si una sede de la BD no está en la lista recibida, se elimina.
     * @param sedes La lista completa de sedes a sincronizar.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @PostMapping("/upsert")
    public ResponseEntity<Void> upsertSedes(@RequestHeader("SessionId") String SessionId, @RequestBody List<Sede> sedes) {
        sedeMySqlService.upsertSedes(SessionId, sedes);
        return ResponseEntity.ok().build();
    }


}
