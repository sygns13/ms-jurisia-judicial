package pj.gob.pe.judicial.service.externals;

import pj.gob.pe.judicial.utils.beans.plantilla.InputProcesarPlantillaIA;
import pj.gob.pe.judicial.utils.beans.plantilla.ResponseProcesarPlantillaIA;

public interface ConsultaiaPlantillaService {

    ResponseProcesarPlantillaIA ProcesarPlantillaGemini(InputProcesarPlantillaIA input, String SessionId);
}
