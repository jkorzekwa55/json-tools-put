package pl.put.poznan.jsontools.exception;

/**
 * JSON error payload for transformation API failures.
 */
public class ErrorResponse {

    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
