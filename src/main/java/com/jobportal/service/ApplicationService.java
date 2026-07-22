package com.jobportal.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.recruiter.RecruiterDashboardResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PdfService pdfService;
    
    private static final Logger logger =
            LoggerFactory.getLogger(ApplicationService.class);

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository,
                              EmailService emailService,
                              PdfService pdfService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.pdfService = pdfService; 
    }

    public Application apply(Long jobId,
                         MultipartFile file,
                         String email) {

    try {

        User user = userRepository.findByEmail(email)
        .orElseThrow();

Job job = jobRepository.findById(jobId)
        .orElseThrow();

// ✅ Prevent duplicate applications
if (applicationRepository.findByUserIdAndJobId(user.getId(), job.getId()).isPresent()) {
    throw new RuntimeException("You have already applied for this job.");
}

        // SAVE FILE
        String uploadDir = "uploads/";

        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName =
                System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        Path filePath =
                Paths.get(uploadDir, fileName);

        Files.write(filePath, file.getBytes());

        // EXTRACT TEXT
        String resumeText =
                pdfService.extractText(file.getInputStream());

        // AI MATCH
        double score =
                calculateMatch(
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
        app.setResumeFileName(fileName);

        app.setResumeFilePath(filePath.toString());

        // Save application
        Application savedApplication = applicationRepository.save(app);

        // Send confirmation email
        sendApplicationReceivedEmail(savedApplication);
        
        // Notify recruiter
        sendRecruiterNotificationEmail(savedApplication);
        
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
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    // Find application
    Application app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

    Job job = app.getJob();

    // Security check
    if (!job.getRecruiter().getId().equals(recruiter.getId())) {
        throw new RuntimeException(
                "You are not authorized to update this application.");
    }

    // Update status
    app.setStatus(status);

    applicationRepository.save(app);

    // Send email
    sendApplicationStatusEmail(app);
}

   // 🔥 SAFE AI CALL (IMPORTANT)
private double calculateMatch(String skills, String resume) {

    try {

        RestTemplate restTemplate = new RestTemplate();

        String url =
                "https://resume-ai-service-l3qw.onrender.com/predict";

        Map<String, String> request = new HashMap<>();

        request.put("resume", resume);
        request.put("skills", skills);

        System.out.println("Calling AI Service...");
        System.out.println("URL = " + url);
        System.out.println("Skills = " + skills);

        Map response =
                restTemplate.postForObject(
                        url,
                        request,
                        Map.class
                );

        System.out.println("AI Response = " + response);

        if (response != null &&
                response.get("match_score") != null) {

            return Double.parseDouble(
                    response.get("match_score").toString()
            );
        }

    } catch (Exception e) {

        System.out.println("=================================");
        System.out.println("AI SERVICE ERROR");
        System.out.println("=================================");

        e.printStackTrace();

        System.out.println("=================================");
    }

    // fallback
    return 50.0;
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
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

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
                    new RuntimeException("Recruiter not found"));

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

private void sendApplicationReceivedEmail(Application application) {

    User candidate = application.getUser();

    Job job = application.getJob();

    String companyName = "Our Company";

    if (job.getCompany() != null) {
        companyName = job.getCompany().getCompanyName();
    }

    String subject = "Application Received - " + job.getTitle();

    String message =
            "Dear " + candidate.getName() + ",\n\n" +
            "Thank you for applying for the position of '" + job.getTitle() + "'.\n\n" +
            "Company: " + companyName + "\n" +
            "Current Status: PENDING\n\n" +
            "We have successfully received your application.\n" +
            "Our recruitment team will review your profile and contact you if you are shortlisted.\n\n" +
            "Thank you for your interest in joining " + companyName + ".\n\n" +
            "Best Regards,\n" +
            companyName + " Recruitment Team";

    try {

        emailService.sendEmail(
                candidate.getEmail(),
                subject,
                message
        );

        logger.info("Application confirmation email sent to {}", candidate.getEmail());

    } catch (Exception e) {

        logger.error("Failed to send application confirmation email to {}", candidate.getEmail(), e);
    }
}

private void sendRecruiterNotificationEmail(Application application) {

    User recruiter = application.getJob().getRecruiter();
    User candidate = application.getUser();
    Job job = application.getJob();

    String subject = "New Job Application Received - " + job.getTitle();

    String message =
            "Dear " + recruiter.getName() + ",\n\n" +
            "A new candidate has applied for your job posting.\n\n" +
            "Candidate Name: " + candidate.getName() + "\n" +
            "Candidate Email: " + candidate.getEmail() + "\n" +
            "Job Title: " + job.getTitle() + "\n" +
            "AI Match Score: " + application.getMatchScore() + "%\n\n" +
            "Please log in to the Job Portal to review the application.\n\n" +
            "Best Regards,\n" +
            "AI Recruitment Portal";

    try {

        emailService.sendEmail(
                recruiter.getEmail(),
                subject,
                message
        );

        logger.info("Recruiter notification email sent to {}", recruiter.getEmail());

    } catch (Exception e) {

        logger.error("Failed to send recruiter notification email to {}", recruiter.getEmail(), e);

    }
}
private void sendApplicationStatusEmail(Application application) {

    User candidate = application.getUser();
    Job job = application.getJob();

    String subject;
    String message;

    if ("ACCEPTED".equalsIgnoreCase(application.getStatus())) {

        subject = "Congratulations! Your Application Has Been Accepted";

        message =
                "Dear " + candidate.getName() + ",\n\n" +
                "Congratulations!\n\n" +
                "Your application for the position '" + job.getTitle() + "' has been ACCEPTED.\n\n" +
                "The recruiter will contact you regarding the next steps.\n\n" +
                "Best Wishes,\n" +
                "AI Recruitment Portal";

    } else if ("REJECTED".equalsIgnoreCase(application.getStatus())) {

        subject = "Application Status Update";

        message =
                "Dear " + candidate.getName() + ",\n\n" +
                "Thank you for applying for the position '" + job.getTitle() + "'.\n\n" +
                "Unfortunately, your application was not selected this time.\n\n" +
                "We encourage you to apply for future opportunities.\n\n" +
                "Best Regards,\n" +
                "AI Recruitment Portal";

    } else {

        return;
    }

    try {

        emailService.sendEmail(
                candidate.getEmail(),
                subject,
                message
        );

        logger.info("Status update email sent to {}", candidate.getEmail());

    } catch (Exception e) {

        logger.error("Failed to send status update email to {}", candidate.getEmail(), e);

    }
}
}