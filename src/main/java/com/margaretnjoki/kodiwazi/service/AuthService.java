package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.ContributorResponse;
import com.margaretnjoki.kodiwazi.dtos.RegisterContributorRequest;
import com.margaretnjoki.kodiwazi.entity.Contributor;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    private final ContributorRepository contributorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ContributorRepository contributorRepository, PasswordEncoder passwordEncoder) {
        this.contributorRepository = contributorRepository;
        this.passwordEncoder = passwordEncoder;
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
}
