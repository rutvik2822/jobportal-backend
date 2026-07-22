package com.jobportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "User",
    description = "APIs for authenticated users."
)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Operation(
        summary = "Verify User Access",
        description = "Returns a confirmation message indicating that the authenticated user has access to protected USER endpoints."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User access granted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public String userAccess() {
        return "User access granted";
    }
}