package pl.put.poznan.jsontools.service.decorator;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.service.JsonService;

import pl.put.poznan.jsontools.service.JsonService;

/**
 * A decorator that modifies a JSON tree by removing a specific set of keys.
 * 
 * This class traverses the JSON structure recursively. If it encounters a key 
 * that is present in the {@code keysToExclude} list, that key and its entire 
 * underlying structure (if it contains nested objects or arrays) are completely 
 * removed from the resulting JSON. All other unlisted keys are retained.
 *
 * @see JsonDecorator
 */
@Slf4j
public class ExcludeDecorator extends JsonDecorator {
    private final List<String> keysToExclude;

    /**
     * Constructs a new ExcludeDecorator.
     *
     * @param wrappedService the base JSON service or another decorator to wrap
     * @param keysToExclude a list of string keys that should be stripped from the JSON
     */
    public ExcludeDecorator(JsonService wrappedService, List<String> keysToExclude) {
        super(wrappedService);
        this.keysToExclude = keysToExclude;
    }

    /**
     * Processes the input JSON by first passing it to the wrapped service, 
     * and then recursively removing any keys present in the exclusion list.
     *
     * @param input the initial {@link JsonNode} to be processed
     * @return a new {@link JsonNode} with the excluded keys removed. 
     * Returns null if the input is null.
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
     * Recursively traverses and filters a JSON node, removing the specified keys.
     * 
     * For JSON objects, it iterates through the specified keys to remove them, 
     * and then recursively processes the remaining child nodes.
     * For JSON arrays, it iterates through and recursively processes every element.
     * Primitive value nodes (strings, numbers, booleans) are returned unmodified.
     *
     * @param node the current {@link JsonNode} being inspected and filtered
     * @param keys the collection of keys that must be removed
     * @return a processed {@link JsonNode} safely stripped of the targeted keys
     */
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
