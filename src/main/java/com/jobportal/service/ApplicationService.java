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

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PdfService pdfService;

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

        return applicationRepository.save(app);

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
    public Application updateStatus(Long applicationId, String status) {

    Application app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

    app.setStatus(status);

    Application updatedApp = applicationRepository.save(app);

    // 🔥 GET USER
    User user = app.getUser();

    // 🔥 EMAIL SUBJECT
    String subject = "Job Application Status Update";

    // 🔥 EMAIL MESSAGE
    Job job = app.getJob();

String message;

if (status.equals("ACCEPTED")) {

    message =
            "Congratulations!\n\n" +
            "Your application for the position of '" + job.getTitle() + "' has been ACCEPTED.\n\n" +
            "Our team will contact you shortly regarding the next steps.\n\n" +
            "Best Regards,\n" +
            "Job Portal Team";

} else {

    message =
            "Dear Candidate,\n\n" +
            "We regret to inform you that your application for the position of '" + job.getTitle() + "' has been REJECTED.\n\n" +
            "Thank you for applying and we wish you success in your future opportunities.\n\n" +
            "Best Regards,\n" +
            "Job Portal Team";
}

    // 🔥 SEND EMAIL
try {

    emailService.sendEmail(
            user.getEmail(),
            subject,
            message
    );

    System.out.println("Email sent successfully");

} catch (Exception e) {

    System.out.println("Email sending failed");

    e.printStackTrace();
}

return updatedApp;
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
}