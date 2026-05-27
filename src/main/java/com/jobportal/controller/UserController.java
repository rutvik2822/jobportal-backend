package com.jobportal.controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping
    public String userAccess() {
        return "User access granted";
    }
}
