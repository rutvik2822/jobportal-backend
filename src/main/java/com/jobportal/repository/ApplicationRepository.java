package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUserId(Long userId);

    Optional<Application> findByUserIdAndJobId(Long userId, Long jobId);

    // Recruiter can view applications for their own jobs
    List<Application> findByJobRecruiterId(Long recruiterId);

    long countByJobRecruiterId(Long recruiterId);

    long countByJobRecruiterIdAndStatus(Long recruiterId, String status);
}