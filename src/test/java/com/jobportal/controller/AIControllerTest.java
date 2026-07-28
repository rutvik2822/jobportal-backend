package com.jobportal.controller;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.service.OpenRouterService;
import com.jobportal.service.PdfService;

@SpringBootTest
@AutoConfigureMockMvc
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OpenRouterService openRouterService;

    @MockitoBean
    private PdfService pdfService;

    @Test
    void shouldAnalyzeJob() throws Exception {

        when(openRouterService.analyzeJob("Java Developer"))
                .thenReturn(Map.of("result", "Analysis Successful"));

        mockMvc.perform(post("/api/ai/analyze-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("job", "Java Developer"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Analysis Successful")));
    }

    @Test
    void shouldAnalyzeResume() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Dummy Resume".getBytes());

        when(pdfService.extractText(any()))
                .thenReturn("Resume Text");

        when(openRouterService.analyzeResume("Resume Text"))
                .thenReturn(Map.of(
                        "score", "90",
                        "message", "Resume analyzed successfully"));

        mockMvc.perform(multipart("/api/ai/analyze-resume")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("90")));
    }

    @Test
    void shouldCompareResumeWithJob() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Dummy Resume".getBytes());

        when(pdfService.extractText(any()))
                .thenReturn("Resume Text");

        when(openRouterService.compareResumeWithJob(
                "Resume Text",
                "Java Developer"))
                .thenReturn(Map.of(
                        "match", "95%",
                        "result", "Excellent Match"));

        mockMvc.perform(multipart("/api/ai/compare")
                        .file(file)
                        .param("job", "Java Developer"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("95%")));
    }
}