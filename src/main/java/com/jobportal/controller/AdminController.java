package com.jobportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.service.ApplicationService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ApplicationService applicationService;

    public AdminController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> getApplications() {
        return applicationService.getAllApplications();
    }

   @PutMapping("/applications/{id}/status")
public ResponseEntity<String> updateStatus(
        @PathVariable Long id,
        @RequestParam String status) {

    applicationService.updateStatus(id, status);

    return ResponseEntity.ok("Application status updated successfully.");
}
}