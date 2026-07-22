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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Recruiter Management",
    description = "APIs for recruiters to manage applications, update application status, and view dashboard statistics."
)
@RestController
@RequestMapping("/api/recruiter")
public class RecruiterApplicationController {

    private final ApplicationService applicationService;

    public RecruiterApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
        summary = "Get Recruiter Applications",
        description = "Retrieves all job applications submitted for jobs posted by the authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/applications")
    public List<ApplicationResponse> getRecruiterApplications(
            Principal principal) {

        return applicationService.getApplicationsForRecruiter(
                principal.getName()
        );
    }

    @Operation(
        summary = "Recruiter Dashboard",
        description = "Returns dashboard statistics including total jobs, applications, pending, accepted, and rejected applications."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/dashboard")
    public RecruiterDashboardResponse getDashboard(
            Principal principal) {

        return applicationService.getRecruiterDashboard(
                principal.getName()
        );
    }

    @Operation(
        summary = "Update Application Status",
        description = "Allows the recruiter to update the status of an application (e.g. PENDING, ACCEPTED, REJECTED) for jobs they own."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
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