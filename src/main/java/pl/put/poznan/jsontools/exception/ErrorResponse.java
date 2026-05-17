package pl.put.poznan.jsontools.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JSON error payload for transformation API failures.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private final String error;
}
