package com.jobportal.controller;

import com.jobportal.entity.Company;
import com.jobportal.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Create Company
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@RequestBody Company company) {
        return companyService.createCompany(company);
    }

    // Get All Companies
    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    // Get Company By ID
    @GetMapping("/{id}")
    public Optional<Company> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    // Delete Company
    @DeleteMapping("/{id}")
    public String deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return "Company deleted successfully";
    }
}