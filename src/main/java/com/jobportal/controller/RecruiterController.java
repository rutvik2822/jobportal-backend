package com.jobportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.service.RecruiterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Recruiter Management",
    description = "APIs for SUPER_ADMIN to create and manage recruiter accounts."
)
@RestController
@RequestMapping("/api/admin/recruiters")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @Operation(
        summary = "Create Recruiter",
        description = "Creates a new recruiter account and associates it with a company. Accessible only to SUPER_ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recruiter created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "409", description = "Recruiter already exists")
    })
    @PostMapping
    public ResponseEntity<RecruiterResponse> createRecruiter(
            @Valid @RequestBody RecruiterRequest request) {

        RecruiterResponse response = recruiterService.createRecruiter(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}