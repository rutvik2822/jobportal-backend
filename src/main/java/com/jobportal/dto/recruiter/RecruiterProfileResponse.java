package com.jobportal.dto.recruiter;

import lombok.Data;

@Data
public class RecruiterProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String companyName;
}