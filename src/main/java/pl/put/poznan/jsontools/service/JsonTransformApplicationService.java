package pl.put.poznan.jsontools.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.put.poznan.jsontools.dto.TransformRequest;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.decorator.ExcludeDecorator;
import pl.put.poznan.jsontools.service.decorator.FilterDecorator;
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
@Slf4j
public class JsonTransformApplicationService {
    private final BaseJsonService baseJsonService;
    private final ObjectMapper objectMapper;

    public JsonTransformApplicationService(BaseJsonService baseJsonService, ObjectMapper objectMapper) {
        this.baseJsonService = baseJsonService;
        this.objectMapper = objectMapper;
    }

    public String minify(String jsonText) {
        log.info("Starting minify transformation");
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new MinifyDecorator(baseJsonService);
        JsonNode processedNode = pipeline.process(input);
        try {
            String result = objectMapper.writeValueAsString(processedNode);
            log.info("Finished minify transformation");
            return result;
        } catch (JsonProcessingException e) {
            log.debug("Failed to serialize minified JSON", e);
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    public JsonNode excludeKeys(String jsonText, List<String> keysToExclude) {
        log.info("Starting exclude-keys transformation");
        log.debug("ExcludeKeys input JSON length - {}, keys count - {}", lengthOf(jsonText), sizeOf(keysToExclude));
        requireJsonText(jsonText);
        List<String> keys = keysToExclude != null ? keysToExclude : Collections.emptyList();
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new ExcludeDecorator(baseJsonService, keys);
        log.info("Finished exclude-keys transformation");
        return pipeline.process(input);
    }

    public JsonNode filterKeys(String jsonText, List<String> keysToKeep) {
        log.info("Starting filter-keys transformation");
        log.debug("FilterKeys input JSON length - {}, keys count - {}", lengthOf(jsonText), sizeOf(keysToKeep));
        requireJsonText(jsonText);
        List<String> keys = keysToKeep != null ? keysToKeep : Collections.emptyList();
        JsonNode input = parseJson(jsonText);
        JsonService pipeline = new FilterDecorator(baseJsonService, keys);
        log.info("Finished filter-keys transformation");
        return pipeline.process(input);
    }

    public String prettyPrint(String jsonText) {
        log.info("Starting pretty-print transformation");
        log.debug("PrettyPrint input JSON length - {}", lengthOf(jsonText));
        requireJsonText(jsonText);
        JsonNode input = parseJson(jsonText);
        PrettyPrintDecorator pipeline = new PrettyPrintDecorator(baseJsonService);
        log.info("Finished pretty-print transformation");
        return pipeline.processToString(input);
    }

    /**
     * Applies {@link TransformRequest#getActions()} in order: first action is innermost (closest to base),
     * last is outermost (runs last on the data flowing through the chain).
     */
    public String transform(TransformRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        log.info("Starting Transformation pipeline");
        log.debug("Transform input JSON length - {}, actions count - {}", lengthOf(request.getJson()), sizeOf(request.getActions()));
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
            log.debug("Adding normalized action to Transformation pipeline - {}", action);
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
                    log.info("Unknown transformation action requested - {}", rawAction);
                    throw new IllegalArgumentException("Unknown action: " + rawAction);
            }
        }
        log.debug("Selected transformation output format - {}", outputFormat);

        if (outputFormat == OutputFormat.PRETTY) {
            PrettyPrintDecorator prettyPipeline = new PrettyPrintDecorator(pipeline);
            log.info("Finished transformation pipeline");
            return prettyPipeline.processToString(input);
        }

        JsonNode processedNode = pipeline.process(input);
        try {
            String result = objectMapper.writeValueAsString(processedNode);
            log.info("Finished transformation pipeline");
            return result;
        } catch (JsonProcessingException e) {
            log.debug("Failed to serialize transformed JSON", e);
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
            log.debug("Parsing JSON input");
            return objectMapper.readTree(jsonText);
        } catch (JsonProcessingException e) {
            log.debug("Failed to parse JSON input", e);
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    private enum OutputFormat {
        COMPACT,
        PRETTY
    }
    private static int lengthOf(String text) {
        return text != null ? text.length() : 0;
    }

    private static int sizeOf(List<?> list) {
        return list != null ? list.size() : 0;
    }
}
