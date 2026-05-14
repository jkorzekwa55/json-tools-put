package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.put.poznan.jsontools.service.JsonService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcludeDecoratorTest {

    private ObjectMapper objectMapper;
    private JsonService mockService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockService = mock(JsonService.class);
    }

    @Test
    void testExcludeKeysFromRootLevel() throws Exception {
        String jsonText = "{\"name\":\"Alice\",\"password\":\"secret123\",\"age\":25}";
        JsonNode inputNode = objectMapper.readTree(jsonText);
        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        List<String> keysToExclude = Arrays.asList("password", "age");
        ExcludeDecorator decorator = new ExcludeDecorator(mockService, keysToExclude);

        JsonNode resultNode = decorator.process(inputNode);

        assertTrue(resultNode.has("name"), "Name property should remain in the object");
        assertFalse(resultNode.has("password"), "Password property should be removed");
        assertFalse(resultNode.has("age"), "Age property should be removed");
        assertEquals(1, resultNode.size(), "Only one property should be left in the root object");
    }

    @Test
    void testExcludeKeysRecursively() throws Exception {

        String jsonText = "{\"id\":1,\"user\":{\"id\":2,\"name\":\"Bob\"},\"metadata\":{\"info\":\"test\",\"id\":3}}";
        JsonNode inputNode = objectMapper.readTree(jsonText);
        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        List<String> keysToExclude = Arrays.asList("id");
        ExcludeDecorator decorator = new ExcludeDecorator(mockService, keysToExclude);

        JsonNode resultNode = decorator.process(inputNode);

        assertFalse(resultNode.has("id"), "Root id should be removed");

        assertTrue(resultNode.has("user"), "User object should remain");
        assertFalse(resultNode.get("user").has("id"), "Nested user id should be removed");
        assertTrue(resultNode.get("user").has("name"), "Nested user name should remain");

        assertTrue(resultNode.has("metadata"), "Metadata object should remain");
        assertFalse(resultNode.get("metadata").has("id"), "Nested metadata id should be removed");
    }

    @Test
    void testExcludeKeysInsideArrays() throws Exception {

        String jsonText = "{\"users\":[{\"name\":\"Alice\",\"token\":\"abc\"},{\"name\":\"Bob\",\"token\":\"xyz\"}]}";
        JsonNode inputNode = objectMapper.readTree(jsonText);
        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);


        List<String> keysToExclude = Arrays.asList("token");
        ExcludeDecorator decorator = new ExcludeDecorator(mockService, keysToExclude);


        JsonNode resultNode = decorator.process(inputNode);

        JsonNode usersArray = resultNode.get("users");
        assertTrue(usersArray.isArray(), "The users field should still be an array");
        assertEquals(2, usersArray.size(), "Array size should not change");

        assertFalse(usersArray.get(0).has("token"), "Token should be removed from the first element");
        assertTrue(usersArray.get(0).has("name"), "Name should remain in the first element");

        assertFalse(usersArray.get(1).has("token"), "Token should be removed from the second element");
    }

    @Test
    void testNullHandling() {
        when(mockService.process(any())).thenReturn(null);
        List<String> keysToExclude = Arrays.asList("key");
        ExcludeDecorator decorator = new ExcludeDecorator(mockService, keysToExclude);

        JsonNode resultNode = decorator.process(null);

        assertNull(resultNode, "The decorator should return null safely when input is null");
    }

    @Test
    void testInvalidJsonHandling() {
        when(mockService.process(any())).thenThrow(new IllegalArgumentException("Invalid JSON structure"));

        List<String> keysToExclude = Arrays.asList("password");
        ExcludeDecorator decorator = new ExcludeDecorator(mockService, keysToExclude);

        assertThrows(IllegalArgumentException.class, () -> {
            decorator.process(null);
        }, "The decorator should propagate exceptions for invalid JSON");
    }
}