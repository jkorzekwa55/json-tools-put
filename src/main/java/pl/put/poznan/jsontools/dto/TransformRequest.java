package pl.put.poznan.jsontools.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Unified transform body: {@code { "json": "...", "actions": ["minify", "exclude-keys"], "keysToExclude": ["x"] }}
 * <p>
 * {@code keysToExclude} is used when an {@code exclude-keys} action appears (same list for each such step).
 */
public class TransformRequest {

    @NotBlank(message = "json is required")
    private String json;

    @NotEmpty(message = "actions is required")
    private List<String> actions;
    private List<String> keysToExclude;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getKeysToExclude() {
        return keysToExclude;
    }

    public void setKeysToExclude(List<String> keysToExclude) {
        this.keysToExclude = keysToExclude;
    }
}
