package com.jobportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

public void sendApplicationReceivedEmail(Application application) {

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
public void sendRecruiterNotificationEmail(Application application) {

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

public void sendApplicationStatusEmail(Application application) {

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