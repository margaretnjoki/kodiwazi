package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.AreaResponse;
import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<AreaResponse> listAreas(UUID regionId, String regionName, String name) {

        List<Area> areas;

        boolean hasRegionId = regionId != null;
        boolean hasRegionName = regionName != null && !regionName.isBlank();
        boolean hasName = name != null && !name.isBlank();

        if (hasRegionId && hasName) {
            areas = areaRepository.findByRegionIdAndNameContainingIgnoreCase(regionId, name);
        } else if (hasRegionName && hasName) {
            areas = areaRepository.findByRegionNameIgnoreCaseAndNameContainingIgnoreCase(regionName, name);
        } else if (hasRegionId) {
            areas = areaRepository.findByRegionId(regionId);
        } else if (hasRegionName) {
            areas = areaRepository.findByRegionNameIgnoreCase(regionName);
        } else if (hasName) {
            areas = areaRepository.findByNameContainingIgnoreCase(name);
        } else {
            areas = areaRepository.findAll();
        }

        return areas.stream()
                .map(this::toResponse)
                .toList();
    }

    private AreaResponse toResponse(Area area) {
        return new AreaResponse(area.getId(), area.getName(), area.getRegion().getId(), area.getRegion().getName());
    }
}