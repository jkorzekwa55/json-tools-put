package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.poznan.jsontools.service.JsonService;

/**
 * A decorator that processes a JSON tree to ensure a minified (compact) structure.
 * 
 * This class ensures minification by serializing the processed JSON node into a 
 * compact string format (which natively removes all unnecessary whitespace, newlines, 
 * and indentation) and then parsing it back into a {@link JsonNode}.
 * @see JsonDecorator
 */
public class MinifyDecorator extends JsonDecorator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a new MinifyDecorator.
     *
     * @param wrappedService the base JSON service or another decorator to wrap
     */
    public MinifyDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    /**
     * Processes the input JSON by first passing it to the wrapped service, 
     * and then minifying the result by string serialization.
     *
     * @param input the initial {@link JsonNode} to be processed
     * @return a new {@link JsonNode} representing the minified JSON structure
     * @throws IllegalArgumentException if an error occurs during the Jackson serialization 
     * or deserialization process
     */
    @Override
    public JsonNode process(JsonNode input) {

        JsonNode processedNode = super.process(input);

        try {
            String minifiedText = objectMapper.writeValueAsString(processedNode);
            return objectMapper.readTree(minifiedText);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
