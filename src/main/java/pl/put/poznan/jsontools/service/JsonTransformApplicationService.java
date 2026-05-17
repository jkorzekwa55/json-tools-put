package pl.put.poznan.jsontools.service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.poznan.jsontools.dto.TransformRequest;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.decorator.ExcludeDecorator;
import pl.put.poznan.jsontools.service.decorator.FilterDecorator;
import pl.put.poznan.jsontools.service.decorator.MinifyDecorator;
import pl.put.poznan.jsontools.service.decorator.PrettyPrintDecorator;

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

    public String minify(String jsonText) {
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new MinifyDecorator(baseJsonService);
        JsonNode processedNode = pipeline.process(input);
        try {
            return objectMapper.writeValueAsString(processedNode);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    public JsonNode excludeKeys(String jsonText, List<String> keysToExclude) {
        requireJsonText(jsonText);
        List<String> keys = keysToExclude != null ? keysToExclude : Collections.emptyList();
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new ExcludeDecorator(baseJsonService, keys);
        return pipeline.process(input);
    }

    public JsonNode filterKeys(String jsonText, List<String> keysToKeep) {
        requireJsonText(jsonText);
        List<String> keys = keysToKeep != null ? keysToKeep : Collections.emptyList();
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new FilterDecorator(baseJsonService, keys);
        return pipeline.process(input);
    }

    public String prettyPrint(String jsonText) {
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        PrettyPrintDecorator pipeline = new PrettyPrintDecorator(baseJsonService);
        return pipeline.processToString(input);
    }

    /**
     * Applies {@link TransformRequest#actions} in order: first action is innermost (closest to base),
     * last is outermost (runs last on the data flowing through the chain).
     * @param request the request containing the JSON and the list of actions to apply
     * @return the transformed JSON string as defined by the requested output format
     */
    public String transform(TransformRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        requireJsonText(request.getJson());
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new IllegalArgumentException("Field \"actions\" must not be empty");
        }

        JsonNode input = parseJson(request.getJson());
        List<String> keysToExclude = request.getKeysToExclude() != null
                ? request.getKeysToExclude()
                : Collections.emptyList();
        List<String> keysToKeep = request.getKeysToKeep() != null
                ? request.getKeysToKeep()
                : Collections.emptyList();

        JsonService pipeline = baseJsonService;
        OutputFormat outputFormat = OutputFormat.COMPACT;
        for (String rawAction : request.getActions()) {
            if (rawAction == null || rawAction.isBlank()) {
                throw new IllegalArgumentException("Actions must not be null or blank");
            }
            String action = normalizeAction(rawAction);
            switch (action) {
                case "minify":
                    pipeline = new MinifyDecorator(pipeline);
                    outputFormat = OutputFormat.COMPACT;
                    break;
                case "exclude_keys":
                    pipeline = new ExcludeDecorator(pipeline, keysToExclude);
                    break;
                case "filter":
                case "keep_keys":
                    pipeline = new FilterDecorator(pipeline, keysToKeep);
                    break;
                case "pretty_print":
                    outputFormat = OutputFormat.PRETTY;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + rawAction);
            }
        }

        if (outputFormat == OutputFormat.PRETTY) {
            PrettyPrintDecorator prettyPipeline = new PrettyPrintDecorator(pipeline);
            return prettyPipeline.processToString(input);
        }

        JsonNode processedNode = pipeline.process(input);
        try {
            return objectMapper.writeValueAsString(processedNode);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    private static String normalizeAction(String rawAction) {
        return rawAction.trim().toLowerCase(Locale.ROOT).replace('-', '_');
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

    private enum OutputFormat {
        COMPACT,
        PRETTY
    }
}
