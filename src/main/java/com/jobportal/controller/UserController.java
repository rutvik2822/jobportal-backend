package com.jobportal.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.UserProfileResponse;
import com.jobportal.dto.UserProfileUpdateRequest;
import com.jobportal.service.UserService;

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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @Operation(
        summary = "Get User Profile",
        description = "Retrieves the profile information of the currently authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/profile")
    public UserProfileResponse getProfile(Principal principal) {
        return userService.getProfile(principal.getName());
    }

    @Operation(
        summary = "Update User Profile",
        description = "Updates the name, phone number, city, and state of the authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid profile information"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            @RequestBody UserProfileUpdateRequest request,
            Principal principal) {

        return userService.updateProfile(
                principal.getName(),
                request.getName(),
                request.getPhoneNumber(),
                request.getCity(),
                request.getState()
        );
    }
}