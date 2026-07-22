package com.jobportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.company.CompanyRequest;
import com.jobportal.dto.company.CompanyResponse;
import com.jobportal.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Company Management",
    description = "APIs for SUPER_ADMIN to create, retrieve, update, and delete company information."
)
@RestController
@RequestMapping("/api/admin/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(
        summary = "Create Company",
        description = "Creates a new company. Accessible only to SUPER_ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request) {

        return ResponseEntity.ok(companyService.createCompany(request));
    }

    @Operation(
        summary = "Get All Companies",
        description = "Retrieves a list of all registered companies."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {

        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(
        summary = "Get Company By ID",
        description = "Retrieves the details of a specific company using its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Operation(
        summary = "Update Company",
        description = "Updates the details of an existing company."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequest request) {

        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    @Operation(
        summary = "Delete Company",
        description = "Deletes a company using its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(
            @PathVariable Long id) {

        companyService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}