package pl.put.poznan.jsontools.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Body for filtering keys: {@code { "json": "...", "keysToKeep": ["a", "b"] }}.
 */
@Data
@NoArgsConstructor
public class FilterKeysRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "keysToKeep is required")
    private List<String> keysToKeep;
}
