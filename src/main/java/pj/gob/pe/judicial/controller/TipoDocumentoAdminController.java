package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.service.mysql.TipoDocumentoAdminService;
import pj.gob.pe.judicial.utils.beans.plantilla.InputAdminTipoDocumento;

import java.net.URI;
import java.util.List;

@Tag(name = "Admin Tipo Documento Controller", description = "Mantenimiento de Tipos de Documento (MySQL)")
@RestController
@RequestMapping("/v1/admin/tipodocumento")
@RequiredArgsConstructor
public class TipoDocumentoAdminController {

    private final TipoDocumentoAdminService tipoDocumentoAdminService;

    @Operation(summary = "Listar Tipos de Documento", description = "Lista los tipos de documento no borrados (activos e inactivos), opcionalmente filtrados por instancia")
    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar(
            @RequestHeader("SessionId") String SessionId,
            @RequestParam(name = "idInstancia", required = false) String idInstancia) {

        return new ResponseEntity<>(tipoDocumentoAdminService.listar(SessionId, idInstancia), HttpStatus.OK);
    }

    @Operation(summary = "Obtener Tipo de Documento", description = "Obtiene un tipo de documento por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumento> listarPorId(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        return new ResponseEntity<>(tipoDocumentoAdminService.listarPorId(SessionId, id), HttpStatus.OK);
    }

    @Operation(summary = "Registrar Tipo de Documento", description = "Registra un tipo de documento para una instancia")
    @PostMapping
    public ResponseEntity<TipoDocumento> registrar(
            @RequestHeader("SessionId") String SessionId,
            @Valid @RequestBody InputAdminTipoDocumento input) {

        TipoDocumento tipoDocumento = tipoDocumentoAdminService.registrar(SessionId, input);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(tipoDocumento.getIdTipoDocumento()).toUri();

        return ResponseEntity.created(location).body(tipoDocumento);
    }

    @Operation(summary = "Modificar Tipo de Documento", description = "Modifica la instancia y descripción de un tipo de documento")
    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumento> modificar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @Valid @RequestBody InputAdminTipoDocumento input) {

        return new ResponseEntity<>(tipoDocumentoAdminService.modificar(SessionId, id, input), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar Tipo de Documento", description = "Borrado lógico. No se permite si tiene documentos registrados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        tipoDocumentoAdminService.eliminar(SessionId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar/Desactivar Tipo de Documento", description = "valor = 1 activa, 0 desactiva")
    @PatchMapping("/activation/{id}/{valor}")
    public ResponseEntity<TipoDocumento> altabaja(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @PathVariable("valor") Integer valor) {

        return new ResponseEntity<>(tipoDocumentoAdminService.altabaja(SessionId, id, valor), HttpStatus.OK);
    }
}
