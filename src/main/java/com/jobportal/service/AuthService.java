package com.jobportal.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.UUID;

import com.jobportal.entity.PasswordResetToken;
import com.jobportal.repository.PasswordResetTokenRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(
        UserRepository userRepository,
        PasswordResetTokenRepository passwordResetTokenRepository,
        BCryptPasswordEncoder passwordEncoder,
        JwtUtil jwtUtil,
        EmailService emailService) {

    this.userRepository = userRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.emailService = emailService;
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

    @Transactional
    public String requestPasswordReset(String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    // Remove any previous reset token
    passwordResetTokenRepository.deleteByUserId(user.getId());

    // Generate secure random token
    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken = new PasswordResetToken();

    resetToken.setToken(token);
    resetToken.setUser(user);
    resetToken.setExpiryDate(
            LocalDateTime.now().plusMinutes(15)
    );

    passwordResetTokenRepository.save(resetToken);

    String resetLink =
            "http://localhost:3000/reset-password?token=" + token;

    String subject = "Password Reset Request";

    String message =
            "Dear " + user.getName() + ",\n\n" +
            "We received a request to reset your password.\n\n" +
            "Click the link below to reset your password:\n\n" +
            resetLink + "\n\n" +
            "This link will expire in 15 minutes.\n\n" +
            "If you did not request a password reset, you can safely ignore this email.\n\n" +
            "Best Regards,\n" +
            "AI Recruitment Portal";

    emailService.sendEmail(
            user.getEmail(),
            subject,
            message
    );

    return "Password reset link sent to your email";
}
@Transactional
public String resetPassword(
        String token,
        String newPassword,
        String confirmPassword) {

    PasswordResetToken resetToken =
            passwordResetTokenRepository.findByToken(token)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Invalid or expired password reset token"));

    if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

        passwordResetTokenRepository.deleteByToken(token);

        throw new RuntimeException(
                "Password reset token has expired");
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {

        throw new RuntimeException(
                "New password cannot be empty");
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

    User user = resetToken.getUser();

    if (passwordEncoder.matches(
            newPassword,
            user.getPassword())) {

        throw new RuntimeException(
                "New password must be different from your current password");
    }

    user.setPassword(
            passwordEncoder.encode(newPassword)
    );

    userRepository.save(user);

    // Make token single-use
    passwordResetTokenRepository.deleteByToken(token);

    return "Password reset successfully";
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