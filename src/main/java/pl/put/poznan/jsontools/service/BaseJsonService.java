package pl.put.poznan.jsontools.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class BaseJsonService implements JsonService {
    @Override
    public JsonNode process(JsonNode input) {
        // This is the base. It returns the node as-is.
        // Decorators will be wrapped around this class.
        return input;
    }
}
