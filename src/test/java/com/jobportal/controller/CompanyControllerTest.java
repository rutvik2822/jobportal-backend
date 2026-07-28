package com.jobportal.controller;

import java.util.List;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.company.CompanyRequest;
import com.jobportal.dto.company.CompanyResponse;
import com.jobportal.service.CompanyService;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CompanyService companyService;

    @Test
    void shouldCreateCompany() throws Exception {

        CompanyRequest request = new CompanyRequest();
        request.setCompanyName("Google");
        request.setWebsite("https://google.com");
        request.setLocation("Pune");
        request.setDescription("Tech Company");

        CompanyResponse response = new CompanyResponse();
        response.setId(1L);
        response.setCompanyName("Google");

        when(companyService.createCompany(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/companies")
                .with(user("admin").roles("SUPER_ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Google"));
    }

    @Test
    void shouldReturnAllCompanies() throws Exception {

        CompanyResponse response = new CompanyResponse();
        response.setId(1L);
        response.setCompanyName("Google");

        when(companyService.getAllCompanies())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/companies")
                .with(user("admin").roles("SUPER_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Google"));
    }

    @Test
    void shouldReturnCompanyById() throws Exception {

        CompanyResponse response = new CompanyResponse();
        response.setId(1L);
        response.setCompanyName("Google");

        when(companyService.getCompanyById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/companies/1")
                .with(user("admin").roles("SUPER_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Google"));
    }
}