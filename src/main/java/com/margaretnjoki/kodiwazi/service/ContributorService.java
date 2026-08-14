package com.margaretnjoki.kodiwazi.service;


import com.margaretnjoki.kodiwazi.dtos.ContributorResponse;
import com.margaretnjoki.kodiwazi.dtos.RegisterContributorRequest;
import com.margaretnjoki.kodiwazi.entity.Contributor;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final PasswordEncoder passwordEncoder;

    public ContributorService(RentSubmissionRepository rentSubmissionRepository, ContributorRepository contributorRepository, PasswordEncoder passwordEncoder) {
        this.contributorRepository = contributorRepository;
        this.passwordEncoder = passwordEncoder;
    }


}
