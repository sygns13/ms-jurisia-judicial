package pj.gob.pe.judicial.service.mysql;

import org.springframework.web.multipart.MultipartFile;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaDocumento;
import pj.gob.pe.judicial.model.mysql.entities.PlantillaVariable;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaDocumento;
import pj.gob.pe.judicial.utils.beans.plantilla.InputPlantillaVariable;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponsePlantillaDetalle;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseVariableSistema;
import pj.gob.pe.judicial.utils.beans.plantilla.ResultadoDocumentoPlantilla;

import java.util.List;

public interface PlantillaDocumentoService {

    List<PlantillaDocumento> listar(String SessionId, Long idDocumento);

    ResponsePlantillaDetalle listarPorId(String SessionId, Long idPlantilla);

    ResponsePlantillaDetalle registrar(String SessionId, InputPlantillaDocumento input, MultipartFile archivo);

    ResponsePlantillaDetalle modificar(String SessionId, Long idPlantilla, InputPlantillaDocumento input);

    ResponsePlantillaDetalle reemplazarArchivo(String SessionId, Long idPlantilla, MultipartFile archivo);

    ResultadoDocumentoPlantilla descargarArchivo(String SessionId, Long idPlantilla);

    void eliminar(String SessionId, Long idPlantilla);

    PlantillaDocumento altabaja(String SessionId, Long idPlantilla, Integer valor);

    List<PlantillaVariable> listarVariables(String SessionId, Long idPlantilla);

    PlantillaVariable registrarVariable(String SessionId, Long idPlantilla, InputPlantillaVariable input);

    PlantillaVariable modificarVariable(String SessionId, Long idPlantilla, Long idVariable, InputPlantillaVariable input);

    void eliminarVariable(String SessionId, Long idPlantilla, Long idVariable);

    List<ResponseVariableSistema> listarVariablesSistema(String SessionId);
}
