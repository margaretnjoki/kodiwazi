package com.margaretnjoki.kodiwazi.dtos;

import java.math.BigDecimal;

public record RentEstimateSegment(
        BigDecimal medianAmount,
        int sampleSize
) {
}
