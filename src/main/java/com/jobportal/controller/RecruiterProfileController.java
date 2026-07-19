package com.jobportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.service.RecruiterService;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterProfileController {

    private final RecruiterService recruiterService;

    public RecruiterProfileController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @GetMapping("/profile")
    public RecruiterProfileResponse getProfile(Authentication authentication) {

        return recruiterService.getRecruiterProfile(authentication.getName());
    }
}