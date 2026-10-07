package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.service.GenDocumentoPlantillaService;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoGenerable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseDocumentoPlantillaHTML;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;
import pj.gob.pe.judicial.utils.plantilla.ConstantesPlantilla;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Generación de documentos por plantilla completa. Equivalente a DocumentoController
 * (generar-documento-docx / generar-documento-web) sin el código de template en la ruta: la
 * plantilla se obtiene del idDocumento.
 */
@Tag(name = "Documento Plantilla Controller", description = "Generación de documentos a partir de plantillas Word completas")
@RestController
@RequestMapping("/v1/documento-plantilla")
@RequiredArgsConstructor
public class DocumentoPlantillaController {

    private final GenDocumentoPlantillaService genDocumentoPlantillaService;

    @Operation(summary = "Documentos generables", description = "Tipos de documento y documentos activos que tienen plantilla activa, opcionalmente por instancia")
    @GetMapping("/documentos")
    public ResponseEntity<List<ResponseDocumentoGenerable>> listarDocumentosGenerables(
            @RequestHeader("SessionId") String SessionId,
            @RequestParam(name = "idInstancia", required = false) String idInstancia) {

        return new ResponseEntity<>(genDocumentoPlantillaService.listarDocumentosGenerables(SessionId, idInstancia), HttpStatus.OK);
    }

    @Operation(summary = "Generar Documento DOCX por plantilla",
            description = "Devuelve el .docx. Headers: X-Estado-IA (NO_APLICA/EXITOSO/ERROR), X-Variables-Sin-Valor (cantidad) y X-Session-UID")
    @GetMapping("/generar-docx/{nUnico}/{numIncidente}/{idDocumento}")
    public ResponseEntity<byte[]> generarDocx(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("nUnico") Long nUnico,
            @PathVariable("numIncidente") String numIncidente,
            @PathVariable("idDocumento") Long idDocumento) throws Exception {

        ResultadoDocumentoPlantilla resultado = genDocumentoPlantillaService.generarDocx(nUnico, numIncidente, idDocumento, SessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(resultado.getNombreArchivo(), StandardCharsets.UTF_8).build().toString())
                .header(ConstantesPlantilla.HEADER_ESTADO_IA, resultado.getEstadoIA())
                .header(ConstantesPlantilla.HEADER_VARIABLES_SIN_VALOR, String.valueOf(resultado.getVariablesSinValor().size()))
                .header(ConstantesPlantilla.HEADER_SESSION_UID, resultado.getSessionUID())
                .contentType(MediaType.parseMediaType(ConstantesPlantilla.MIME_DOCX))
                .body(resultado.getContenido());
    }

    @Operation(summary = "Generar Documento HTML por plantilla", description = "Devuelve el documento en HTML para edición/revisión en el frontend")
    @GetMapping("/generar-web/{nUnico}/{numIncidente}/{idDocumento}")
    public ResponseEntity<ResponseDocumentoPlantillaHTML> generarHTML(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("nUnico") Long nUnico,
            @PathVariable("numIncidente") String numIncidente,
            @PathVariable("idDocumento") Long idDocumento) throws Exception {

        return new ResponseEntity<>(genDocumentoPlantillaService.generarHTML(nUnico, numIncidente, idDocumento, SessionId), HttpStatus.OK);
    }
}
