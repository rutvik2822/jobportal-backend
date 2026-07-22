package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByRecruiter(User recruiter);
    Optional<Job> findByIdAndRecruiter(Long id, User recruiter);

    long countByRecruiter(User recruiter);
}