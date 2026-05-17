package pl.put.poznan.jsontools.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.put.poznan.jsontools.app.JsonToolsApplication;
import pl.put.poznan.jsontools.dto.TransformRequest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JsonToolsApplication.class)
@AutoConfigureMockMvc
class JsonTransformAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldTransformMinifyAndExcludeKeys() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setJson("{\n  \"name\" : \"Alice\",\n  \"password\" : \"secret\",\n  \"details\" : {\n    \"city\" : \"Poznan\"\n  }\n}");
        request.setActions(java.util.Arrays.asList("minify", "exclude-keys"));
        request.setKeysToExclude(java.util.Collections.singletonList("password"));

        mockMvc.perform(post("/api/json/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Alice\",\"details\":{\"city\":\"Poznan\"}}"));
    }

    @Test
    void shouldFilterUsingDedicatedFilterKeysEndpoint() throws Exception {
        String requestBody = "{\"json\":\"{\\\"name\\\":\\\"Alice\\\",\\\"password\\\":\\\"secret\\\",\\\"details\\\":{\\\"city\\\":\\\"Poznan\\\"}}\",\"keysToKeep\":[\"name\",\"details\",\"city\"]}";

        mockMvc.perform(post("/api/json/filter-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Alice\",\"details\":{\"city\":\"Poznan\"}}"));
    }

    @Test
    void shouldTransformUsingFilterAction() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setJson("{\"name\":\"Alice\",\"password\":\"secret\",\"details\":{\"city\":\"Poznan\"}}");
        request.setActions(java.util.Collections.singletonList("filter"));
        request.setKeysToKeep(java.util.Arrays.asList("name", "details", "city"));

        mockMvc.perform(post("/api/json/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Alice\",\"details\":{\"city\":\"Poznan\"}}"));
    }

    @Test
    void shouldPrettyPrintWhenRequested() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setJson("{\"name\":\"Alice\",\"details\":{\"city\":\"Poznan\"}}");
        request.setActions(java.util.Collections.singletonList("pretty-print"));

        mockMvc.perform(post("/api/json/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\n  \"name\"")))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.details.city").value("Poznan"));
    }
}
