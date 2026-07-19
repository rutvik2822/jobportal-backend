package com.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.jobportal.security.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // Allow iframe preview
            .headers(headers ->
                headers.frameOptions(frame -> frame.disable())
            )

            // Disable default Spring Security login page
            .httpBasic(httpBasic -> httpBasic.disable())

            .formLogin(form -> form.disable())

            // Use custom CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeHttpRequests(auth -> auth

                // Public
                .requestMatchers("/").permitAll()

                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()

                // AI APIs
                .requestMatchers("/api/ai/**").permitAll()

                // Resume Preview & Download
                .requestMatchers("/api/user/resume/**").permitAll()

                // Allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // SUPER_ADMIN only
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/admin/companies/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/admin/recruiters/**").hasRole("SUPER_ADMIN")

                .requestMatchers("/api/recruiter/**").hasRole("RECRUITER")

                // USER only
                .requestMatchers("/api/user/**").hasRole("USER")

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // JWT Filter
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}