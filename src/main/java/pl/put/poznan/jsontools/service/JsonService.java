package pl.put.poznan.jsontools.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Component in the Decorator pattern: transforms a {@link JsonNode}.
 */
public interface JsonService {

    /** Applies this step and returns the transformed node. */
    JsonNode process(JsonNode input);
}
