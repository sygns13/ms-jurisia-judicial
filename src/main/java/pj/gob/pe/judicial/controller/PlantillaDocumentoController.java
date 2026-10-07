package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;
import pj.gob.pe.judicial.service.mysql.MigracionPlantillaService;
import pj.gob.pe.judicial.service.mysql.PlantillaDocumentoService;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseMigracionPlantillas;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaDocumento;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaVariable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponsePlantillaDetalle;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseVariableSistema;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;
import pj.gob.pe.judicial.utils.plantilla.ConstantesPlantilla;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Admin Plantilla Documento Controller", description = "Mantenimiento de plantillas Word (.docx) completas y sus variables")
@RestController
@RequestMapping("/v1/admin/plantillas")
@RequiredArgsConstructor
public class PlantillaDocumentoController {

    private final PlantillaDocumentoService plantillaDocumentoService;
    private final MigracionPlantillaService migracionPlantillaService;

    // ---------------------------- PLANTILLAS ----------------------------

    @Operation(summary = "Listar plantillas", description = "Lista las plantillas no borradas (sin el archivo), opcionalmente por documento")
    @GetMapping
    public ResponseEntity<List<PlantillaDocumento>> listar(
            @RequestHeader("SessionId") String SessionId,
            @RequestParam(name = "idDocumento", required = false) Long idDocumento) {

        return new ResponseEntity<>(plantillaDocumentoService.listar(SessionId, idDocumento), HttpStatus.OK);
    }

    @Operation(summary = "Obtener plantilla", description = "Metadata de la plantilla, sus variables y advertencias de configuración")
    @GetMapping("/{id}")
    public ResponseEntity<ResponsePlantillaDetalle> listarPorId(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        return new ResponseEntity<>(plantillaDocumentoService.listarPorId(SessionId, id), HttpStatus.OK);
    }

    @Operation(summary = "Registrar plantilla",
            description = "multipart/form-data: campos idDocumento, codigo, nombreOut, descripcion, corregirIA y el archivo .docx en 'archivo'. " +
                    "Las variables ${...} del Word se detectan y registran automáticamente: SISTEMA si existen en el catálogo, " +
                    "MANUAL en otro caso (nunca IA)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponsePlantillaDetalle> registrar(
            @RequestHeader("SessionId") String SessionId,
            @Valid @ModelAttribute InputPlantillaDocumento input,
            @RequestPart("archivo") MultipartFile archivo) {

        ResponsePlantillaDetalle detalle = plantillaDocumentoService.registrar(SessionId, input, archivo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(detalle.getPlantilla().getIdPlantilla()).toUri();

        return ResponseEntity.created(location).body(detalle);
    }

    @Operation(summary = "Modificar plantilla", description = "Modifica la metadata (documento, código, nombre de salida, descripción, corregirIA). El archivo se reemplaza en /{id}/archivo")
    @PutMapping("/{id}")
    public ResponseEntity<ResponsePlantillaDetalle> modificar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @Valid @RequestBody InputPlantillaDocumento input) {

        return new ResponseEntity<>(plantillaDocumentoService.modificar(SessionId, id, input), HttpStatus.OK);
    }

    @Operation(summary = "Reemplazar archivo .docx", description = "Sube una nueva versión del Word (incrementa version). Elimina físicamente todas las variables de la plantilla " +
                    "y las vuelve a registrar según el Word: SISTEMA si existen en el catálogo, MANUAL en otro caso (nunca IA). " +
                    "La configuración manual previa no se conserva")
    @PutMapping(value = "/{id}/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponsePlantillaDetalle> reemplazarArchivo(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @RequestPart("archivo") MultipartFile archivo) {

        return new ResponseEntity<>(plantillaDocumentoService.reemplazarArchivo(SessionId, id, archivo), HttpStatus.OK);
    }

    @Operation(summary = "Descargar archivo .docx", description = "Descarga el Word vigente de la plantilla para editarlo")
    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> descargarArchivo(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        ResultadoDocumentoPlantilla archivo = plantillaDocumentoService.descargarArchivo(SessionId, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo.getNombreArchivo(), StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType(ConstantesPlantilla.MIME_DOCX))
                .body(archivo.getContenido());
    }

    @Operation(summary = "Eliminar plantilla", description = "Borrado lógico de la plantilla")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        plantillaDocumentoService.eliminar(SessionId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar/Desactivar plantilla", description = "valor = 1 activa, 0 desactiva")
    @PatchMapping("/activation/{id}/{valor}")
    public ResponseEntity<PlantillaDocumento> altabaja(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @PathVariable("valor") Integer valor) {

        return new ResponseEntity<>(plantillaDocumentoService.altabaja(SessionId, id, valor), HttpStatus.OK);
    }

    // ---------------------------- MIGRACIÓN ----------------------------

    @Operation(summary = "Migrar el flujo por secciones",
            description = "Crea una plantilla por cada Documento con codigoTemplate: Word base + contenido de SectionTemplates, con las " +
                    "variables configuradas como las resolvía el flujo anterior. Idempotente (omite documentos con plantilla). " +
                    "Con simular=true (por defecto) no escribe nada y devuelve el detalle de lo que haría")
    @PostMapping("/migrar-flujo-secciones")
    public ResponseEntity<ResponseMigracionPlantillas> migrarFlujoSecciones(
            @RequestHeader("SessionId") String SessionId,
            @RequestParam(name = "simular", defaultValue = "true") boolean simular) {

        return new ResponseEntity<>(migracionPlantillaService.migrarFlujoSecciones(SessionId, simular), HttpStatus.OK);
    }

    // ---------------------------- VARIABLES ----------------------------

    @Operation(summary = "Catálogo de variables del sistema", description = "Variables que se resuelven solas escribiendo ${clave} en el Word")
    @GetMapping("/variables-sistema")
    public ResponseEntity<List<ResponseVariableSistema>> listarVariablesSistema(
            @RequestHeader("SessionId") String SessionId) {

        return new ResponseEntity<>(plantillaDocumentoService.listarVariablesSistema(SessionId), HttpStatus.OK);
    }

    @Operation(summary = "Listar variables de la plantilla", description = "Variables configuradas de la plantilla")
    @GetMapping("/{id}/variables")
    public ResponseEntity<List<PlantillaVariable>> listarVariables(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id) {

        return new ResponseEntity<>(plantillaDocumentoService.listarVariables(SessionId, id), HttpStatus.OK);
    }

    @Operation(summary = "Registrar variable", description = "Registra una variable a mano (por ejemplo un bloque IA antes de agregarlo al Word)")
    @PostMapping("/{id}/variables")
    public ResponseEntity<PlantillaVariable> registrarVariable(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @Valid @RequestBody InputPlantillaVariable input) {

        PlantillaVariable variable = plantillaDocumentoService.registrarVariable(SessionId, id, input);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{idVariable}")
                .buildAndExpand(variable.getIdVariable()).toUri();

        return ResponseEntity.created(location).body(variable);
    }

    @Operation(summary = "Modificar variable", description = "Cambia origen (SISTEMA/MANUAL/IA), campo de sistema, valor por defecto o instrucción de IA")
    @PutMapping("/{id}/variables/{idVariable}")
    public ResponseEntity<PlantillaVariable> modificarVariable(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @PathVariable("idVariable") Long idVariable,
            @Valid @RequestBody InputPlantillaVariable input) {

        return new ResponseEntity<>(plantillaDocumentoService.modificarVariable(SessionId, id, idVariable, input), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar variable", description = "Eliminación física de la variable (si sigue en el Word, se resolverá por catálogo o quedará como '...')")
    @DeleteMapping("/{id}/variables/{idVariable}")
    public ResponseEntity<Void> eliminarVariable(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("id") Long id,
            @PathVariable("idVariable") Long idVariable) {

        plantillaDocumentoService.eliminarVariable(SessionId, id, idVariable);
        return ResponseEntity.noContent().build();
    }
}
