package com.jobportal.dto;

import lombok.Data;

@Data
public class UserProfileResponse {

    private Long id;

    private String name;

    private String email;

    private String role;

    private String companyName;

    private String phoneNumber;

    private String city;

    private String state;

    private String profilePictureUrl;
}