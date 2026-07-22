package com.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
    name = "Admin Management",
    description = "APIs for SUPER_ADMIN to manage and monitor the Job Portal."
)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ApplicationService applicationService;

    public AdminController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
        summary = "Get all job applications",
        description = "Retrieves all job applications submitted by candidates. Accessible only to SUPER_ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/applications")
    public List<ApplicationResponse> getApplications() {
        return applicationService.getAllApplications();
    }
}