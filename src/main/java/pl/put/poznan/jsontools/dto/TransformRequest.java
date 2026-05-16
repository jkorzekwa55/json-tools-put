package pl.put.poznan.jsontools.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Unified transform body: {@code { "json": "...", "actions": ["minify", "exclude-keys"], "keysToExclude": ["x"] }}
 * <p>
 * {@code keysToExclude} is used when an {@code exclude-keys} action appears (same list for each such step).
 */
@Data
@NoArgsConstructor
public class TransformRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "actions is required")
    private List<String> actions;
    private List<String> keysToExclude;
}
