package pl.put.poznan.jsontools.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Body for excluding keys: {@code { "json": "...", "keysToExclude": ["a", "b"] }}.
 */
public class ExcludeKeysRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "keysToExclude is required")
    private List<String> keysToExclude;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<String> getKeysToExclude() {
        return keysToExclude;
    }

    public void setKeysToExclude(List<String> keysToExclude) {
        this.keysToExclude = keysToExclude;
    }
}
