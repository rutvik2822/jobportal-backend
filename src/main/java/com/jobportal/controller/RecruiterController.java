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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/recruiters")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @PostMapping
    public ResponseEntity<RecruiterResponse> createRecruiter(
            @Valid @RequestBody RecruiterRequest request) {

        RecruiterResponse response = recruiterService.createRecruiter(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}