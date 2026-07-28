package com.jobportal.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.jobportal.dto.job.JobRequest;
import com.jobportal.dto.job.JobResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateJobSuccessfully() {

        // Arrange
        JobRequest request = new JobRequest();
        request.setTitle("Java Developer");
        request.setDescription("Backend Developer");
        request.setSkillsRequired("Java, Spring Boot");
        request.setLocation("Pune");
        request.setSalary("8 LPA");
        request.setJobType("Full Time");

        Company company = new Company();
        company.setCompanyName("OpenAI");

        User recruiter = new User();
        recruiter.setName("Rutvik");
        recruiter.setEmail("rutvik@test.com");
        recruiter.setCompany(company);

        Job job = new Job();
        job.setId(1L);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setRecruiter(recruiter);
        job.setCompany(company);

        when(userRepository.findByEmail("rutvik@test.com"))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.save(any(Job.class)))
                .thenReturn(job);

        // Act
        JobResponse response = jobService.createJob(request, "rutvik@test.com");

        // Assert
        assertNotNull(response);
        assertEquals("Java Developer", response.getTitle());
        assertEquals("OpenAI", response.getCompanyName());
        assertEquals("Rutvik", response.getRecruiterName());

        verify(userRepository).findByEmail("rutvik@test.com");
        verify(jobRepository).save(any(Job.class));
    }
    
   @Test
void shouldThrowExceptionWhenRecruiterNotFound() {

    // Arrange
    JobRequest request = new JobRequest();
    request.setTitle("Java Developer");

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.createJob(request, "rutvik@test.com")
    );

    assertEquals("Recruiter not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository, never()).save(any(Job.class));
}

@Test
void shouldReturnAllJobs() {

    // Arrange
    Company company = new Company();
    company.setCompanyName("OpenAI");

    User recruiter = new User();
    recruiter.setName("Rutvik");

    Job job = new Job();
    job.setId(1L);
    job.setTitle("Java Developer");
    job.setDescription("Backend Developer");
    job.setSkillsRequired("Java, Spring Boot");
    job.setLocation("Pune");
    job.setSalary("8 LPA");
    job.setJobType("Full Time");
    job.setRecruiter(recruiter);
    job.setCompany(company);

    when(jobRepository.findAll())
            .thenReturn(List.of(job));

    // Act
    List<JobResponse> response = jobService.getAllJobs();

    // Assert
    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals("Java Developer", response.get(0).getTitle());
    assertEquals("OpenAI", response.get(0).getCompanyName());

    verify(jobRepository).findAll();
}

@Test
void shouldReturnEmptyJobList() {

    // Arrange
    when(jobRepository.findAll())
            .thenReturn(List.of());

    // Act
    List<JobResponse> response = jobService.getAllJobs();

    // Assert
    assertNotNull(response);
    assertTrue(response.isEmpty());

    verify(jobRepository).findAll();
}

@Test
void shouldReturnRecruiterJobs() {

    Company company = new Company();
    company.setCompanyName("OpenAI");

    User recruiter = new User();
    recruiter.setName("Rutvik");
    recruiter.setEmail("rutvik@test.com");
    recruiter.setCompany(company);

    Job job = new Job();
    job.setId(1L);
    job.setTitle("Java Developer");
    job.setDescription("Backend Developer");
    job.setSkillsRequired("Java, Spring Boot");
    job.setLocation("Pune");
    job.setSalary("8 LPA");
    job.setJobType("Full Time");
    job.setRecruiter(recruiter);
    job.setCompany(company);

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.findByRecruiter(recruiter))
            .thenReturn(List.of(job));

    List<JobResponse> response =
            jobService.getRecruiterJobs("rutvik@test.com");

    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals("Java Developer", response.get(0).getTitle());
    assertEquals("OpenAI", response.get(0).getCompanyName());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository).findByRecruiter(recruiter);
}

@Test
void shouldThrowExceptionWhenRecruiterNotFoundForGetRecruiterJobs() {

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.getRecruiterJobs("rutvik@test.com")
    );

    assertEquals("Recruiter not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository, never()).findByRecruiter(any(User.class));
}

@Test
void shouldUpdateJobSuccessfully() {

    Company company = new Company();
    company.setCompanyName("OpenAI");

    User recruiter = new User();
    recruiter.setName("Rutvik");
    recruiter.setEmail("rutvik@test.com");
    recruiter.setCompany(company);

    JobRequest request = new JobRequest();
    request.setTitle("Senior Java Developer");
    request.setDescription("Updated Description");
    request.setSkillsRequired("Java, Spring Boot, Docker");
    request.setLocation("Mumbai");
    request.setSalary("12 LPA");
    request.setJobType("Full Time");

    Job job = new Job();
    job.setId(1L);
    job.setRecruiter(recruiter);
    job.setCompany(company);

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.findByIdAndRecruiter(1L, recruiter))
            .thenReturn(Optional.of(job));

    when(jobRepository.save(any(Job.class)))
            .thenReturn(job);

    JobResponse response =
            jobService.updateJob(1L, request, "rutvik@test.com");

    assertNotNull(response);
    assertEquals("Senior Java Developer", response.getTitle());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository).findByIdAndRecruiter(1L, recruiter);
    verify(jobRepository).save(any(Job.class));
}

@Test
void shouldThrowExceptionWhenUpdatingJobIfRecruiterNotFound() {

    JobRequest request = new JobRequest();

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.updateJob(1L, request, "rutvik@test.com")
    );

    assertEquals("Recruiter not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository, never()).findByIdAndRecruiter(anyLong(), any(User.class));
}

@Test
void shouldThrowExceptionWhenUpdatingJobIfJobNotFound() {

    User recruiter = new User();
    recruiter.setEmail("rutvik@test.com");

    JobRequest request = new JobRequest();

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.findByIdAndRecruiter(1L, recruiter))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.updateJob(1L, request, "rutvik@test.com")
    );

    assertEquals("Job not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository).findByIdAndRecruiter(1L, recruiter);
    verify(jobRepository, never()).save(any(Job.class));
}

@Test
void shouldDeleteJobSuccessfully() {

    User recruiter = new User();
    recruiter.setEmail("rutvik@test.com");

    Job job = new Job();
    job.setId(1L);

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.findByIdAndRecruiter(1L, recruiter))
            .thenReturn(Optional.of(job));

    String response = jobService.deleteJob(1L, "rutvik@test.com");

    assertEquals("Job deleted successfully", response);

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository).findByIdAndRecruiter(1L, recruiter);
    verify(jobRepository).delete(job);
}

@Test
void shouldThrowExceptionWhenDeletingJobIfRecruiterNotFound() {

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.deleteJob(1L, "rutvik@test.com")
    );

    assertEquals("Recruiter not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository, never()).findByIdAndRecruiter(anyLong(), any(User.class));
}

@Test
void shouldThrowExceptionWhenDeletingJobIfJobNotFound() {

    User recruiter = new User();
    recruiter.setEmail("rutvik@test.com");

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.findByIdAndRecruiter(1L, recruiter))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> jobService.deleteJob(1L, "rutvik@test.com")
    );

    assertEquals("Job not found", exception.getMessage());

    verify(userRepository).findByEmail("rutvik@test.com");
    verify(jobRepository).findByIdAndRecruiter(1L, recruiter);
    verify(jobRepository, never()).delete(any(Job.class));
}
}