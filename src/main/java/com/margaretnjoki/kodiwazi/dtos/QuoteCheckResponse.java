package com.margaretnjoki.kodiwazi.dtos;

import java.math.BigDecimal;

public record QuoteCheckResponse(
        BigDecimal quotedAmount,
        BigDecimal medianAmount,
        Double percentageDifference,
        String verdict,
        double confidenceScore,
        String confidenceLabel
) {
}