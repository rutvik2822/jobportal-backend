package com.jobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.UserRepository;

class RecruiterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RecruiterService recruiterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

@Test
void shouldCreateRecruiterSuccessfully() {

    RecruiterRequest request = new RecruiterRequest();
    request.setName("Rutvik");
    request.setEmail("rutvik@test.com");
    request.setPassword("password123");
    request.setCompanyId(1L);

    Company company = new Company();
    company.setId(1L);
    company.setCompanyName("OpenAI");

    User recruiter = new User();
    recruiter.setId(1L);
    recruiter.setName("Rutvik");
    recruiter.setEmail("rutvik@test.com");
    recruiter.setCompany(company);

    when(userRepository.existsByEmail("rutvik@test.com"))
            .thenReturn(false);

    when(companyRepository.findById(1L))
            .thenReturn(Optional.of(company));

    when(passwordEncoder.encode("password123"))
            .thenReturn("encodedPassword");

    when(userRepository.save(any(User.class)))
            .thenReturn(recruiter);

    RecruiterResponse response =
            recruiterService.createRecruiter(request);

    assertNotNull(response);
    assertEquals("Rutvik", response.getName());
    assertEquals("rutvik@test.com", response.getEmail());
    assertEquals("OpenAI", response.getCompanyName());

    verify(userRepository).existsByEmail("rutvik@test.com");
    verify(companyRepository).findById(1L);
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
}

@Test
void shouldThrowExceptionWhenEmailAlreadyExists() {

    RecruiterRequest request = new RecruiterRequest();
    request.setEmail("rutvik@test.com");

    when(userRepository.existsByEmail("rutvik@test.com"))
            .thenReturn(true);

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> recruiterService.createRecruiter(request)
    );

    assertEquals("Email already exists", exception.getMessage());

    verify(userRepository).existsByEmail("rutvik@test.com");
    verify(companyRepository, never()).findById(any());
}

@Test
void shouldThrowExceptionWhenCompanyNotFound() {

    RecruiterRequest request = new RecruiterRequest();
    request.setEmail("rutvik@test.com");
    request.setCompanyId(1L);

    when(userRepository.existsByEmail("rutvik@test.com"))
            .thenReturn(false);

    when(companyRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> recruiterService.createRecruiter(request)
    );

    assertEquals("Company not found", exception.getMessage());

    verify(userRepository).existsByEmail("rutvik@test.com");
    verify(companyRepository).findById(1L);
    verify(userRepository, never()).save(any(User.class));
}

@Test
void shouldReturnRecruiterProfile() {

    Company company = new Company();
    company.setCompanyName("OpenAI");

    User recruiter = new User();
    recruiter.setId(1L);
    recruiter.setName("Rutvik");
    recruiter.setEmail("rutvik@test.com");
    recruiter.setRole(Role.RECRUITER);
    recruiter.setCompany(company);

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    RecruiterProfileResponse response =
            recruiterService.getRecruiterProfile("rutvik@test.com");

    assertNotNull(response);
    assertEquals("Rutvik", response.getName());
    assertEquals("rutvik@test.com", response.getEmail());
    assertEquals("RECRUITER", response.getRole());
    assertEquals("OpenAI", response.getCompanyName());

    verify(userRepository).findByEmail("rutvik@test.com");
}

@Test
void shouldThrowExceptionWhenRecruiterProfileNotFound() {

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> recruiterService.getRecruiterProfile("rutvik@test.com")
    );

    assertEquals("Recruiter not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
}
}