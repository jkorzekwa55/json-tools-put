package pl.put.poznan.jsontools.service.decorator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.poznan.jsontools.service.JsonService;

class FilterDecoratorTest {
    
    private ObjectMapper mapper;
    private JsonService mockService;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mockService = mock(JsonService.class);
    }

    @Test
    void testFilterKeysFromRootLevel() throws Exception {
        String inputJson = "{\"id\": 1, \"name\": \"Alice\", \"secret\": \"hidden\"}";
        List<String> keysToKeep = Arrays.asList("id", "name");
        String expectedJson = "{\"id\": 1, \"name\": \"Alice\"}";

        JsonNode inputNode = mapper.readTree(inputJson);
        JsonNode expectedNode = mapper.readTree(expectedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);
        
        FilterDecorator decorator = new FilterDecorator(mockService, keysToKeep);

        JsonNode actualNode = decorator.process(inputNode);
        assertEquals(expectedNode, actualNode, "Should only keep 'id' and 'name' at the root level");
    }

    @Test
    void testFilterKeysRecursively() throws Exception {
        String inputJson = "{\"user\": {\"name\": \"Bob\", \"age\": 30, \"password\": \"123\"}, \"status\": \"active\"}";
        List<String> keysToKeep = Arrays.asList("user", "name");
        String expectedJson = "{\"user\": {\"name\": \"Bob\"}}";
        
        JsonNode inputNode = mapper.readTree(inputJson);
        JsonNode expectedNode = mapper.readTree(expectedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        FilterDecorator decorator = new FilterDecorator(mockService, keysToKeep);

        JsonNode actualNode = decorator.process(inputNode);
        assertEquals(expectedNode, actualNode, "Should keep 'user' and 'name', removing 'age', 'password', and 'status'");
    }

    @Test
    void testFilterKeysInsideArrays() throws Exception {
        String inputJson = "{\"store\": \"TechHub\", \"items\": [{\"id\": 1, \"price\": 100}, {\"id\": 2, \"price\": 200}]}";
        List<String> keysToKeep = Arrays.asList("items", "price");
        String expectedJson = "{\"items\": [{\"price\": 100}, {\"price\": 200}]}";
    
        JsonNode inputNode = mapper.readTree(inputJson);
        JsonNode expectedNode = mapper.readTree(expectedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);
        
        FilterDecorator decorator = new FilterDecorator(mockService, keysToKeep);

        JsonNode actualNode = decorator.process(inputNode);
        assertEquals(expectedNode, actualNode, "Should filter objects inside the 'items' array");
    }


    @Test
    void testNullHandling() {
        when(mockService.process(null)).thenReturn(null);
        when(mockService.process(mapper.nullNode())).thenReturn(mapper.nullNode());

        FilterDecorator decorator = new FilterDecorator(mockService, Arrays.asList("name"));

        JsonNode actualNodeNull = decorator.process(null);
        assertNull(actualNodeNull, "Should return null if input is null");

        JsonNode actualNodeNullNode = decorator.process(mapper.nullNode());
        assertTrue(actualNodeNullNode.isNull(), "Should return NullNode if input is NullNode");
    }

    @Test
    void testInvalidJsonHandling() {
        String malformedJson = "{\"name\": \"Alice\", \"age\": 25"; 
    
        assertThrows(JsonProcessingException.class, () -> {
            mapper.readTree(malformedJson);
        }, "ObjectMapper should throw JsonProcessingException on malformed JSON");
    }


    @Test
    void testEmptyKeepList() throws Exception {
        String inputJson = "{\"id\": 1, \"name\": \"Alice\"}";
        List<String> keysToKeep = Collections.emptyList();
        String expectedJson = "{}";

        JsonNode inputNode = mapper.readTree(inputJson);
        JsonNode expectedNode = mapper.readTree(expectedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        FilterDecorator decorator = new FilterDecorator(mockService, keysToKeep);

        JsonNode actualNode = decorator.process(inputNode);
        assertEquals(expectedNode, actualNode, "Should return an empty JSON object when keep list is empty");
    }
    @Test
    void testNoMatchingKeys() throws Exception {
        String inputJson = "{\"id\": 1, \"name\": \"Alice\"}";
        List<String> keysToKeep = Arrays.asList("address", "phone");
        String expectedJson = "{}";

        JsonNode inputNode = mapper.readTree(inputJson);
        JsonNode expectedNode = mapper.readTree(expectedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        FilterDecorator decorator = new FilterDecorator(mockService, keysToKeep);

        JsonNode actualNode = decorator.process(inputNode);
        assertEquals(expectedNode, actualNode, "Should return an empty JSON object when no keys match");
    }
    
}
