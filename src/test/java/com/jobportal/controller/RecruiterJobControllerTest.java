package com.jobportal.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.job.JobRequest;
import com.jobportal.dto.job.JobResponse;
import com.jobportal.service.JobService;

@SpringBootTest
@AutoConfigureMockMvc
class RecruiterJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JobService jobService;

    @Test
    void shouldCreateJob() throws Exception {

        JobRequest request = new JobRequest();
        request.setTitle("Java Developer");

        JobResponse response = new JobResponse();
        response.setId(1L);
        response.setTitle("Java Developer");

        when(jobService.createJob(any(JobRequest.class), eq("recruiter@test.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/recruiter/jobs")
                .with(user("recruiter@test.com").roles("RECRUITER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Developer"));
    }

    @Test
    void shouldReturnRecruiterJobs() throws Exception {

        JobResponse response = new JobResponse();
        response.setId(1L);
        response.setTitle("Java Developer");

        when(jobService.getRecruiterJobs("recruiter@test.com"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/recruiter/jobs")
                .with(user("recruiter@test.com").roles("RECRUITER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Developer"));
    }

    @Test
    void shouldUpdateJob() throws Exception {

        JobRequest request = new JobRequest();
        request.setTitle("Senior Java Developer");

        JobResponse response = new JobResponse();
        response.setId(1L);
        response.setTitle("Senior Java Developer");

        when(jobService.updateJob(eq(1L), any(JobRequest.class), eq("recruiter@test.com")))
                .thenReturn(response);

        mockMvc.perform(put("/api/recruiter/jobs/1")
                .with(user("recruiter@test.com").roles("RECRUITER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Java Developer"));
    }

    @Test
    void shouldDeleteJob() throws Exception {

        when(jobService.deleteJob(1L, "recruiter@test.com"))
                .thenReturn("Job deleted successfully");

        mockMvc.perform(delete("/api/recruiter/jobs/1")
                .with(user("recruiter@test.com").roles("RECRUITER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Job deleted successfully"));
    }
}