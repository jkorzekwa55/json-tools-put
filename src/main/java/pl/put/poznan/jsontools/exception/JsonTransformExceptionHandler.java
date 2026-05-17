package pl.put.poznan.jsontools.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

/**
 * Maps request validation and parsing errors to HTTP 400 for all controllers.
 */
@RestControllerAdvice
public class JsonTransformExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonTransformExceptionHandler.class);

    @ExceptionHandler(InvalidJsonException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(InvalidJsonException ex) {
        logger.info("Exception 400 Bad Request - JSON input is invalid");
        logger.debug("Invalid JSON exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid JSON"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Bad request";
        logger.info("Exception 400 Bad Request - illegal argument {}", message);
        logger.debug("Bad request exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : fieldError.getField())
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = "Bad request";
        }
        logger.info("Exception 400 Bad Request - request validation failed - {}", message);
        logger.debug("Validation exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, WebRequest request) {
        logger.info("Exception 400 Bad Request - request body is missing or unreadable");
        logger.debug("Unreadable request body exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Request body is required"));
    }
}
