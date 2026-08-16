package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.ContributorResponse;
import com.margaretnjoki.kodiwazi.dtos.LoginRequest;
import com.margaretnjoki.kodiwazi.dtos.LoginResponse;
import com.margaretnjoki.kodiwazi.dtos.RegisterContributorRequest;
import com.margaretnjoki.kodiwazi.entity.Contributor;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final ContributorRepository contributorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ContributorRepository contributorRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.contributorRepository = contributorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ContributorResponse register(RegisterContributorRequest request) {

        if (contributorRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        Contributor contributor = Contributor.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(hashedPassword)
                .enabled(true)
                .build();

        Contributor savedContributor = contributorRepository.save(contributor);

        return new ContributorResponse(
                savedContributor.getId(),
                savedContributor.getFirstName(),
                savedContributor.getLastName(),
                savedContributor.getEmail(),
                savedContributor.isEnabled()
        );
    }

    public LoginResponse login(LoginRequest request) {

        Contributor contributor = contributorRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.password(),
                contributor.getPassword()
        )) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(contributor.getEmail());

        return new LoginResponse(token);
    }
}
