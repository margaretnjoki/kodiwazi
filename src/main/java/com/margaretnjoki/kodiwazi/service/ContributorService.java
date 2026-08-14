package com.margaretnjoki.kodiwazi.service;


import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ContributorService {
    private final RentSubmissionRepository rentSubmissionRepository;
    private final PasswordEncoder passwordEncoder;

    public ContributorService(RentSubmissionRepository rentSubmissionRepository, PasswordEncoder passwordEncoder) {
        this.rentSubmissionRepository = rentSubmissionRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
