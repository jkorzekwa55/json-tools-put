package pl.put.poznan.jsontools.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.put.poznan.jsontools.controller.JsonTransformController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps transformation errors to HTTP 400 for {@link JsonTransformController} only.
 */
@RestControllerAdvice(assignableTypes = JsonTransformController.class)
public class JsonTransformExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonTransformExceptionHandler.class);

    @ExceptionHandler(InvalidJsonException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(InvalidJsonException ex) {
        logger.info("Returning 400 Bad Request - JSON input is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid JSON"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Bad request";
        logger.info("Returning 400 Bad Request: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }
}
