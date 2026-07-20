package com.jobportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.jobportal.dto.job.JobRequest;
import com.jobportal.dto.job.JobResponse;
import com.jobportal.service.JobService;

@RestController
@RequestMapping("/api/recruiter/jobs")
public class RecruiterJobController {

    private final JobService jobService;

    public RecruiterJobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Create Job
    @PostMapping
    public JobResponse createJob(
            @RequestBody JobRequest request,
            Principal principal) {

        return jobService.createJob(
                request,
                principal.getName());
    }

    // View My Jobs
    @GetMapping
    public List<JobResponse> getMyJobs(Principal principal) {

        return jobService.getRecruiterJobs(
                principal.getName());
    }

    // Update Job
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
    @DeleteMapping("/{id}")
public String deleteJob(
        @PathVariable Long id,
        Principal principal) {

    return jobService.deleteJob(
            id,
            principal.getName());
}
}