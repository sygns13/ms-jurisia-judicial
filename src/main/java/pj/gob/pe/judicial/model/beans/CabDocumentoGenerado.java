package pj.gob.pe.judicial.model.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Entidad que representa la tabla CabDocumentoGenerado")
@Data // Lombok: Genera getters, setters, toString, equals, y hashCode
@NoArgsConstructor // Lombok: Constructor sin argumentos
@AllArgsConstructor // Lombok: Constructor con todos los argumentos
public class CabDocumentoGenerado {


    private Long id;
    private Long userId;
    private String typedoc;
    private String codSede;
    private String codInstancia;
    private String codEspecialidad;
    private String codMateria;
    private String sede;
    private String instancia;
    private String especialidad;
    private String materia;
    private String codNumero;
    private String codYear;
    private Long idDocumento;
    private Long idTipoDocumento;
    private String tipoDocumento;
    private String documento;
    private Long nUnico;
    private String xFormato;
    private String ubicacion;
    private String juez;
    private String estado;
    private String dniDemandante;
    private String demandante;
    private String dniDemandado;
    private String demandado;
    private String templateCode;
    private Long templateID;
    private String templateNombreOut;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate regDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDatetime;
    private Long regTimestamp;
}
