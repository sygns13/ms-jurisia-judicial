package pj.gob.pe.judicial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pj.gob.pe.judicial.exception.ModeloNotFoundException;
import pj.gob.pe.judicial.model.sybase.dto.DataUsuarioDTO;
import pj.gob.pe.judicial.model.sybase.dto.UsuarioDTO;
import pj.gob.pe.judicial.service.UsuarioService;
import pj.gob.pe.judicial.utils.beans.InputUsuario;

@Tag(name = "Service User Controller", description = "Service User Controller")
@RestController
@RequestMapping("/v1/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Obtener usuario por Username y DNI", description = "Obtener usuario por Username y DNI")
    @PostMapping("/consulta")
    public ResponseEntity<DataUsuarioDTO> registrar(@Valid @RequestBody InputUsuario user) throws Exception{

        DataUsuarioDTO usuario = usuarioService.findByCredentials(user.getUsername(), user.getPassword());

        if(usuario == null) {
            throw new ModeloNotFoundException("Usuario no encontrado");
        }

        return new ResponseEntity<DataUsuarioDTO>(usuario, HttpStatus.OK);
    }

}
