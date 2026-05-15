package pl.put.poznan.jsontools.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.put.poznan.jsontools.dto.ExcludeKeysRequest;
import pl.put.poznan.jsontools.dto.MinifyRequest;
import pl.put.poznan.jsontools.dto.TransformRequest;
import pl.put.poznan.jsontools.service.JsonTransformApplicationService;

/**
 * {@link pl.put.poznan.jsontools.exception.JsonTransformExceptionHandler} maps domain errors to 400 responses with {@link pl.put.poznan.jsontools.exception.ErrorResponse}.
 */
@RestController
@RequestMapping(path = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsonTransformController {

    private final JsonTransformApplicationService transformApplicationService;

    public JsonTransformController(JsonTransformApplicationService transformApplicationService) {
        this.transformApplicationService = transformApplicationService;
    }

    /**
     * Convenience endpoint for a single operation (same behavior as {@code actions: ["minify"]} on {@link #transform}).
     */
    @PostMapping(path = "/minify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode minify(@RequestBody MinifyRequest request) {
        requireRequestBody(request);
        return transformApplicationService.minify(request.getJson());
    }

    /**
     * Convenience endpoint for exclude-keys (same as including {@code exclude-keys} in {@link #transform}).
     */
    @PostMapping(path = "/exclude-keys", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode excludeKeys(@RequestBody ExcludeKeysRequest request) {
        requireRequestBody(request);
        return transformApplicationService.excludeKeys(request.getJson(), request.getKeysToExclude());
    }

    /**
     * Endpoint that run any implemented transformation sequence
     */
    @PostMapping(path = "/transform", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode transform(@RequestBody TransformRequest request) {
        requireRequestBody(request);
        return transformApplicationService.transform(request);
    }

    /**
     * Convenience endpoint for pretty-printing JSON into a readable format.
     */
    @PostMapping(path = "/pretty-print", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prettyPrint(@RequestBody MinifyRequest request) {
        requireRequestBody(request);
        return transformApplicationService.prettyPrint(request.getJson());
    }

    private static void requireRequestBody(Object request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
    }
}
