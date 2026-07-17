package com.jobportal.dto.company;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyResponse {

    private Long id;
    private String companyName;
    private String website;
    private String location;
    private String description;
    private String logoUrl;
    private LocalDateTime createdAt;
}