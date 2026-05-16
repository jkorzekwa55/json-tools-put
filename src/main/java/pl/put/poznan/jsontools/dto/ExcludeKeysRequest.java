package pl.put.poznan.jsontools.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Body for excluding keys: {@code { "json": "...", "keysToExclude": ["a", "b"] }}.
 */
@Data
@NoArgsConstructor
public class ExcludeKeysRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "keysToExclude is required")
    private List<String> keysToExclude;
}
