package com.jobportal.config;

import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {

            User admin = new User();

            admin.setName("Super Admin");
            admin.setEmail("admin@jobportal.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.SUPER_ADMIN);

            userRepository.save(admin);

            System.out.println("======================================");
            System.out.println("SUPER_ADMIN created successfully");
            System.out.println("Email : admin@jobportal.com");
            System.out.println("Password : admin123");
            System.out.println("======================================");
        }
    }
}