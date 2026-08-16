package com.margaretnjoki.kodiwazi.repository;

import com.margaretnjoki.kodiwazi.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContributorRepository extends JpaRepository<Contributor, UUID> {

    boolean existsByEmail(String email);

    Optional<Contributor> findByEmail(String email);
}

