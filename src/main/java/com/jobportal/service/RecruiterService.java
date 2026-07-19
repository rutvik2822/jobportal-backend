package com.jobportal.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecruiterService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public RecruiterService(UserRepository userRepository,
                            CompanyRepository companyRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RecruiterResponse createRecruiter(RecruiterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        User recruiter = new User();
        recruiter.setName(request.getName());
        recruiter.setEmail(request.getEmail());
        recruiter.setPassword(passwordEncoder.encode(request.getPassword()));
        recruiter.setRole(Role.RECRUITER);
        recruiter.setCompany(company);

        User savedRecruiter = userRepository.save(recruiter);

        RecruiterResponse response = new RecruiterResponse();
        response.setId(savedRecruiter.getId());
        response.setName(savedRecruiter.getName());
        response.setEmail(savedRecruiter.getEmail());
        response.setCompanyName(company.getCompanyName());

        return response;
    }
    @Transactional
    public RecruiterProfileResponse getRecruiterProfile(String email) {

    User recruiter = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    RecruiterProfileResponse response = new RecruiterProfileResponse();

    response.setId(recruiter.getId());
    response.setName(recruiter.getName());
    response.setEmail(recruiter.getEmail());
    response.setRole(recruiter.getRole().name());

    if (recruiter.getCompany() != null) {
        response.setCompanyName(recruiter.getCompany().getCompanyName());
    }

    return response;
}
}