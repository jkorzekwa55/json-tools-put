package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.JsonService;
import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.JsonService;
/**
 * A decorator that formats a JSON tree into a human-readable string.
 * 
 * Unlike other decorators that structurally modify the {@link JsonNode}, 
 * this decorator focuses on serialization. It uses Jackson's {@link DefaultPrettyPrinter}
 * configured with a 2-space indentation and newline characters to output 
 * a cleanly formatted, highly readable JSON string.
 *
 * @see JsonDecorator
 */
@Slf4j
public class PrettyPrintDecorator extends JsonDecorator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a new PrettyPrintDecorator.
     *
     * @param wrappedService the base JSON service or another decorator to wrap
     */
    public PrettyPrintDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    /**
     * Processes the input JSON by passing it to the wrapped service.
     * 
     * Note: This method returns the raw {@link JsonNode} without formatting, 
     * as pretty-printing is a string serialization process. To get the 
     * formatted string, use {@link #processToString(JsonNode)}.
     *
     * @param input the initial {@link JsonNode} to be processed
     * @return the structurally processed {@link JsonNode}
     */
    @Override
    public JsonNode process(JsonNode input) {
        return super.process(input);
    }

    /**
     * Processes the input JSON through the wrapped service and then serializes 
     * the result into a beautifully formatted string.
     * 
     * Objects and arrays are indented using two spaces per level.
     *
     * @param input the initial {@link JsonNode} to be processed
     * @return a formatted, human-readable JSON string
     * @throws InvalidJsonException if an error occurs during the Jackson 
     * string serialization process
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
