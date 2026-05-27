package com.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.JobRequest;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import com.jobportal.service.ApplicationService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ApplicationService applicationService;
    private final JobRepository jobRepository;

    public AdminController(ApplicationService applicationService,
        JobRepository jobRepository) {
        this.applicationService = applicationService;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> getApplications() {
        return applicationService.getAllApplications();
    }
    @PutMapping("/applications/{id}/status")
    public Application updateStatus(
        @PathVariable Long id,
        @RequestParam String status) {

            return applicationService.updateStatus(id, status);
        }

    @PostMapping("/jobs")
public Job addJob(@RequestBody JobRequest request) {

    Job job = new Job();

    job.setTitle(request.getTitle());

    job.setDescription(request.getDescription());

    job.setSkillsRequired(request.getSkillsRequired());

    job.setLocation(request.getLocation());

    job.setSalary(request.getSalary());

    job.setJobType(request.getJobType());

    return jobRepository.save(job);
}
    @DeleteMapping("/jobs/{id}")
public String deleteJob(@PathVariable Long id) {

    jobRepository.deleteById(id);

    return "Job deleted successfully";
}
@GetMapping("/jobs")
public List<Job> getAllJobs() {

    return jobRepository.findAll();
}
@PutMapping("/jobs/{id}")
public Job updateJob(
        @PathVariable Long id,
        @RequestBody JobRequest request) {

    Job job = jobRepository
            .findById(id)
            .orElseThrow();

    job.setTitle(request.getTitle());

    job.setDescription(request.getDescription());

    job.setSkillsRequired(request.getSkillsRequired());

    job.setLocation(request.getLocation());

    job.setSalary(request.getSalary());

    job.setJobType(request.getJobType());

    return jobRepository.save(job);
}
}