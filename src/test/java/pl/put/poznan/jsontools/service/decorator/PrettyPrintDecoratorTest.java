package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.put.poznan.jsontools.service.JsonService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrettyPrintDecoratorTest {

    private ObjectMapper objectMapper;
    private JsonService mockService;
    private PrettyPrintDecorator prettyPrintDecorator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockService = mock(JsonService.class);
        prettyPrintDecorator = new PrettyPrintDecorator(mockService);
    }

    @Test
    void PrettyPrintWithDeterministicIndentation() throws Exception {
        String minifiedJson = "{\"name\":\"John\",\"surname\":\"Pork\",\"address\":{\"city\":\"Poznan\"}}";
        JsonNode inputNode = objectMapper.readTree(minifiedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        String result = prettyPrintDecorator.processToString(inputNode);

        String expected = """
{
  "name" : "John",
  "surname" : "Pork",
  "address" : {
    "city" : "Poznan"
  }
}""";

        assertEquals(expected, result);
    }

    @Test
    void NotChangeJsonShape() throws Exception {
        String minifiedJson = "{\"user\":{\"id\":1}}";
        JsonNode inputNode = objectMapper.readTree(minifiedJson);

        when(mockService.process(any(JsonNode.class))).thenReturn(inputNode);

        String result = prettyPrintDecorator.processToString(inputNode);

        assertEquals(
                inputNode,
                objectMapper.readTree(result),
                "Pretty printing should not change the JSON structure"
        );
    }

    @Test
    void InvalidJsonProcessingError() {
        when(mockService.process(any())).thenThrow(new IllegalArgumentException("Invalid JSON structure"));

        assertThrows(IllegalArgumentException.class, () -> {
            prettyPrintDecorator.processToString(null);
        });
    }
}