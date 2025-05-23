package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pj.gob.pe.judicial.model.mysql.entities.Documento;
import pj.gob.pe.judicial.service.GenDocumentoService;
import pj.gob.pe.judicial.service.mysql.DocumentoService;

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

    @Operation(summary = "Generar Documento", description = "Generar Documento")
    @GetMapping("/generar-documento/{nUnico}/{codigo}")
    public ResponseEntity<byte[]> generateDocx(@PathVariable("nUnico") Long  nUnico, @PathVariable("codigo") String  codigo) throws Exception {


        byte[] documento = genDocumentoService.generateDocx(codigo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.docx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(documento);
    }
}
