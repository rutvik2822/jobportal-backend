package com.jobportal.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    private String name;

    private String phoneNumber;

    private String city;

    private String state;
}