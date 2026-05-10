package pl.put.poznan.jsontools.dto.request;

import java.util.List;

/**
 * Request body: {@code { "json": "...", "actions": ["minify", "filter"] }}
 */
public class TransformRequest {

    private String json;
    private List<String> actions;

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
}
