package com.margaretnjoki.kodiwazi.dtos;

import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.entity.SubmissionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record RentSubmissionResponse(
        UUID id,
        UUID contributorId,
        UUID areaId,
        HouseType houseType,
        BigDecimal amount,
        SubmissionStatus status,
        boolean utilitiesIncluded
) {
}
