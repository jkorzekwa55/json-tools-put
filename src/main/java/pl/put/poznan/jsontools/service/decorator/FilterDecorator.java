package pl.put.poznan.jsontools.service.decorator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pl.put.poznan.jsontools.service.JsonService;


public class FilterDecorator extends JsonDecorator {

    private final Set<String> keysToKeep;

    public FilterDecorator(JsonService wrappedService, List<String> keysToKeep) {
        super(wrappedService);
        this.keysToKeep = Set.copyOf(keysToKeep);
    }

    @Override
    public JsonNode process(JsonNode input) {
        JsonNode processedNode = super.process(input);
        if (processedNode == null || processedNode.isNull()) {
            return processedNode;
        }
        return filterKeysRecursively(processedNode, keysToKeep);
    }


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
