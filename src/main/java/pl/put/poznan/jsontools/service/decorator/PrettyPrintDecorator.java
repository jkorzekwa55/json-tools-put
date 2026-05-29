package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.JsonService;

/**
 * Decorator that pretty-prints JSON text. Use {@link #processToString} for indented output;
 * {@link #process} only runs the inner chain.
 */
@Slf4j
public class PrettyPrintDecorator extends JsonDecorator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PrettyPrintDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    @Override
    public JsonNode process(JsonNode input) {
        return super.process(input);
    }

    /**
     * Processes JSON and formats it in human-readable way
     *
     * @param input JSON node input
     * @return processed JSON with human-readable format
     * @throws InvalidJsonException when given JSON is invalid 
     */
    public String processToString(JsonNode input) {
        log.debug("Using PrettyPrintDecorator");

        JsonNode processedNode = super.process(input);

        try {
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\n"));
            prettyPrinter.indentArraysWith(new DefaultIndenter("  ", "\n"));

            return objectMapper.writer(prettyPrinter).writeValueAsString(processedNode);

        } catch (JsonProcessingException e) {
            log.debug("Failed to apply PrettyPrintDecorator", e);
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }
}
