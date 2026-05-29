package pl.put.poznan.jsontools.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Standard API error body: {@code {"error":"..."}}. */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private final String error;
}
