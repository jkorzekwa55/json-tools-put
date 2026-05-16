package pl.put.poznan.jsontools.dto;

import javax.validation.constraints.NotBlank;

/**
 * Body for minify: {@code { "json": "..." }}.
 */
public class MinifyRequest {

    @NotBlank(message = "json is required")
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
