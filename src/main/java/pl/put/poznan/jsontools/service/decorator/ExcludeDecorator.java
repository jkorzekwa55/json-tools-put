package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.service.JsonService;

import java.util.List;

@Slf4j
public class ExcludeDecorator extends JsonDecorator {
    private final List<String> keysToExclude;

    public ExcludeDecorator(JsonService wrappedService, List<String> keysToExclude) {
        super(wrappedService);
        this.keysToExclude = keysToExclude;
    }

    @Override
    public JsonNode process(JsonNode input) {
        log.debug("Using ExcludeDecorator with {} keys", keysToExclude.size());

        JsonNode processedNode = super.process(input);

        if (processedNode == null || processedNode.isNull()) {
            log.debug("Skipping ExcludeDecorator - processed JSON node is null");
            return processedNode;
        }

        return removeKeysRecursively(processedNode, keysToExclude);
    }


    private JsonNode removeKeysRecursively(JsonNode node, List<String> keys) {
        if (node.isObject()) {

            ObjectNode objectNode = node.deepCopy();


            for (String key : keys) {
                objectNode.remove(key);
            }


            objectNode.fields().forEachRemaining(entry ->
                    entry.setValue(removeKeysRecursively(entry.getValue(), keys))
            );
            return objectNode;

        } else if (node.isArray()) {

            ArrayNode arrayNode = node.deepCopy();
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayNode.set(i, removeKeysRecursively(arrayNode.get(i), keys));
            }
            return arrayNode;
        }

        return node;
    }
}
