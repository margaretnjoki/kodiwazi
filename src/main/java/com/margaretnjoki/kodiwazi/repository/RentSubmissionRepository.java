package com.margaretnjoki.kodiwazi.repository;

import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.entity.RentSubmission;
import com.margaretnjoki.kodiwazi.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentSubmissionRepository extends JpaRepository<RentSubmission, UUID> {
    List<RentSubmission> findByAreaIdAndHouseTypeAndStatus(
            UUID areaId,
            HouseType houseType,
            SubmissionStatus status
    );

    List<RentSubmission> findByStatus(SubmissionStatus status);

    List<RentSubmission> findByContributorIdAndAreaIdAndHouseTypeAndUtilitiesIncludedAndStatus(
            UUID contributorId,
            UUID areaId,
            HouseType houseType,
            boolean utilitiesIncluded,
            SubmissionStatus status
    );
}
