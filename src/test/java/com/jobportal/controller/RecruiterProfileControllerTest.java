package com.jobportal.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.service.RecruiterService;

@SpringBootTest
@AutoConfigureMockMvc
class RecruiterProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecruiterService recruiterService;

    @Test
    void shouldReturnRecruiterProfile() throws Exception {

        RecruiterProfileResponse response = new RecruiterProfileResponse();
        response.setName("John Recruiter");
        response.setEmail("john@test.com");

        when(recruiterService.getRecruiterProfile("recruiter@test.com"))
                .thenReturn(response);

        mockMvc.perform(get("/api/recruiter/profile")
                .with(user("recruiter@test.com").roles("RECRUITER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Recruiter"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }
}