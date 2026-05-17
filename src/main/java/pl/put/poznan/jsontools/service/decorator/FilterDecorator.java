package pl.put.poznan.jsontools.service.decorator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import pl.put.poznan.jsontools.service.JsonService;

/**
 * A decorator that filters a JSON tree, retaining only the keys specified in an allowed list.
 * 
 * This class traverses the JSON structure recursively. If a key is present in the 
 * {@code keysToKeep} list, it is retained along with its children. If a key is not 
 * in the list, it is entirely removed from the resulting JSON.
 * * @see JsonDecorator
 */
@Slf4j
public class FilterDecorator extends JsonDecorator {

    private final Set<String> keysToKeep;

    /**
     * Constructs a new FilterDecorator.
     *
     * @param wrappedService the base JSON service or another decorator to wrap
     * @param keysToKeep a list of string keys that should be retained in the JSON
     */
    public FilterDecorator(JsonService wrappedService, List<String> keysToKeep) {
        super(wrappedService);
        this.keysToKeep = Set.copyOf(keysToKeep);
    }

    /**
     * Processes the input JSON by first passing it to the wrapped service, 
     * and then recursively filtering out any keys not present in the keep list.
     *
     * @param input the initial JSON node to be processed
     * @return a new {@link JsonNode} containing only the allowed keys, 
     * or an empty JSON object if no keys matched. Returns null if the input is null.
     */
    @Override
    public JsonNode process(JsonNode input) {
        log.debug("Applying FilterDecorator with {} keys", keysToKeep.size());
        JsonNode processedNode = super.process(input);
        if (processedNode == null || processedNode.isNull()) {
            log.debug("Skipping FilterDecorator - processed JSON node is null");
            return processedNode;
        }
        return filterKeysRecursively(processedNode, keysToKeep);
    }
    /**
     * Recursively traverses and filters a JSON node, retaining only the allowed keys.
     * 
     * For JSON objects, it iterates through all fields, removing any key not present 
     * in the allowed list, and recursively filters the remaining child nodes.
     * For JSON arrays, it iterates through and recursively filters every element.
     * Primitive value nodes (strings, numbers, booleans) are returned unmodified.
     *
     * @param node the current {@link JsonNode} being inspected and filtered
     * @param keys the collection of allowed keys that should be retained
     * @return a processed {@link JsonNode} with unlisted keys removed
     */
    private JsonNode filterKeysRecursively(JsonNode node, Set<String> keys) {
        if (node.isObject()) {
            ObjectNode objectNode = node.deepCopy();

            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();

                if (!keys.contains(fieldName)) {
                    objectNode.remove(fieldName);
                } else {
                    JsonNode childNode = node.get(fieldName);
                    objectNode.set(fieldName, filterKeysRecursively(childNode, keys));
                }
            }
            return objectNode;

        } else if (node.isArray()) {
            ArrayNode arrayNode = node.deepCopy();
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode arrayElement = arrayNode.get(i);
                arrayNode.set(i, filterKeysRecursively(arrayElement, keys));
            }   
            return arrayNode;
        }
        return node;

    }
}
