package pj.gob.pe.judicial.service.externals;


import pj.gob.pe.judicial.utils.beans.ResponseLogin;

public interface SecurityService {

    public ResponseLogin GetSessionData(String SessionId);
}
