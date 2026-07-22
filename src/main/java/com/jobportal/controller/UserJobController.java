package com.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.job.JobResponse;
import com.jobportal.service.JobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Job Search",
    description = "APIs for candidates to browse available job opportunities."
)
@RestController
@RequestMapping("/api/user/jobs")
public class UserJobController {

    private final JobService jobService;

    public UserJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
        summary = "Get All Available Jobs",
        description = "Retrieves a list of all active job postings available for candidates."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }
}