package com.margaretnjoki.kodiwazi.controller;

import com.margaretnjoki.kodiwazi.dtos.AreaResponse;
import com.margaretnjoki.kodiwazi.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Areas", description = "Browse and search Kenyan neighborhoods/estates")
@RestController
@RequestMapping("/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @Operation(
            summary = "List areas",
            description = "Returns areas, optionally filtered by region and/or a " +
                    "case-insensitive partial name match (e.g. 'south' matches 'South B' " +
                    "and 'South C'). Omit both parameters to list everything."
    )
    @SecurityRequirements
    @GetMapping
    public List<AreaResponse> listAreas(
            @Parameter(description = "UUID of a region to filter by") @RequestParam(required = false) UUID regionId,
            @Parameter(description = "Partial, case-insensitive area name to search for") @RequestParam(required = false) String name
    ) {
        return areaService.listAreas(regionId, name);
    }
}