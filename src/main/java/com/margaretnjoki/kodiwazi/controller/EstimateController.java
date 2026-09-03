package com.margaretnjoki.kodiwazi.controller;

import com.margaretnjoki.kodiwazi.dtos.QuoteCheckRequest;
import com.margaretnjoki.kodiwazi.dtos.QuoteCheckResponse;
import com.margaretnjoki.kodiwazi.dtos.RentEstimateResponse;
import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.service.EstimateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Estimates", description = "Query typical rent, and check whether a specific quote is fair")
@RestController
@RequestMapping("/areas/{areaId}/house-types/{houseType}")
public class EstimateController {

    private final EstimateService estimateService;

    public EstimateController(EstimateService estimateService) {
        this.estimateService = estimateService;
    }
    @Operation(
            summary = "Get the rent estimate for an area and house type",
            description = "Returns a recency-weighted median rent for this segment, " +
                    "split by whether utilities are included, each with a confidence " +
                    "score reflecting sample size, recency, and consistency of the " +
                    "backing data. No authentication required."
    )

    @GetMapping("/estimate")
    public RentEstimateResponse getEstimate(
            @Parameter(description = "UUID of the area, from GET /areas") @PathVariable UUID areaId,
            @Parameter(description = "One of the fixed house type categories, e.g. ONE_BEDROOM") @PathVariable HouseType houseType

    ) {
        return estimateService.getEstimate(areaId, houseType);
    }
    @Operation(
            summary = "Check whether a quoted rent is fair",
            description = "Compares a quoted amount against the segment's typical rent, " +
                    "returning a verdict (e.g. TYPICAL, SIGNIFICANTLY_ABOVE_TYPICAL) and " +
                    "the confidence behind that comparison. No authentication required."
    )

    @PostMapping("/check-quote")
    public QuoteCheckResponse checkQuote(
            @Parameter(description = "UUID of the area, from GET /areas") @PathVariable UUID areaId,
            @Parameter(description = "One of the fixed house type categories, e.g. ONE_BEDROOM") @PathVariable HouseType houseType,
            @Valid @RequestBody QuoteCheckRequest request
    ) {
        return estimateService.checkQuote(areaId, houseType, request);
    }
}