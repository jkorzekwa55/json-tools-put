package pl.put.poznan.jsontools.dto;

/**
 * Body for minify: {@code { "json": "..." }}.
 */
public class MinifyRequest {

    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
