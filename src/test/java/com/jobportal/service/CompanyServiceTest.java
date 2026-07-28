package com.jobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.jobportal.dto.company.CompanyRequest;
import com.jobportal.dto.company.CompanyResponse;
import com.jobportal.entity.Company;
import com.jobportal.repository.CompanyRepository;

import java.util.*;

class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void shouldCreateCompanySuccessfully() {
    // Arrange

    CompanyRequest request = new CompanyRequest();
    request.setCompanyName("OpenAI");
    request.setWebsite("https://openai.com");
    request.setLocation("San Francisco");
    request.setDescription("AI Company");
    request.setLogoUrl("logo.png");

    Company company = new Company();
    company.setId(1L);
    company.setCompanyName(request.getCompanyName());
    company.setWebsite(request.getWebsite());
    company.setLocation(request.getLocation());
    company.setDescription(request.getDescription());
    company.setLogoUrl(request.getLogoUrl());

    when(companyRepository.existsByCompanyName("OpenAI"))
            .thenReturn(false);

    when(companyRepository.save(any(Company.class)))
            .thenReturn(company);

    // Act

    CompanyResponse response = companyService.createCompany(request);

    // Assert

    assertNotNull(response);
    assertEquals("OpenAI", response.getCompanyName());

    verify(companyRepository).existsByCompanyName("OpenAI");
    verify(companyRepository).save(any(Company.class));
}

@Test
void shouldThrowExceptionWhenCompanyAlreadyExists() {

    // Arrange
    CompanyRequest request = new CompanyRequest();
    request.setCompanyName("OpenAI");

    when(companyRepository.existsByCompanyName("OpenAI"))
            .thenReturn(true);

    // Act & Assert
    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> companyService.createCompany(request)
    );

    assertEquals("Company already exists", exception.getMessage());

    verify(companyRepository).existsByCompanyName("OpenAI");
    verify(companyRepository, never()).save(any());
}

@Test
void shouldReturnAllCompanies() {

    Company company = new Company();
    company.setId(1L);
    company.setCompanyName("OpenAI");

    when(companyRepository.findAll())
            .thenReturn(List.of(company));

    List<CompanyResponse> response = companyService.getAllCompanies();

    assertEquals(1, response.size());
    assertEquals("OpenAI", response.get(0).getCompanyName());

    verify(companyRepository).findAll();
}

@Test
void shouldReturnEmptyCompanyList() {

    when(companyRepository.findAll())
            .thenReturn(List.of());

    List<CompanyResponse> response = companyService.getAllCompanies();

    assertTrue(response.isEmpty());

    verify(companyRepository).findAll();
}

@Test
void shouldReturnCompanyById() {

    Company company = new Company();
    company.setId(1L);
    company.setCompanyName("OpenAI");

    when(companyRepository.findById(1L))
            .thenReturn(Optional.of(company));

    CompanyResponse response = companyService.getCompanyById(1L);

    assertNotNull(response);
    assertEquals("OpenAI", response.getCompanyName());

    verify(companyRepository).findById(1L);
}

@Test
void shouldThrowExceptionWhenCompanyNotFound() {

    when(companyRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> companyService.getCompanyById(1L)
    );

    assertEquals("Company not found", exception.getMessage());

    verify(companyRepository).findById(1L);
}

@Test
void shouldUpdateCompanySuccessfully() {

    Company company = new Company();
    company.setId(1L);
    company.setCompanyName("Old Company");

    CompanyRequest request = new CompanyRequest();
    request.setCompanyName("New Company");

    when(companyRepository.findById(1L))
            .thenReturn(Optional.of(company));

    when(companyRepository.save(any(Company.class)))
            .thenReturn(company);

    CompanyResponse response = companyService.updateCompany(1L, request);

    assertEquals("New Company", response.getCompanyName());

    verify(companyRepository).findById(1L);
    verify(companyRepository).save(any(Company.class));
}

@Test
void shouldThrowExceptionWhenUpdatingMissingCompany() {

    CompanyRequest request = new CompanyRequest();

    when(companyRepository.findById(1L))
            .thenReturn(Optional.empty());

    assertThrows(
            RuntimeException.class,
            () -> companyService.updateCompany(1L, request)
    );

    verify(companyRepository).findById(1L);
}

@Test
void shouldDeleteCompanySuccessfully() {

    companyService.deleteCompany(1L);

    verify(companyRepository).deleteById(1L);
}
}