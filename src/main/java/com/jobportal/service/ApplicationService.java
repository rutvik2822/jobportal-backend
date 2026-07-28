package com.jobportal.service;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.recruiter.RecruiterDashboardResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.exception.DuplicateApplicationException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;
    private final FileStorageService fileStorageService;
    private final ResumeMatcherService resumeMatcherService;
    private final NotificationService notificationService;
    

    public ApplicationService(ApplicationRepository applicationRepository,
                          JobRepository jobRepository,
                          UserRepository userRepository,
                          PdfService pdfService,
                          FileStorageService fileStorageService,
                          ResumeMatcherService resumeMatcherService,
                          NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
        this.fileStorageService = fileStorageService;
        this.resumeMatcherService = resumeMatcherService;
        this.notificationService = notificationService; 
    }

    public Application apply(Long jobId,
                         MultipartFile file,
                         String email) {

    try {

        User user = userRepository.findByEmail(email)
        .orElseThrow(() ->
        new ResourceNotFoundException("User not found"));

Job job = jobRepository.findById(jobId)
        .orElseThrow(() ->
        new ResourceNotFoundException("Job not found"));

// ✅ Prevent duplicate applications
if (applicationRepository.findByUserIdAndJobId(user.getId(), job.getId()).isPresent()) {
    throw new DuplicateApplicationException(
        "You have already applied for this job.");
}

        // SAVE FILE
        String filePath = fileStorageService.storeFile(file);

        // EXTRACT TEXT
        String resumeText =
                pdfService.extractText(file.getInputStream());

        // AI MATCH
        double score = resumeMatcherService.calculateMatch(
        job.getSkillsRequired(),
        resumeText
        );

        Application app = new Application();

        app.setUser(user);

        app.setJob(job);

        app.setResume(resumeText);

        app.setMatchScore(score);

        app.setStatus("PENDING");

        // SAVE FILE INFO
        app.setResumeFilePath(filePath);

        app.setResumeFileName(
        Paths.get(filePath)
             .getFileName()
             .toString());

        // Save application
        Application savedApplication = applicationRepository.save(app);

        // Send confirmation email
        notificationService.sendApplicationReceivedEmail(savedApplication);
        
        // Notify recruiter
        notificationService.sendRecruiterNotificationEmail(savedApplication);
        return savedApplication;

    } catch (IOException e) {

        throw new RuntimeException(e);
    }
}

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplications() {

    List<Application> applications =
            applicationRepository.findAll();

    List<ApplicationResponse> responseList =
            new ArrayList<>();

    for (Application app : applications) {

        User user = app.getUser();

        Job job = app.getJob();

        // ✅ SKIP BROKEN RECORDS
        if (user == null || job == null) {
            continue;
        }

        ApplicationResponse response =
                new ApplicationResponse();

        response.setId(app.getId());

        response.setUserEmail(user.getEmail());

        response.setJobTitle(job.getTitle());

        response.setMatchScore(app.getMatchScore());

        response.setStatus(app.getStatus());

        response.setResumeFileName(
                app.getResumeFileName()
        );

        responseList.add(response);
    }

    return responseList;
}

@Transactional
public void updateStatusByRecruiter(Long applicationId,
                                    String status,
                                    String recruiterEmail) {

    // Find logged-in recruiter
    User recruiter = userRepository.findByEmail(recruiterEmail)
        .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

    // Find application
    Application app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

    Job job = app.getJob();

    // Security check
    if (!job.getRecruiter().getId().equals(recruiter.getId())) {
        throw new UnauthorizedException(
                "You are not authorized to update this application.");
    }

    // Update status
    app.setStatus(status);

    applicationRepository.save(app);

    // Send email
    notificationService.sendApplicationStatusEmail(app);
}

   @Transactional(readOnly = true)
public List<ApplicationResponse> getApplicationsByUser(String email) {

    User user = userRepository
            .findByEmail(email)
            .orElseThrow();

    List<Application> applications =
            applicationRepository.findByUserId(user.getId());

    List<ApplicationResponse> responseList =
            new ArrayList<>();

    for (Application app : applications) {

        Job job = app.getJob(); 

        // ✅ skip deleted jobs
        if (job == null) {
            continue;
        }

        ApplicationResponse response =
                new ApplicationResponse();

        response.setId(app.getId());

        response.setJobTitle(job.getTitle());

        response.setUserEmail(user.getEmail());

        response.setMatchScore(app.getMatchScore());

        response.setStatus(app.getStatus());

        response.setResumeFileName(
                app.getResumeFileName()
        );

        responseList.add(response);
    }

    return responseList;
}

@Transactional(readOnly = true)
public List<ApplicationResponse> getApplicationsForRecruiter(String email) {

    User recruiter = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

    List<Application> applications =
            applicationRepository.findByJobRecruiterId(recruiter.getId());

    List<ApplicationResponse> responseList = new ArrayList<>();

    for (Application app : applications) {

        User candidate = app.getUser();
        Job job = app.getJob();

        if (candidate == null || job == null) {
            continue;
        }

        ApplicationResponse response = new ApplicationResponse();

        response.setId(app.getId());
        response.setUserEmail(candidate.getEmail());
        response.setJobTitle(job.getTitle());
        response.setMatchScore(app.getMatchScore());
        response.setStatus(app.getStatus());
        response.setResumeFileName(app.getResumeFileName());

        responseList.add(response);
    }

    return responseList;
}

@Transactional(readOnly = true)
public RecruiterDashboardResponse getRecruiterDashboard(String email) {

    // Find logged-in recruiter
    User recruiter = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Recruiter not found"));

    RecruiterDashboardResponse response =
            new RecruiterDashboardResponse();

    // Total jobs posted
    response.setTotalJobs(
            jobRepository.countByRecruiter(recruiter)
    );

    // Total applications received
    response.setTotalApplications(
            applicationRepository.countByJobRecruiterId(
                    recruiter.getId())
    );

    // Pending applications
    response.setPending(
            applicationRepository.countByJobRecruiterIdAndStatus(
                    recruiter.getId(),
                    "PENDING")
    );

    // Accepted applications
    response.setAccepted(
            applicationRepository.countByJobRecruiterIdAndStatus(
                    recruiter.getId(),
                    "ACCEPTED")
    );

    // Rejected applications
    response.setRejected(
            applicationRepository.countByJobRecruiterIdAndStatus(
                    recruiter.getId(),
                    "REJECTED")
    );

    return response;
}
}