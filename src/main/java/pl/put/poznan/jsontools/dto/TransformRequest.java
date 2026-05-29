package pl.put.poznan.jsontools.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/** Request for {@code POST /api/json/transform}: JSON text, ordered actions, optional key lists. */
@Data
@NoArgsConstructor
public class TransformRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "actions is required")
    private List<String> actions;
    private List<String> keysToExclude;
    private List<String> keysToKeep;
}
