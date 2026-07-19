package com.jobportal.dto.recruiter;

import lombok.Data;

@Data
public class RecruiterResponse {

    private Long id;
    private String name;
    private String email;
    private String companyName;
}