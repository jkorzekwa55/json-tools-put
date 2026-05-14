package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.put.poznan.jsontools.service.JsonService;

public class MinifyDecorator extends JsonDecorator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MinifyDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

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
