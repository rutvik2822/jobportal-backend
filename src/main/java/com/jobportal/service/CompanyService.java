package com.jobportal.service;

import com.jobportal.dto.company.CompanyRequest;
import com.jobportal.dto.company.CompanyResponse;
import com.jobportal.entity.Company;
import com.jobportal.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Helper Method: Entity -> DTO
    private CompanyResponse mapToResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setCompanyName(company.getCompanyName());
        response.setWebsite(company.getWebsite());
        response.setLocation(company.getLocation());
        response.setDescription(company.getDescription());
        response.setLogoUrl(company.getLogoUrl());
        response.setCreatedAt(company.getCreatedAt());
        return response;
    }

    // Create Company
    public CompanyResponse createCompany(CompanyRequest request) {

        if (companyRepository.existsByCompanyName(request.getCompanyName())) {
            throw new RuntimeException("Company already exists");
        }

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setDescription(request.getDescription());
        company.setLogoUrl(request.getLogoUrl());

        Company savedCompany = companyRepository.save(company);

        return mapToResponse(savedCompany);
    }

    // Get All Companies
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Company By ID
    public CompanyResponse getCompanyById(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return mapToResponse(company);
    }

    // Update Company
public CompanyResponse updateCompany(Long id, CompanyRequest request) {

    Company company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));

    company.setCompanyName(request.getCompanyName());
    company.setWebsite(request.getWebsite());
    company.setLocation(request.getLocation());
    company.setDescription(request.getDescription());
    company.setLogoUrl(request.getLogoUrl());

    Company updatedCompany = companyRepository.save(company);

    return mapToResponse(updatedCompany);
}

    // Delete Company
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}