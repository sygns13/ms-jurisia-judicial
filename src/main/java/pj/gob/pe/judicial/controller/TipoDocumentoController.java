package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pj.gob.pe.judicial.model.mysql.entities.TipoDocumento;
import pj.gob.pe.judicial.service.mysql.TipoDocumentoService;

import java.util.List;

@Tag(name = "Tipo Documento Controller", description = "Tipo Documento Controller")
@RestController
@RequestMapping("/v1/tipodocumento")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    @Operation(summary = "Consulta Lista de todos los tipodocumento", description = "Retorna una Lista de todos los tipodocumento")
    @GetMapping("")
    public ResponseEntity<List<TipoDocumento>> listarAll(
            //@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization
    ) throws Exception{

        //this.SetClaims(Authorization);

        List<TipoDocumento> resultado = tipoDocumentoService.listarAll();

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @Operation(summary = "Consulta de tipodocumentos por instancia", description = "Consulta de tipodocumentos por instancia")
    @GetMapping("/{idInstancia}")
    public ResponseEntity<List<TipoDocumento>> listarPorId(
            //@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization,
            @PathVariable("idInstancia") String  idInstancia) throws Exception{

        //this.SetClaims(Authorization);

        List<TipoDocumento> resultado = tipoDocumentoService.listarByInstancia(idInstancia);

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}
