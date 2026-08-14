package com.margaretnjoki.kodiwazi.service;


import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class RentSubmissionService {
    private final RentSubmissionRepository rentSubmissionRepository;

    public RentSubmissionService(RentSubmissionRepository rentSubmissionRepository) {
        this.rentSubmissionRepository = rentSubmissionRepository;
    }
}
