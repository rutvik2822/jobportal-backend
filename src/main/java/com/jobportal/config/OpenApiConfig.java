package com.jobportal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobPortalOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("AI Recruitment & Job Portal API")
                        .description("REST APIs for the AI Recruitment & Job Portal built using Spring Boot.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rutvik Devdare"))
                        .license(new License()
                                .name("MIT License")));
    }
}