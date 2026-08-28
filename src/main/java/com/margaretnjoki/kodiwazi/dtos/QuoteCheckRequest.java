package com.margaretnjoki.kodiwazi.dtos;

import java.math.BigDecimal;

public record QuoteCheckRequest(
        BigDecimal quotedAmount,
        boolean utilitiesIncluded
) {
}