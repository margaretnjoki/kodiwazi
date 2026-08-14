package com.margaretnjoki.kodiwazi.repository;

import com.margaretnjoki.kodiwazi.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
}
