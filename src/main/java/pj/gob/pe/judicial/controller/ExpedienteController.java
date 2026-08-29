package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.*;
import pj.gob.pe.judicial.service.ExpedienteService;
import pj.gob.pe.judicial.utils.beans.InputCabExpediente;
import pj.gob.pe.judicial.utils.beans.InputCabExpedienteCalifica;
import pj.gob.pe.judicial.utils.beans.InputDocumentoDigital;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pj.gob.pe.judicial.utils.beans.InputObtenerPdf;

import java.util.List;

@Tag(name = "Service Expediente Controller", description = "Service Expediente Controller")
@RestController
@RequestMapping("/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @Operation(summary = "Obtener Cabeceras de expedientes", description = "Obtener Cabeceras de expedientes")
    @PostMapping("/listar/cabeceras")
    public ResponseEntity<List<DataCabExpedienteDTO>> listarExpedientes(@Valid @RequestBody InputCabExpediente input) throws Exception{

        List<DataCabExpedienteDTO> expedientes = expedienteService.findCabExpedientes(input);

        if(expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }

        return new ResponseEntity<List<DataCabExpedienteDTO>>(expedientes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener Data de expedientes", description = "Obtener Data de expedientes")
    @GetMapping("/getData/{nUnico}")
    public ResponseEntity<List<DataExpedienteDTO>> listarExpedientes(@PathVariable("nUnico") Long  nUnico) throws Exception{

        String numIncidenteDefault = "0";
        List<DataExpedienteDTO> expedientes = expedienteService.getDataExpediente(nUnico, numIncidenteDefault);

        if(expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }

        return new ResponseEntity<List<DataExpedienteDTO>>(expedientes, HttpStatus.OK);
    }

    @Operation(summary = "Buscar expediente por número completo", description = "Consulta un expediente a partir de su número completo")
    @GetMapping("/buscar/{numeroExpediente}")
    public ResponseEntity<List<CabExpedienteChatDTO>> buscarPorNumeroExpediente(@PathVariable("numeroExpediente") String numeroExpediente) throws Exception {

        List<CabExpedienteChatDTO> expedientes = expedienteService.getDataExpedientePorNumero(numeroExpediente);

        if (expedientes == null || expedientes.isEmpty()) {
            throw new ModeloNotFoundException("Expediente no encontrado con número: " + numeroExpediente);
        }

        return new ResponseEntity<>(expedientes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener lista de partes por nUnico", description = "Devuelve las partes asociadas al expediente")
    @GetMapping("/listar-partes/{nUnico}")
    public ResponseEntity<List<DataTipoParteDTO>> listarPartes(@PathVariable("nUnico") Long nUnico) throws Exception {

        List<DataTipoParteDTO> partes = expedienteService.getPartesByNUnico(nUnico);

        if (partes == null || partes.isEmpty()) {
            throw new ModeloNotFoundException("No se encontraron partes para el expediente con nUnico: " + nUnico);
        }

        return new ResponseEntity<>(partes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener resumen de expediente y partes", description = "Retorna información general del expediente con partes")
    @GetMapping("/resumen-partes/{nUnico}")
    public ResponseEntity<List<ResumenExpedienteParteDTO>> getResumenPartes(@PathVariable("nUnico") Long nUnico) throws Exception {

        List<ResumenExpedienteParteDTO> resultado = expedienteService.getResumenExpedienteYPartes(nUnico);

        if (resultado == null || resultado.isEmpty()) {
            throw new ModeloNotFoundException("No se encontró información para el expediente: " + nUnico);
        }

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }


//    ---------------------EXPEDIENTES POR CALIFICAR-----------------------
    @Operation(summary = "Obtener Cabeceras de expedientes a calificar", description = "Obtener Cabeceras de expedientes")
    @PostMapping("/listar/cabecerascalificar")
    public ResponseEntity<List<DataCabExpedienteCalificarDTO>> listarExpedientesCalificar(
            @RequestHeader("SessionId") String SessionId,
            @Valid @RequestBody InputCabExpedienteCalifica input) throws Exception {
        List<DataCabExpedienteCalificarDTO> expedientes =
                expedienteService.findCabExpedientesCalificar(SessionId, input);
        if (expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }
        return new ResponseEntity<>(expedientes, HttpStatus.OK);
    }

//    ---------------------EXPEDIENTES POR SENTENCIAR-----------------------
    @Operation(summary = "Obtener Cabecera de expediente a sentenciar", description = "Obtener la cabecera de un expediente (por Sede/Instancia/Especialidad/Número/Año) para sentenciar")
    @PostMapping("/listar/cabecerassentenciar")
    public ResponseEntity<List<DataCabExpedienteCalificarDTO>> listarExpedientesSentenciar(
            @RequestHeader("SessionId") String SessionId,
            @Valid @RequestBody InputCabExpediente input) throws Exception {
        List<DataCabExpedienteCalificarDTO> expedientes =
                expedienteService.findCabExpedientesSentenciar(SessionId, input);
        if (expedientes == null) {
            throw new ModeloNotFoundException("Expedientes no encontrados");
        }
        return new ResponseEntity<>(expedientes, HttpStatus.OK);
    }

    @Operation(summary = "Listar documentos de Resoluciones del expediente", description = "Documentos digitales tipo Resolución (l_tipo_doc = 'R')")
    @PostMapping("/listar/documentos/resoluciones")
    public ResponseEntity<List<DataDocumentoDigitalDTO>> listarResoluciones(
            @Valid @RequestBody InputDocumentoDigital input) throws Exception {
        List<DataDocumentoDigitalDTO> documentos =
                expedienteService.findDocumentosResoluciones(input.getNUnico(), input.getNIncidente());
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    @Operation(summary = "Listar documentos Digitalizados del expediente", description = "Documentos digitales tipo Digitalizado (l_tipo_doc IN (d))")
    @PostMapping("/listar/documentos/digitalizados")
    public ResponseEntity<List<DataDocumentoDigitalDTO>> listarDigitalizados(
            @Valid @RequestBody InputDocumentoDigital input) throws Exception {
        List<DataDocumentoDigitalDTO> documentos =
                expedienteService.findDocumentosDigitalizados(input.getNUnico(), input.getNIncidente());
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    @Operation(summary = "Obtener PDF de expediente desde FTP", description = "Descarga un archivo PDF desde el servidor FTP")
    @PostMapping(value = "/obtener-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> obtenerPdf(@RequestBody InputObtenerPdf input) throws Exception {

        byte[] pdfBytes = expedienteService.obtenerPdfDesdeFtp(input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + input.getXnombreArchivo() + "\"");
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
