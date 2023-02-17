package dk.bemyndigelsesregister.exception;

public class ServiceRuntimeException extends RuntimeException {
    public ServiceRuntimeException(String message) {
        super(message);
    }

    public ServiceRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
