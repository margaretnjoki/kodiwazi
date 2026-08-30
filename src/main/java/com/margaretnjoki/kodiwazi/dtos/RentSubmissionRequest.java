package com.margaretnjoki.kodiwazi.dtos;

import com.margaretnjoki.kodiwazi.entity.HouseType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record RentSubmissionRequest(

        @NotNull(message = "areaId is required")
        UUID areaId,

        @NotNull(message = "houseType is required")
        HouseType houseType,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        @DecimalMax(value = "10000000", message = "amount seems unrealistically high")
        BigDecimal amount,

        boolean utilitiesIncluded
) {
}