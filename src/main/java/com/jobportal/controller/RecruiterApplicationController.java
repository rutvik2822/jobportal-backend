package com.jobportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.application.ApplicationStatusRequest;
import com.jobportal.dto.recruiter.RecruiterDashboardResponse;
import com.jobportal.service.ApplicationService;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterApplicationController {

    private final ApplicationService applicationService;

    public RecruiterApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> getRecruiterApplications(
            Principal principal) {

        return applicationService.getApplicationsForRecruiter(
                principal.getName()
        );
    }

    @GetMapping("/dashboard")
    public RecruiterDashboardResponse getDashboard(
            Principal principal) {

        return applicationService.getRecruiterDashboard(
                principal.getName()
        );
    }

    @PutMapping("/applications/{id}/status")
    public String updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusRequest request,
            Principal principal) {

        applicationService.updateStatusByRecruiter(
                id,
                request.getStatus(),
                principal.getName()
        );

        return "Application status updated successfully.";
    }
}