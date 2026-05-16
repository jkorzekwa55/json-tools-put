package pl.put.poznan.jsontools.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.put.poznan.jsontools.app.JsonToolsApplication;
import pl.put.poznan.jsontools.dto.CompareRequest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JsonToolsApplication.class)
@AutoConfigureMockMvc
class JsonCompareAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNoDifferencesForIdenticalTexts() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setLeft("line 1\nline 2\nline 3");
        request.setRight("line 1\nline 2\nline 3");

        mockMvc.perform(post("/api/json/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.differences", hasSize(0)));
    }

    @Test
    void shouldReturnOnlyChangedLineForPartialDifferences() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setLeft("line 1\nline 2\nline 3");
        request.setRight("line 1\nLINE 2\nline 3");

        mockMvc.perform(post("/api/json/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.differences", hasSize(1)))
                .andExpect(jsonPath("$.differences[0].line").value(2))
                .andExpect(jsonPath("$.differences[0].left").value("line 2"))
                .andExpect(jsonPath("$.differences[0].right").value("LINE 2"));
    }

    @Test
    void shouldMarkMissingLinesAsDifferencesWhenTextsHaveDifferentLengths() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setLeft("line 1\nline 2\nline 3");
        request.setRight("line 1\nline 2");

        mockMvc.perform(post("/api/json/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.differences", hasSize(1)))
                .andExpect(jsonPath("$.differences[0].line").value(3))
                .andExpect(jsonPath("$.differences[0].left").value("line 3"))
                .andExpect(jsonPath("$.differences[0].right").value(""));
    }

    @Test
    void shouldReturnErrorPayloadWhenCompareRequestIsInvalid() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setLeft("line 1");

        mockMvc.perform(post("/api/json/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("right is required"));
    }

    @Test
    void shouldReturnErrorPayloadWhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/json/compare")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request body is required"));
    }
}
