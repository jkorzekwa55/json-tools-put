package pl.put.poznan.jsontools.exception;

/**
 * Thrown when the {@code json} string cannot be parsed (wraps Jackson's {@link com.fasterxml.jackson.core.JsonProcessingException}).
 */
public class InvalidJsonException extends RuntimeException {

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
