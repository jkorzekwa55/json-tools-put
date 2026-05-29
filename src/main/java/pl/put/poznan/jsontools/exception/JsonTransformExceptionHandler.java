package pl.put.poznan.jsontools.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.stream.Collectors;

/** Maps validation and parsing failures to HTTP 400 with {@link ErrorResponse}. */
@Slf4j
@RestControllerAdvice
public class JsonTransformExceptionHandler {
    @ExceptionHandler(InvalidJsonException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(InvalidJsonException ex) {
        log.info("Exception 400 Bad Request - JSON input is invalid");
        log.debug("Invalid JSON exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid JSON"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Bad request";
        log.info("Exception 400 Bad Request - illegal argument {}", message);
        log.debug("Bad request exception details", ex);
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
        log.info("Exception 400 Bad Request - request validation failed - {}", message);
        log.debug("Validation exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, WebRequest request) {
        log.info("Exception 400 Bad Request - request body is missing or unreadable");
        log.debug("Unreadable request body exception details", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Request body is required"));
    }
}
