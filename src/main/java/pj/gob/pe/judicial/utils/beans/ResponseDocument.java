package pj.gob.pe.judicial.utils.beans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pj.gob.pe.judicial.model.mysql.entities.SectionTemplate;

import java.util.List;

@Schema(description = "Response Document to Formatted")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDocument {

    @Schema(description = "ID de Expediente")
    Long nUnico;

    @Schema(description = "Template Code")
    String codeTemplate;

    @Schema(description = "Section Templates")
    List<SectionTemplate> sectionTemplates;
}
