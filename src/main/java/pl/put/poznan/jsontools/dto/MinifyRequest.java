package pl.put.poznan.jsontools.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Body for minify: {@code { "json": "..." }}.
 */
@Data
@NoArgsConstructor
public class MinifyRequest {

    @NotBlank(message = "json is required")
    private String json;
}
