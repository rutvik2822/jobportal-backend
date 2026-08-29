package com.jobportal.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                   BCryptPasswordEncoder passwordEncoder,
                   JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}

    public String register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return "User already exists";
        }

        User user = new User();

            user.setName(request.getName());

            user.setEmail(request.getEmail());

            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );

            user.setRole(
            request.getRole() != null
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.USER
            );

            userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

    Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

    if (userOpt.isEmpty()) {
        return "User not found";
    }

    User user = userOpt.get();

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return "Invalid password";
    }

   return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    public String changePassword(
        String email,
        String currentPassword,
        String newPassword,
        String confirmPassword) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    if (!passwordEncoder.matches(
            currentPassword,
            user.getPassword())) {

        throw new RuntimeException("Current password is incorrect");
    }

    if (passwordEncoder.matches(
        newPassword,
        user.getPassword())) {

    throw new RuntimeException(
            "New password must be different from your current password");
}

    if (newPassword == null || newPassword.trim().isEmpty()) {

        throw new RuntimeException("New password cannot be empty");
    }

    if (!newPassword.equals(confirmPassword)) {

        throw new RuntimeException(
                "New password and confirm password do not match");
    }

    if (newPassword.length() < 8) {

    throw new RuntimeException(
            "New password must be at least 8 characters");
}

if (!newPassword.matches(".*[A-Z].*")) {

    throw new RuntimeException(
            "New password must contain at least one uppercase letter");
}

if (!newPassword.matches(".*[a-z].*")) {

    throw new RuntimeException(
            "New password must contain at least one lowercase letter");
}

if (!newPassword.matches(".*\\d.*")) {

    throw new RuntimeException(
            "New password must contain at least one number");
}

if (!newPassword.matches(".*[^a-zA-Z0-9].*")) {

    throw new RuntimeException(
            "New password must contain at least one special character");
}

    user.setPassword(
            passwordEncoder.encode(newPassword)
    );

    userRepository.save(user);

    return "Password changed successfully";
}
}