package pj.gob.pe.judicial.service.externals;

import pj.gob.pe.judicial.utils.beans.InputDocument;
import pj.gob.pe.judicial.utils.beans.ResponseDocument;

public interface ConsultaiaService {

    public ResponseDocument ProcessDocument(InputDocument inputDocument);
}
