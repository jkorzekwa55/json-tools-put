package pl.put.poznan.jsontools.service.decorator;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.service.JsonService;


/**
 * Decorator that recursively removes selected keys from JSON tree.
 */
@Slf4j
public class ExcludeDecorator extends JsonDecorator {
    private final List<String> keysToExclude;
    /**
     * Modifies a JSON tree by removing specified keys and their children.
     */
    public ExcludeDecorator(JsonService wrappedService, List<String> keysToExclude) {
        super(wrappedService);
        this.keysToExclude = keysToExclude;
    }
    
    /**
     * Processes JSON and removes excluded keys recursively.
     * 
     * @param input : JSON node input
     * @return processed JSON without specified keys
     */
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

    /**
     * Recursively traverses a JSON and removes the specified keys.
     * 
     * @param node inout JSON node
     * @param keys keys to be excluded from JSON
     * @return processed JSON without specified keys
     */
    private JsonNode removeKeysRecursively(JsonNode node, List<String> keys) {
        if (node.isObject()) {
            // keys are iterated to remove from JSON and its elements proccessed recursively

            ObjectNode objectNode = node.deepCopy();
            for (String key : keys) {
                objectNode.remove(key);
            }
            objectNode.fields().forEachRemaining(entry ->
                    entry.setValue(removeKeysRecursively(entry.getValue(), keys))
            );
            return objectNode;

        } else if (node.isArray()) {
            // elements of JSON arrays are iterated and processed recursively

            ArrayNode arrayNode = node.deepCopy();
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayNode.set(i, removeKeysRecursively(arrayNode.get(i), keys));
            }
            return arrayNode;
        }

        return node;
    }
}
