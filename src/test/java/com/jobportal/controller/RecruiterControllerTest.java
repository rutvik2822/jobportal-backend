package com.jobportal.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.service.RecruiterService;

@SpringBootTest
@AutoConfigureMockMvc
class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RecruiterService recruiterService;

    @Test
    void shouldCreateRecruiter() throws Exception {

        RecruiterRequest request = new RecruiterRequest();
        request.setName("John Recruiter");
        request.setEmail("john@test.com");
        request.setPassword("password123");
        request.setCompanyId(1L);

        RecruiterResponse response = new RecruiterResponse();
        response.setId(1L);
        response.setName("John Recruiter");
        response.setEmail("john@test.com");

        when(recruiterService.createRecruiter(any(RecruiterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/recruiters")
                .with(user("admin").roles("SUPER_ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Recruiter"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }
}