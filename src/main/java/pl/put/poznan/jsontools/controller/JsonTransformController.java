package pl.put.poznan.jsontools.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.jsontools.dto.ExcludeKeysRequest;
import pl.put.poznan.jsontools.dto.FilterKeysRequest;
import pl.put.poznan.jsontools.dto.MinifyRequest;
import pl.put.poznan.jsontools.dto.TransformRequest;
import pl.put.poznan.jsontools.service.JsonTransformApplicationService;

import java.util.Collection;

/** REST API for JSON transformations (minify, filter, exclude, pretty-print, combined pipeline). */
@Slf4j
@RestController
@RequestMapping(path = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JsonTransformController {

    private final JsonTransformApplicationService transformApplicationService;

    /**
     * Convenience endpoint for a single operation (same behavior as {@code actions: ["minify"]} on {@link #transform}).
     */
    @PostMapping(path = "/minify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String minify(@Valid @RequestBody MinifyRequest request) {
        log.info("Received minify request");
        log.debug("Minify request JSON length - {}",  lengthOf(request.getJson()));
        return transformApplicationService.minify(request.getJson());
    }

    /**
     * Convenience endpoint for exclude-keys (same as including {@code exclude-keys} in {@link #transform}).
     */
    @PostMapping(path = "/exclude-keys", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode excludeKeys(@Valid @RequestBody ExcludeKeysRequest request) {
        log.info("Received exclude-keys request");
        log.debug("Exclude-keys request JSON length - {}, keys count - {}", request.getJson(), request.getKeysToExclude());
        return transformApplicationService.excludeKeys(request.getJson(), request.getKeysToExclude());
    }

    /**
     * Convenience endpoint for keep-keys filtering (same as including {@code filter} in {@link #transform}).
     */
    @PostMapping(path = "/filter-keys", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode filterKeys(@Valid @RequestBody FilterKeysRequest request) {
        log.info("Received filter-keys request");
        log.debug("Filter-keys request JSON length - {}, keys count - {}", lengthOf(request.getJson()), sizeOf(request.getKeysToKeep()));
        return transformApplicationService.filterKeys(request.getJson(), request.getKeysToKeep());
    }

    /**
     * Endpoint that runs any implemented transformation sequence and returns the final JSON text.
     */
    @PostMapping(path = "/transform", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String transform(@Valid @RequestBody TransformRequest request) {
        log.info("Received transform request");
        log.debug("Transform request JSON length - {}, actions count - {}, keysToExclude count - {}, keysToKeep count - {}",
                lengthOf(request.getJson()), sizeOf(request.getActions()), sizeOf(request.getKeysToExclude()), sizeOf(request.getKeysToKeep()));
        return transformApplicationService.transform(request);
    }

    /**
     * Convenience endpoint for pretty-printing JSON into a readable format.
     */
    @PostMapping(path = "/pretty-print", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prettyPrint(@Valid @RequestBody MinifyRequest request) {
        log.info("Received pretty-print request");
        log.debug("Pretty-print request JSON length: {}", lengthOf(request.getJson()));
        return transformApplicationService.prettyPrint(request.getJson());
    }

    private static int lengthOf(String text) {
        return text != null ? text.length() : 0;
    }

    private static int sizeOf(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }
}
