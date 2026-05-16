package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.put.poznan.jsontools.service.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinifyDecorator extends JsonDecorator {

    private static final Logger logger = LoggerFactory.getLogger(MinifyDecorator.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MinifyDecorator(JsonService wrappedService) {
        super(wrappedService);
    }

    @Override
    public JsonNode process(JsonNode input) {

        logger.debug("Applying Minification");

        JsonNode processedNode = super.process(input);

        try {
            String minifiedText = objectMapper.writeValueAsString(processedNode);
            return objectMapper.readTree(minifiedText);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
