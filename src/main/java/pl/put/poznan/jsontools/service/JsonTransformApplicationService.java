package pl.put.poznan.jsontools.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.put.poznan.jsontools.dto.TransformRequest;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.decorator.ExcludeDecorator;
import pl.put.poznan.jsontools.service.decorator.MinifyDecorator;
import pl.put.poznan.jsontools.service.decorator.PrettyPrintDecorator;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Builds decorator pipelines and runs transformations (parsing + {@link JsonService} chain).
 * Keeps HTTP adapters thin and matches the sprint task of a service layer that invokes transformations.
 */
@Service
public class JsonTransformApplicationService {

    private final BaseJsonService baseJsonService;
    private final ObjectMapper objectMapper;

    public JsonTransformApplicationService(BaseJsonService baseJsonService, ObjectMapper objectMapper) {
        this.baseJsonService = baseJsonService;
        this.objectMapper = objectMapper;
    }

    public JsonNode minify(String jsonText) {
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new MinifyDecorator(baseJsonService);
        return pipeline.process(input);
    }

    public JsonNode excludeKeys(String jsonText, List<String> keysToExclude) {
        requireJsonText(jsonText);
        List<String> keys = keysToExclude != null ? keysToExclude : Collections.emptyList();
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new ExcludeDecorator(baseJsonService, keys);
        return pipeline.process(input);
    }

    public String prettyPrint(String jsonText) {
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        PrettyPrintDecorator pipeline = new PrettyPrintDecorator(baseJsonService);
        return pipeline.processToString(input);
    }

    /**
     * Applies {@link TransformRequest#getActions()} in order: first action is innermost (closest to base),
     * last is outermost (runs last on the data flowing through the chain).
     */
    public JsonNode transform(TransformRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        requireJsonText(request.getJson());
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new IllegalArgumentException("Field \"actions\" must not be empty");
        }

        JsonNode input = parseJson(request.getJson());
        List<String> keys = request.getKeysToExclude() != null
                ? request.getKeysToExclude()
                : Collections.emptyList();

        JsonService pipeline = baseJsonService;
        for (String rawAction : request.getActions()) {
            if (rawAction == null || rawAction.isBlank()) {
                throw new IllegalArgumentException("Actions must not be null or blank");
            }
            String action = rawAction.trim().toLowerCase(Locale.ROOT);
            switch (action) {
                case "minify":
                    pipeline = new MinifyDecorator(pipeline);
                    break;
                case "exclude_keys":
                    pipeline = new ExcludeDecorator(pipeline, keys);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + rawAction);
            }
        }
        return pipeline.process(input);
    }

    private static void requireJsonText(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Field \"json\" is required and must not be blank");
        }
    }

    private JsonNode parseJson(String jsonText) {
        try {
            return objectMapper.readTree(jsonText);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }
}
