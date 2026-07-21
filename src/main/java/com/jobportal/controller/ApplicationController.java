package com.jobportal.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.service.ApplicationService;

@RestController
@RequestMapping("/api/user")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    public Application apply(
            @RequestParam("jobId") Long jobId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        return applicationService.apply(
                jobId,
                file,
                principal.getName()
        );
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume(
            @RequestParam String fileName
    ) throws Exception {

        Path path = Paths.get("uploads")
                .resolve(fileName)
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume file not found: " + fileName);
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

    @GetMapping("/resume/view")
    public ResponseEntity<Resource> previewResume(
            @RequestParam String fileName
    ) throws Exception {

        Path path = Paths.get("uploads")
                .resolve(fileName)
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume file not found: " + fileName);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> getMyApplications(
            Principal principal) {

        return applicationService.getApplicationsByUser(
                principal.getName()
        );
    }
}