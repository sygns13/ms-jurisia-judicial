package pj.gob.pe.judicial.exception;

public class AuthOpenAIException extends RuntimeException{

    private static final long serialVersionUID = 7513221174646872318L;

    public AuthOpenAIException(String mensaje) {
        super(mensaje);
    }
}
