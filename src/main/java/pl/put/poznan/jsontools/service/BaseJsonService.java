package pl.put.poznan.jsontools.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * Concrete component in the Decorator pattern: returns the input node unchanged.
 */
@Service
public class BaseJsonService implements JsonService {

    @Override
    public JsonNode process(JsonNode input) {
        // This is the base. It returns the node as-is.
        // Decorators will be wrapped around this class.
        return input;
    }
}
