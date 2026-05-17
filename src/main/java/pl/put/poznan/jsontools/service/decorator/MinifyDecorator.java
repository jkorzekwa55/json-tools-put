package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.service.JsonService;

@Slf4j
public class MinifyDecorator extends JsonDecorator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MinifyDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    @Override
    public JsonNode process(JsonNode input) {
        log.debug("Applying Minification");

        JsonNode processedNode = super.process(input);

        try {
            String minifiedText = objectMapper.writeValueAsString(processedNode);
            return objectMapper.readTree(minifiedText);
        } catch (Exception e) {
            log.debug("Failed to apply MinifyDecorator", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
