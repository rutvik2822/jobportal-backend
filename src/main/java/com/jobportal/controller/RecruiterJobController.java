package com.jobportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.jobportal.dto.job.JobRequest;
import com.jobportal.dto.job.JobResponse;
import com.jobportal.service.JobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Recruiter Job Management",
    description = "APIs for recruiters to create, manage, update, view, and delete their job postings."
)
@RestController
@RequestMapping("/api/recruiter/jobs")
public class RecruiterJobController {

    private final JobService jobService;

    public RecruiterJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
        summary = "Create Job",
        description = "Creates a new job posting for the authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid job details"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping
    public JobResponse createJob(
            @RequestBody JobRequest request,
            Principal principal) {

        return jobService.createJob(
                request,
                principal.getName());
    }

    @Operation(
        summary = "Get My Jobs",
        description = "Retrieves all job postings created by the authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<JobResponse> getMyJobs(
            Principal principal) {

        return jobService.getRecruiterJobs(
                principal.getName());
    }

    @Operation(
        summary = "Update Job",
        description = "Updates an existing job posting owned by the authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid job details"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PutMapping("/{id}")
    public JobResponse updateJob(
            @PathVariable Long id,
            @RequestBody JobRequest request,
            Principal principal) {

        return jobService.updateJob(
                id,
                request,
                principal.getName());
    }

    @Operation(
        summary = "Delete Job",
        description = "Deletes a job posting owned by the authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id,
            Principal principal) {

        return jobService.deleteJob(
                id,
                principal.getName());
    }
}