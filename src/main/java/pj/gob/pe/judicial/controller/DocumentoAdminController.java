package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.service.mysql.DocumentoAdminService;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminDocumento;

import java.net.URI;
import java.util.List;

@Tag(name = "Admin Documento Controller", description = "Mantenimiento de Documentos (MySQL)")
@RestController
@RequestMapping("/v1/admin/documento")
@RequiredArgsConstructor
public class DocumentoAdminController {

    private final DocumentoAdminService documentoAdminService;

    @Operation(summary = "Listar Documentos", description = "Lista los documentos no borrados (activos e inactivos), opcionalmente filtrados por tipo de documento")
    @GetMapping
    public ResponseEntity<List<Documento>> listar(
            @RequestHeader("SessionId") String SessionId,
            @RequestParam(name = "idTipoDocumento", required = false) Long idTipoDocumento) {

        return new ResponseEntity<>(documentoAdminService.listar(SessionId, idTipoDocumento), HttpStatus.OK);
    }

    @Operation(summary = "Obtener Documento", description = "Obtiene un documento por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Documento> listarPorId(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        return new ResponseEntity<>(documentoAdminService.listarPorId(SessionId, id), HttpStatus.OK);
    }

    @Operation(summary = "Registrar Documento", description = "Registra un documento dentro de un tipo de documento")
    @PostMapping
    public ResponseEntity<Documento> registrar(
            @RequestHeader("SessionId") String SessionId,
            @Valid @RequestBody InputAdminDocumento input) {

        Documento documento = documentoAdminService.registrar(SessionId, input);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(documento.getIdDocumento()).toUri();

        return ResponseEntity.created(location).body(documento);
    }

    @Operation(summary = "Modificar Documento", description = "Modifica tipo, descripción y código de template (flujo por secciones) del documento")
    @PutMapping("/{id}")
    public ResponseEntity<Documento> modificar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @Valid @RequestBody InputAdminDocumento input) {

        return new ResponseEntity<>(documentoAdminService.modificar(SessionId, id, input), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar Documento", description = "Borrado lógico. No se permite si tiene una plantilla registrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        documentoAdminService.eliminar(SessionId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar/Desactivar Documento", description = "valor = 1 activa, 0 desactiva")
    @PatchMapping("/activation/{id}/{valor}")
    public ResponseEntity<Documento> altabaja(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @PathVariable("valor") Integer valor) {

        return new ResponseEntity<>(documentoAdminService.altabaja(SessionId, id, valor), HttpStatus.OK);
    }
}
