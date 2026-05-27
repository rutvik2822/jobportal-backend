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

        // ✅ Allow iframe preview
        .headers(headers ->
            headers.frameOptions(frame -> frame.disable())
        )

        // ✅ Disable default Spring login popup
        .httpBasic(httpBasic -> httpBasic.disable())

        .formLogin(form -> form.disable())

        // ✅ Use your custom CORS config
        .cors(cors -> cors.configurationSource(corsConfigurationSource))

        .authorizeHttpRequests(auth -> auth
            
            .requestMatchers("/").permitAll()
            // ✅ Public APIs
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/ai/**").permitAll()

            // ✅ Resume preview + download
            .requestMatchers("/api/user/resume/**").permitAll()

            // ✅ Allow OPTIONS requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // ✅ Role based access
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            .requestMatchers("/api/user/**").hasRole("USER")

            .anyRequest().authenticated()
        )

        // ✅ JWT filter
        .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}
}