package com.jobportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.service.RecruiterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Recruiter Profile",
    description = "APIs for recruiters to view their profile information."
)
@RestController
@RequestMapping("/api/recruiter")
public class RecruiterProfileController {

    private final RecruiterService recruiterService;

    public RecruiterProfileController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @Operation(
        summary = "Get Recruiter Profile",
        description = "Retrieves the profile information of the currently authenticated recruiter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recruiter profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Recruiter not found")
    })
    @GetMapping("/profile")
    public RecruiterProfileResponse getProfile(Authentication authentication) {

        return recruiterService.getRecruiterProfile(authentication.getName());
    }
}