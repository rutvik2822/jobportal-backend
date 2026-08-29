package com.jobportal.dto;

import java.util.List;

import lombok.Data;

@Data
public class AdminDashboardResponse {

    private long totalApplications;

    private long accepted;

    private long rejected;

    private long pending;

    private List<JobApplicationCount> applicationsPerJob;

    @Data
    public static class JobApplicationCount {

        private String job;

        private long applications;

        public JobApplicationCount(String job, long applications) {
            this.job = job;
            this.applications = applications;
        }
    }
}