package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.service.GenDocumentoService;
import pj.gob.pe.judicial.service.mysql.DocumentoService;
import pj.gob.pe.judicial.utils.beans.AuxDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocumentHTML;

import java.util.List;

@Tag(name = "Documento Controller", description = "Documento Controller")
@RestController
@RequestMapping("/v1/documento")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private final GenDocumentoService genDocumentoService;

    @Operation(summary = "Consulta Lista de todos los documentos", description = "Retorna una Lista de todos los documentos")
    @GetMapping("")
    public ResponseEntity<List<Documento>> listarAll(
            //@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization
    ) throws Exception{

        //this.SetClaims(Authorization);

        List<Documento> resultado = documentoService.listarActivosNoBorrados();

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @Operation(summary = "Generar Documento DOCX", description = "Generar Documento DOCX")
    @GetMapping("/generar-documento-docx/{nUnico}/{numIncidente}/{codigo}/{idDocumento}")
    public ResponseEntity<byte[]> generateDocx(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("nUnico") Long  nUnico, @PathVariable("numIncidente") String  numIncidente, @PathVariable("codigo") String  codigo, @PathVariable("idDocumento") Long  idDocumento) throws Exception {

        AuxDocument auxDocument = new AuxDocument();
        auxDocument.setNombreDoc("nombre.docx");
        byte[] documento = genDocumentoService.generateDocx(nUnico, numIncidente, codigo, auxDocument, SessionId, idDocumento);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+auxDocument.getNombreDoc()+"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(documento);
    }

    @Operation(summary = "Generar Documento HTML", description = "Generar Documento HTML")
    @GetMapping("/generar-documento-web/{nUnico}/{numIncidente}/{codigo}/{idDocumento}")
    public ResponseEntity<ResponseDocumentHTML> generateHTMLDocx(
            @RequestHeader("SessionId") String SessionId,
            @PathVariable("nUnico") Long  nUnico, @PathVariable("numIncidente") String  numIncidente, @PathVariable("codigo") String  codigo, @PathVariable("idDocumento") Long  idDocumento) throws Exception {


        ResponseDocumentHTML resultado = genDocumentoService.generateDocxHTML(nUnico, numIncidente, codigo, SessionId, idDocumento);

        if(resultado == null) {
            throw new ModeloNotFoundException("Documento no Generado");
        }

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}
