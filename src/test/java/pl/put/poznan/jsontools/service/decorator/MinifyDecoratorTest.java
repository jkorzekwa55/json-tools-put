package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.put.poznan.jsontools.service.JsonService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinifyDecoratorTest {

    private ObjectMapper objectMapper;
    private JsonService mockService;
    private MinifyDecorator minifyDecorator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockService = mock(JsonService.class);
        minifyDecorator = new MinifyDecorator(mockService);
    }

    @Test
    void testOneLineOutputAndSpacesRemoved() throws Exception {
        String prettyJson = "{\n  \"name\" : \"John\",\n  \"age\" : 30\n}";
        JsonNode inputNode = objectMapper.readTree(prettyJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        JsonNode resultNode = minifyDecorator.process(inputNode);
        String resultText = objectMapper.writeValueAsString(resultNode);

        String expectedText = "{\"name\":\"John\",\"age\":30}";
        assertEquals(expectedText, resultText, "JSON was not properly minified!");
        assertFalse(resultText.contains("\n"), "Output contains unexpected newline characters!");
        assertFalse(resultText.contains("\r"), "Output contains unexpected carriage return characters!");
    }

    @Test
    void testShapeUnchanged() throws Exception {
        String jsonText = "{\"user\":{\"id\":1},\"roles\":[\"ADMIN\",\"USER\"]}";
        JsonNode inputNode = objectMapper.readTree(jsonText);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        JsonNode resultNode = minifyDecorator.process(inputNode);

        assertEquals(inputNode, resultNode, "The data structure shape has been altered!");
        assertEquals(1, resultNode.get("user").get("id").asInt(), "Nested values were corrupted!");
        assertEquals("ADMIN", resultNode.get("roles").get(0).asText(), "Array data was corrupted!");
    }

    @Test
    void testInvalidJsonHandling() {
        when(mockService.process(any())).thenThrow(new IllegalArgumentException("Invalid JSON structure"));

        assertThrows(IllegalArgumentException.class, () -> {
            minifyDecorator.process(null);
        }, "The decorator should throw an IllegalArgumentException for invalid inputs or states!");
    }
}