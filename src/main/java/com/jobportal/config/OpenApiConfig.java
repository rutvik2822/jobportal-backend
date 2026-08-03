package com.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

   @Bean
public OpenAPI jobPortalOpenAPI() {

    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()

            .info(new Info()
                    .title("AI Recruitment & Job Portal API")
                    .description("REST APIs for the AI Recruitment & Job Portal built using Spring Boot.")
                    .version("1.0.0")
                    .contact(new Contact()
                            .name("Rutvik Devdare"))
                    .license(new License()
                            .name("MIT License")))

            .addSecurityItem(new SecurityRequirement()
                    .addList(securitySchemeName))

            .components(new Components()
                    .addSecuritySchemes(
                            securitySchemeName,
                            new SecurityScheme()
                                    .name(securitySchemeName)
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                    ));
}
}