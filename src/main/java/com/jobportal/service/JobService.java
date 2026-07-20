package com.jobportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobportal.dto.job.JobRequest;
import com.jobportal.dto.job.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
public JobResponse createJob(JobRequest request, String recruiterEmail) {

    User recruiter = userRepository.findByEmail(recruiterEmail)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    Job job = new Job();

    job.setTitle(request.getTitle());
    job.setDescription(request.getDescription());
    job.setSkillsRequired(request.getSkillsRequired());
    job.setLocation(request.getLocation());
    job.setSalary(request.getSalary());
    job.setJobType(request.getJobType());

    job.setRecruiter(recruiter);
    job.setCompany(recruiter.getCompany());

    Job savedJob = jobRepository.save(job);

    return mapToResponse(savedJob);
}

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    
    @Transactional
    public List<Job> getRecruiterJobs(String recruiterEmail) {

        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return jobRepository.findByRecruiter(recruiter);
    }

    private JobResponse mapToResponse(Job job) {

        JobResponse response = new JobResponse();

        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setSkillsRequired(job.getSkillsRequired());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setJobType(job.getJobType());

        response.setCompanyName(
                job.getCompany() != null
                        ? job.getCompany().getCompanyName()
                        : null);

        response.setRecruiterName(
                job.getRecruiter() != null
                        ? job.getRecruiter().getName()
                        : null);

        return response;
    }
}