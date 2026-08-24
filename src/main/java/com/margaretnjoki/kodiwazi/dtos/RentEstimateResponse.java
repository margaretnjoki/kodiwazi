package com.margaretnjoki.kodiwazi.dtos;

import com.margaretnjoki.kodiwazi.entity.HouseType;

import java.util.UUID;

public record RentEstimateResponse(
        UUID areaId,
        String areaName,
        HouseType houseType,
        RentEstimateSegment utilitiesIncluded,
        RentEstimateSegment utilitiesExcluded
) {
}
