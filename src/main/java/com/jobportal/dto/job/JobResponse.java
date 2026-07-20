package com.jobportal.dto.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private Long id;

    private String title;

    private String description;

    private String skillsRequired;

    private String location;

    private String salary;

    private String jobType;

    private String companyName;

    private String recruiterName;
}