package com.margaretnjoki.kodiwazi.repository;

import com.margaretnjoki.kodiwazi.entity.RentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentSubmissionRepository extends JpaRepository<RentSubmission, UUID> {

}
