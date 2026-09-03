package com.margaretnjoki.kodiwazi.controller;


import com.margaretnjoki.kodiwazi.dtos.ContributorResponse;
import com.margaretnjoki.kodiwazi.dtos.LoginRequest;
import com.margaretnjoki.kodiwazi.dtos.LoginResponse;
import com.margaretnjoki.kodiwazi.dtos.RegisterContributorRequest;
import com.margaretnjoki.kodiwazi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Contributor registration and login")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new contributor",
            description = "Creates a lightweight contributor account (name, email, password). " +
                    "No email verification required."
    )
    @SecurityRequirements
    @PostMapping("/register")
    public ContributorResponse register(@Valid @RequestBody RegisterContributorRequest request) {
        return authService.register(request);
    }

    @Operation(
            summary = "Log in",
            description = "Exchanges email and password for a signed JWT (1-hour expiry). " +
                    "Include the returned token as 'Authorization: Bearer <token>' on " +
                    "protected requests."
    )
    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
