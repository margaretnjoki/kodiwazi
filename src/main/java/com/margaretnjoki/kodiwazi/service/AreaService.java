package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.AreaResponse;
import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<AreaResponse> listAll() {
        return areaRepository.findAll().stream().map(this::toResponse).toList();
    }

    private AreaResponse toResponse(Area area) {
        return new AreaResponse(area.getId(), area.getName(), area.getRegion().getId(), area.getRegion().getName());
    }
}