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

        return buildProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(
            String email,
            String name,
            String phoneNumber,
            String city,
            String state) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty");
        }

        user.setName(name.trim());
        user.setPhoneNumber(normalize(phoneNumber));
        user.setCity(normalize(city));
        user.setState(normalize(state));

        User savedUser = userRepository.save(user);

        return buildProfileResponse(savedUser);
    }

    private UserProfileResponse buildProfileResponse(User user) {

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

        response.setPhoneNumber(user.getPhoneNumber());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setProfilePictureUrl(user.getProfilePictureUrl());

        return response;
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty()
                ? null
                : value.trim();
    }
}