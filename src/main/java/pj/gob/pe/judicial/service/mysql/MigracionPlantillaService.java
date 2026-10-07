package pj.gob.pe.judicial.service.mysql;

import pj.gob.pe.judicial.utils.beans.plantilla.ResponseMigracionPlantillas;

public interface MigracionPlantillaService {

    ResponseMigracionPlantillas migrarFlujoSecciones(String SessionId, boolean simular);
}
