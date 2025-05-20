package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pj.gob.pe.judicial.service.GenDocumentoService;

@Tag(name = "Test Gen Documento Controller", description = "Test Gen Documento Controller")
@RestController
@RequestMapping("/v1/gen-documento")
@RequiredArgsConstructor
public class TestDocumentController {

    private final GenDocumentoService genDocumentoService;

    @Operation(summary = "Generar Documento", description = "Generar Documento")
    @GetMapping("/test/{codigo}")
    public ResponseEntity<byte[]> generateDocx(@PathVariable("codigo") String  codigo) throws Exception {


        byte[] documento = genDocumentoService.generateDocx(codigo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.docx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(documento);
    }
}
