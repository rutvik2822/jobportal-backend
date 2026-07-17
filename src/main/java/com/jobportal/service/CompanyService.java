package com.jobportal.service;

import com.jobportal.entity.Company;
import com.jobportal.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Create Company
    public Company createCompany(Company company) {

        if (companyRepository.existsByCompanyName(company.getCompanyName())) {
            throw new RuntimeException("Company already exists");
        }

        return companyRepository.save(company);
    }

    // Get All Companies
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // Get Company By ID
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    // Delete Company
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}