package com.jobportal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.UserProfileResponse;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserProfileResponse response = new UserProfileResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        response.setRole(
                user.getRole() != null
                        ? user.getRole().name()
                        : null
        );

        response.setCompanyName(
                user.getCompany() != null
                        ? user.getCompany().getCompanyName()
                        : null
        );

        return response;
    }

    @Transactional
public UserProfileResponse updateProfile(String email, String name) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    if (name == null || name.trim().isEmpty()) {
        throw new RuntimeException("Name cannot be empty");
    }

    user.setName(name.trim());

    User savedUser = userRepository.save(user);

    UserProfileResponse response = new UserProfileResponse();

    response.setId(savedUser.getId());
    response.setName(savedUser.getName());
    response.setEmail(savedUser.getEmail());

    response.setRole(
            savedUser.getRole() != null
                    ? savedUser.getRole().name()
                    : null
    );

    response.setCompanyName(
            savedUser.getCompany() != null
                    ? savedUser.getCompany().getCompanyName()
                    : null
    );

    return response;
}
}