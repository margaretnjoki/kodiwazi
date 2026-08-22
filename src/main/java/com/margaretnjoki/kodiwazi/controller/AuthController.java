package com.margaretnjoki.kodiwazi.controller;


import com.margaretnjoki.kodiwazi.dtos.ContributorResponse;
import com.margaretnjoki.kodiwazi.dtos.LoginRequest;
import com.margaretnjoki.kodiwazi.dtos.LoginResponse;
import com.margaretnjoki.kodiwazi.dtos.RegisterContributorRequest;
import com.margaretnjoki.kodiwazi.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ContributorResponse register(@RequestBody RegisterContributorRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
