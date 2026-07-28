package com.jobportal.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.application.ApplicationStatusRequest;
import com.jobportal.dto.recruiter.RecruiterDashboardResponse;
import com.jobportal.service.ApplicationService;

@SpringBootTest
@AutoConfigureMockMvc
class RecruiterApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ApplicationService applicationService;

    @Test
    void shouldReturnRecruiterApplications() throws Exception {

        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);

        when(applicationService.getApplicationsForRecruiter("recruiter@test.com"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/recruiter/applications")
                .with(user("recruiter@test.com").roles("RECRUITER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldReturnRecruiterDashboard() throws Exception {

        RecruiterDashboardResponse response = new RecruiterDashboardResponse();
        response.setTotalJobs(5);
        response.setTotalApplications(20);

        when(applicationService.getRecruiterDashboard("recruiter@test.com"))
                .thenReturn(response);

        mockMvc.perform(get("/api/recruiter/dashboard")
                .with(user("recruiter@test.com").roles("RECRUITER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs").value(5))
                .andExpect(jsonPath("$.totalApplications").value(20));
    }

    @Test
    void shouldUpdateApplicationStatus() throws Exception {

        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("ACCEPTED");

        doNothing().when(applicationService)
                .updateStatusByRecruiter(
                        eq(1L),
                        eq("ACCEPTED"),
                        eq("recruiter@test.com"));

        mockMvc.perform(put("/api/recruiter/applications/1/status")
                .with(user("recruiter@test.com").roles("RECRUITER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Application status updated successfully."));
    }
}