package pl.put.poznan.jsontools.dto;

import java.util.List;

/**
 * Body for excluding keys: {@code { "json": "...", "keysToExclude": ["a", "b"] }}.
 */
public class ExcludeKeysRequest {

    private String json;
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
