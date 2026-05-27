package com.jobportal.dto;

import lombok.Data;

@Data
public class ApplicationResponse {

    private Long id;

    private String userEmail;

    private String jobTitle;

    private double matchScore;

    private String status;

    private String resumeFileName; 
}
