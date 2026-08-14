package com.margaretnjoki.kodiwazi.dtos;

import com.margaretnjoki.kodiwazi.entity.HouseType;

import java.math.BigDecimal;
import java.util.UUID;

public record RentSubmissionRequest(
        UUID areaId,
        HouseType houseType,
        BigDecimal amount,
        boolean utilitiesIncluded
) {
}
