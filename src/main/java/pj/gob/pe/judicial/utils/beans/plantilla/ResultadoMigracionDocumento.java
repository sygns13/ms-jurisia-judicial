package pj.gob.pe.judicial.utils.beans.plantilla;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Resultado de la migración de un Documento del flujo por secciones al flujo por plantilla")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoMigracionDocumento {

    private Long idDocumento;
    private String documento;
    private String codigoTemplate;

    @Schema(description = "CREADA, SIMULADA, OMITIDA o ERROR")
    private String estado;
    private String mensaje;

    private Long idPlantilla;
    private String codigoPlantilla;

    @Schema(description = "Secciones ${sectionN} del Word base reemplazadas con su contenido")
    private Integer seccionesReemplazadas;

    @Schema(description = "Marcadores ${sectionN} del Word base sin sección activa en BD (quedan vacíos)")
    private List<String> seccionesSinContenido = new ArrayList<>();

    @Schema(description = "Marcadores del Word emparejados con una sección cuyo código difiere solo en ceros (ej. ${section09} <- section9)")
    private List<String> seccionesEmparejadas = new ArrayList<>();

    private Integer variablesDetectadas;
    private Integer variablesSistema;
    private Integer variablesManuales;

    @Schema(description = "Variables que el flujo por secciones no reemplazaba (se resolverán por catálogo o quedarán como '...')")
    private List<String> variablesSinMapeo = new ArrayList<>();
}
