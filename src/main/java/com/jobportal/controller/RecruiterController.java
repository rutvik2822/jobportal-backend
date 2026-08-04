package com.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.dto.recruiter.RecruiterUpdateRequest;
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

    @Operation(
    summary = "Get All Recruiters",
    description = "Retrieves all recruiter accounts. Accessible only to SUPER_ADMIN."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Recruiters retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied")
})
@GetMapping
public ResponseEntity<List<RecruiterResponse>> getAllRecruiters() {

    return ResponseEntity.ok(
            recruiterService.getAllRecruiters()
    );
}

@Operation(
    summary = "Get Recruiter By ID",
    description = "Retrieves recruiter details by recruiter ID."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Recruiter found"),
    @ApiResponse(responseCode = "404", description = "Recruiter not found")
})
@GetMapping("/{id}")
public ResponseEntity<RecruiterResponse> getRecruiterById(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            recruiterService.getRecruiterById(id)
    );
}

@Operation(
    summary = "Update Recruiter",
    description = "Updates recruiter information. Password is optional."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Recruiter updated successfully"),
    @ApiResponse(responseCode = "400", description = "Validation failed"),
    @ApiResponse(responseCode = "404", description = "Recruiter not found")
})
@PutMapping("/{id}")
public ResponseEntity<RecruiterResponse> updateRecruiter(
        @PathVariable Long id,
        @Valid @RequestBody RecruiterUpdateRequest request) {

    return ResponseEntity.ok(
            recruiterService.updateRecruiter(id, request)
    );
}

@Operation(
    summary = "Delete Recruiter",
    description = "Deletes a recruiter account by ID."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Recruiter deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Recruiter not found")
})
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteRecruiter(
        @PathVariable Long id) {

    recruiterService.deleteRecruiter(id);

    return ResponseEntity.ok("Recruiter deleted successfully");
}
}