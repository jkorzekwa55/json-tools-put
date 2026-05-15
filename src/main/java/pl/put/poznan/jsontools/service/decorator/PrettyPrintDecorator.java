package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.put.poznan.jsontools.exception.InvalidJsonException;
import pl.put.poznan.jsontools.service.JsonService;

public class PrettyPrintDecorator extends JsonDecorator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PrettyPrintDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    @Override
    public JsonNode process(JsonNode input) {
        return super.process(input);
    }

    public String processToString(JsonNode input) {
        JsonNode processedNode = super.process(input);

        try {
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\n"));
            prettyPrinter.indentArraysWith(new DefaultIndenter("  ", "\n"));

            return objectMapper
                    .writer(prettyPrinter)
                    .writeValueAsString(processedNode);

        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }
}
