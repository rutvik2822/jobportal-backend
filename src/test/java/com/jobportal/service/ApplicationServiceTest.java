package com.jobportal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.recruiter.RecruiterDashboardResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.DuplicateApplicationException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

class ApplicationServiceTest{

@Mock
private ApplicationRepository applicationRepository;

@Mock
private JobRepository jobRepository;

@Mock
private UserRepository userRepository;

@Mock
private PdfService pdfService;

@Mock
private FileStorageService fileStorageService;

@Mock
private ResumeMatcherService resumeMatcherService;

@Mock
private NotificationService notificationService;

@InjectMocks
private ApplicationService applicationService;


@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}

@Test
void shouldApplySuccessfully() throws Exception {

    User user = new User();
    user.setId(1L);
    user.setEmail("rutvik@test.com");
    user.setName("Rutvik");

    Job job = new Job();
    job.setId(1L);
    job.setTitle("Java Developer");
    job.setSkillsRequired("Java, Spring Boot");

    MockMultipartFile file =
            new MockMultipartFile(
                    "resume",
                    "resume.pdf",
                    "application/pdf",
                    "dummy".getBytes());

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(user));

    when(jobRepository.findById(1L))
            .thenReturn(Optional.of(job));

    when(applicationRepository.findByUserIdAndJobId(1L, 1L))
            .thenReturn(Optional.empty());

    when(fileStorageService.storeFile(any()))
            .thenReturn("uploads/resume.pdf");

    when(pdfService.extractText(any(ByteArrayInputStream.class)))
            .thenReturn("Java Spring Boot SQL");

    when(resumeMatcherService.calculateMatch(anyString(), anyString()))
            .thenReturn(95.0);

    when(applicationRepository.save(any(Application.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Application application =
            applicationService.apply(
                    1L,
                    file,
                    "rutvik@test.com");

    assertNotNull(application);

    assertEquals(95.0,
            application.getMatchScore());

    assertEquals("PENDING",
            application.getStatus());

    verify(notificationService)
            .sendApplicationReceivedEmail(any());

    verify(notificationService)
            .sendRecruiterNotificationEmail(any());
}

@Test
void shouldThrowExceptionWhenApplicationAlreadyExists() {

    User user = new User();
    user.setId(1L);
    user.setEmail("rutvik@test.com");

    Job job = new Job();
    job.setId(1L);

    MockMultipartFile file =
            new MockMultipartFile(
                    "resume",
                    "resume.pdf",
                    "application/pdf",
                    "dummy".getBytes());

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(user));

    when(jobRepository.findById(1L))
            .thenReturn(Optional.of(job));

    when(applicationRepository.findByUserIdAndJobId(1L, 1L))
            .thenReturn(Optional.of(new Application()));

    DuplicateApplicationException exception =
            assertThrows(
                    DuplicateApplicationException.class,
                    () -> applicationService.apply(
                            1L,
                            file,
                            "rutvik@test.com"));

    assertEquals(
            "You have already applied for this job.",
            exception.getMessage());

    verify(applicationRepository, never())
            .save(any(Application.class));
}

@Test
void shouldThrowExceptionWhenUserNotFound() {

    MockMultipartFile file =
            new MockMultipartFile(
                    "resume",
                    "resume.pdf",
                    "application/pdf",
                    "dummy".getBytes());

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> applicationService.apply(
                            1L,
                            file,
                            "rutvik@test.com"));

    assertEquals(
            "User not found",
            exception.getMessage());

    verify(jobRepository, never()).findById(anyLong());
}

@Test
void shouldThrowExceptionWhenJobNotFound() {

    User user = new User();
    user.setId(1L);
    user.setEmail("rutvik@test.com");

    MockMultipartFile file =
            new MockMultipartFile(
                    "resume",
                    "resume.pdf",
                    "application/pdf",
                    "dummy".getBytes());

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(user));

    when(jobRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> applicationService.apply(
                            1L,
                            file,
                            "rutvik@test.com"));

    assertEquals(
            "Job not found",
            exception.getMessage());

    verify(applicationRepository, never())
            .save(any(Application.class));
}

@Test
void shouldReturnAllApplications() {

    User user = new User();
    user.setEmail("rutvik@test.com");

    Job job = new Job();
    job.setTitle("Java Developer");

    Application application = new Application();
    application.setId(1L);
    application.setUser(user);
    application.setJob(job);
    application.setMatchScore(95.0);
    application.setStatus("PENDING");
    application.setResumeFileName("resume.pdf");

    when(applicationRepository.findAll())
            .thenReturn(List.of(application));

    List<ApplicationResponse> response =
            applicationService.getAllApplications();

    assertEquals(1, response.size());

    assertEquals(
            "Java Developer",
            response.get(0).getJobTitle());

    assertEquals(
            "rutvik@test.com",
            response.get(0).getUserEmail());

    verify(applicationRepository).findAll();
}

@Test
void shouldSkipBrokenApplicationRecords() {

    Application application = new Application();

    application.setUser(null);

    application.setJob(null);

    when(applicationRepository.findAll())
            .thenReturn(List.of(application));

    List<ApplicationResponse> response =
            applicationService.getAllApplications();

    assertTrue(response.isEmpty());

    verify(applicationRepository).findAll();
}

@Test
void shouldReturnApplicationsByUser() {

    User user = new User();
    user.setId(1L);
    user.setEmail("rutvik@test.com");

    Job job = new Job();
    job.setTitle("Java Developer");

    Application application = new Application();
    application.setId(1L);
    application.setUser(user);
    application.setJob(job);
    application.setStatus("PENDING");
    application.setMatchScore(90.0);
    application.setResumeFileName("resume.pdf");

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(user));

    when(applicationRepository.findByUserId(1L))
            .thenReturn(List.of(application));

    List<ApplicationResponse> response =
            applicationService.getApplicationsByUser("rutvik@test.com");

    assertEquals(1, response.size());
    assertEquals("Java Developer", response.get(0).getJobTitle());

    verify(applicationRepository).findByUserId(1L);
}

@Test
void shouldSkipApplicationsWithDeletedJob() {

    User user = new User();
    user.setId(1L);
    user.setEmail("rutvik@test.com");

    Application application = new Application();
    application.setUser(user);
    application.setJob(null);

    when(userRepository.findByEmail("rutvik@test.com"))
            .thenReturn(Optional.of(user));

    when(applicationRepository.findByUserId(1L))
            .thenReturn(List.of(application));

    List<ApplicationResponse> response =
            applicationService.getApplicationsByUser("rutvik@test.com");

    assertTrue(response.isEmpty());
}

@Test
void shouldUpdateApplicationStatusSuccessfully() {

    User recruiter = new User();
    recruiter.setId(10L);
    recruiter.setEmail("recruiter@test.com");

    Job job = new Job();
    job.setRecruiter(recruiter);

    Application application = new Application();
    application.setJob(job);

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(applicationRepository.findById(1L))
            .thenReturn(Optional.of(application));

    applicationService.updateStatusByRecruiter(
            1L,
            "ACCEPTED",
            "recruiter@test.com");

    assertEquals("ACCEPTED", application.getStatus());

    verify(applicationRepository).save(application);

    verify(notificationService)
            .sendApplicationStatusEmail(application);
}

@Test
void shouldThrowExceptionWhenRecruiterNotFound() {

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> applicationService.updateStatusByRecruiter(
                    1L,
                    "ACCEPTED",
                    "recruiter@test.com"));

    verify(applicationRepository, never()).findById(anyLong());
}

@Test
void shouldThrowExceptionWhenApplicationNotFound() {

    User recruiter = new User();
    recruiter.setId(10L);
    recruiter.setEmail("recruiter@test.com");

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(applicationRepository.findById(1L))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> applicationService.updateStatusByRecruiter(
                    1L,
                    "ACCEPTED",
                    "recruiter@test.com"));
}

@Test
void shouldThrowExceptionWhenRecruiterIsUnauthorized() {

    User loggedInRecruiter = new User();
    loggedInRecruiter.setId(1L);

    User actualRecruiter = new User();
    actualRecruiter.setId(2L);

    Job job = new Job();
    job.setRecruiter(actualRecruiter);

    Application application = new Application();
    application.setJob(job);

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.of(loggedInRecruiter));

    when(applicationRepository.findById(1L))
            .thenReturn(Optional.of(application));

    assertThrows(
            UnauthorizedException.class,
            () -> applicationService.updateStatusByRecruiter(
                    1L,
                    "REJECTED",
                    "recruiter@test.com"));

    verify(applicationRepository, never()).save(any());
}

@Test
void shouldReturnApplicationsForRecruiter() {

    User recruiter = new User();
    recruiter.setId(10L);
    recruiter.setEmail("recruiter@test.com");

    User candidate = new User();
    candidate.setEmail("candidate@test.com");

    Job job = new Job();
    job.setTitle("Java Developer");

    Application application = new Application();
    application.setId(1L);
    application.setUser(candidate);
    application.setJob(job);
    application.setStatus("PENDING");
    application.setMatchScore(88.0);
    application.setResumeFileName("resume.pdf");

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(applicationRepository.findByJobRecruiterId(10L))
            .thenReturn(List.of(application));

    List<ApplicationResponse> response =
            applicationService.getApplicationsForRecruiter("recruiter@test.com");

    assertEquals(1, response.size());
    assertEquals("candidate@test.com", response.get(0).getUserEmail());

    verify(applicationRepository).findByJobRecruiterId(10L);
}

@Test
void shouldThrowExceptionWhenRecruiterNotFoundForApplications() {

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> applicationService.getApplicationsForRecruiter(
                    "recruiter@test.com"));
}

@Test
void shouldReturnRecruiterDashboard() {

    User recruiter = new User();
    recruiter.setId(10L);
    recruiter.setEmail("recruiter@test.com");

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.of(recruiter));

    when(jobRepository.countByRecruiter(recruiter))
            .thenReturn(5L);

    when(applicationRepository.countByJobRecruiterId(10L))
            .thenReturn(20L);

    when(applicationRepository.countByJobRecruiterIdAndStatus(10L, "PENDING"))
            .thenReturn(8L);

    when(applicationRepository.countByJobRecruiterIdAndStatus(10L, "ACCEPTED"))
            .thenReturn(7L);

    when(applicationRepository.countByJobRecruiterIdAndStatus(10L, "REJECTED"))
            .thenReturn(5L);

    RecruiterDashboardResponse response =
            applicationService.getRecruiterDashboard("recruiter@test.com");

    assertEquals(5L, response.getTotalJobs());
    assertEquals(20L, response.getTotalApplications());
    assertEquals(8L, response.getPending());
    assertEquals(7L, response.getAccepted());
    assertEquals(5L, response.getRejected());
}

@Test
void shouldThrowExceptionWhenRecruiterNotFoundForDashboard() {

    when(userRepository.findByEmail("recruiter@test.com"))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> applicationService.getRecruiterDashboard(
                    "recruiter@test.com"));
}
}
