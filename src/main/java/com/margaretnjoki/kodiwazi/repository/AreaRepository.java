package com.margaretnjoki.kodiwazi.repository;

import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    List<Area> findByRegionId(UUID regionId);

    List<Area> findByNameContainingIgnoreCase(String name);

    List<Area> findByRegionIdAndNameContainingIgnoreCase(UUID regionId, String name);
}
