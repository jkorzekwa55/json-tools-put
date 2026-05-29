package pl.put.poznan.jsontools.exception;

/** Unparseable JSON input (often wraps Jackson {@link com.fasterxml.jackson.core.JsonProcessingException}). */
public class InvalidJsonException extends RuntimeException {

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
