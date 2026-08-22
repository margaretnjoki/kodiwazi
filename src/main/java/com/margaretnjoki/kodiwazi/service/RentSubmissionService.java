package com.margaretnjoki.kodiwazi.service;


import com.margaretnjoki.kodiwazi.dtos.RentSubmissionRequest;
import com.margaretnjoki.kodiwazi.dtos.RentSubmissionResponse;
import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.entity.Contributor;
import com.margaretnjoki.kodiwazi.entity.RentSubmission;
import com.margaretnjoki.kodiwazi.entity.SubmissionStatus;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class RentSubmissionService {
    private final RentSubmissionRepository rentSubmissionRepository;
    private final AreaRepository areaRepository;
    private final ContributorRepository contributorRepository;

    public RentSubmissionService(RentSubmissionRepository rentSubmissionRepository, AreaRepository areaRepository, ContributorRepository contributorRepository) {
        this.rentSubmissionRepository = rentSubmissionRepository;
        this.areaRepository = areaRepository;
        this.contributorRepository = contributorRepository;
    }

    public RentSubmissionResponse submit(RentSubmissionRequest Request, String contributorEmail) {
        Contributor contributor = contributorRepository
                .findByEmail(contributorEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contributor not found")
                );

        Area area = areaRepository
                .findById(request.areaId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Area not found")
                );

        RentSubmission submission = RentSubmission.builder()
                .contributor(contributor)
                .area(area)
                .houseType(request.houseType())
                .amount(request.amount())
                .status(SubmissionStatus.ACTIVE)
                .utilitiesIncluded(request.utilitiesIncluded())
                .build();

        RentSubmission saved = rentSubmissionRepository.save(submission);

        return toResponse(saved);
    }

    private RentSubmissionResponse toResponse(RentSubmission submission) {
        return new RentSubmissionResponse(
                submission.getId(),
                submission.getContributor().getId(),
                submission.getArea().getId(),
                submission.getHouseType(),
                submission.getAmount(),
                submission.getStatus(),
                submission.isUtilitiesIncluded()
        );
    }
    }

