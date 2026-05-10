package pl.put.poznan.jsontools.service;


import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Component interface in the Decorator pattern.
 * Defines the method for JSON transformation.
 */
public interface JsonService {
    /**
     * Processes the input JSON node and returns a transformed version.
     * @param input The raw or partially processed JsonNode.
     * @return The transformed JsonNode.
     */
    JsonNode process(JsonNode input);
}
