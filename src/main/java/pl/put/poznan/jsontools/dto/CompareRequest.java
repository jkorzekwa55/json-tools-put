package pl.put.poznan.jsontools.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Request body for compare: { "left": "...", "right": "..." }
 */
@Data
@NoArgsConstructor
public class CompareRequest {

    @NotNull(message = "left is required")
    private String left;

    @NotNull(message = "right is required")
    private String right;
}
