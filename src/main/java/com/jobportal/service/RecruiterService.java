package com.jobportal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.recruiter.RecruiterProfileResponse;
import com.jobportal.dto.recruiter.RecruiterRequest;
import com.jobportal.dto.recruiter.RecruiterResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

@Service
public class RecruiterService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;

    public RecruiterService(UserRepository userRepository,
                            CompanyRepository companyRepository,
                            PasswordEncoder passwordEncoder,
                            JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jobRepository = jobRepository;
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
    public List<RecruiterResponse> getAllRecruiters() {

    return userRepository.findAll()
            .stream()
            .filter(user -> user.getRole() == Role.RECRUITER)
            .map(recruiter -> {

                RecruiterResponse response = new RecruiterResponse();

                response.setId(recruiter.getId());
                response.setName(recruiter.getName());
                response.setEmail(recruiter.getEmail());

                if (recruiter.getCompany() != null) {
                    response.setCompanyName(
                            recruiter.getCompany().getCompanyName()
                    );
                }

                return response;

            })
            .collect(Collectors.toList());
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
@Transactional(readOnly = true)
public RecruiterResponse getRecruiterById(Long id) {

    User recruiter = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    if (recruiter.getRole() != Role.RECRUITER) {
        throw new RuntimeException("User is not a recruiter");
    }

    RecruiterResponse response = new RecruiterResponse();

    response.setId(recruiter.getId());
    response.setName(recruiter.getName());
    response.setEmail(recruiter.getEmail());

    if (recruiter.getCompany() != null) {
        response.setCompanyName(recruiter.getCompany().getCompanyName());
    }

    return response;
}

@Transactional
public RecruiterResponse updateRecruiter(Long id, RecruiterRequest request) {

    User recruiter = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    if (recruiter.getRole() != Role.RECRUITER) {
        throw new RuntimeException("User is not a recruiter");
    }

    Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new RuntimeException("Company not found"));

    recruiter.setName(request.getName());
    recruiter.setEmail(request.getEmail());

    // Encode new password
    recruiter.setPassword(
            passwordEncoder.encode(request.getPassword())
    );

    recruiter.setCompany(company);

    User updatedRecruiter = userRepository.save(recruiter);

    RecruiterResponse response = new RecruiterResponse();
    response.setId(updatedRecruiter.getId());
    response.setName(updatedRecruiter.getName());
    response.setEmail(updatedRecruiter.getEmail());
    response.setCompanyName(company.getCompanyName());

    return response;
}

@Transactional
public void deleteRecruiter(Long id) {

    User recruiter = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

    if (recruiter.getRole() != Role.RECRUITER) {
        throw new RuntimeException("User is not a recruiter");
    }

    if (jobRepository.existsByRecruiter(recruiter)) {
        throw new RuntimeException(
                "Cannot delete recruiter because they have posted jobs.");
    }

    userRepository.delete(recruiter);
}
}