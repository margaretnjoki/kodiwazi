package com.margaretnjoki.kodiwazi.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record QuoteCheckRequest(
        @NotNull(message = "quotedAmount is required")
        @Positive(message = "quotedAmount must be greater than zero")
        BigDecimal quotedAmount,
        boolean utilitiesIncluded
) {
}