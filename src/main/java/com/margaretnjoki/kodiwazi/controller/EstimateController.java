package com.margaretnjoki.kodiwazi.controller;

import com.margaretnjoki.kodiwazi.dtos.QuoteCheckRequest;
import com.margaretnjoki.kodiwazi.dtos.QuoteCheckResponse;
import com.margaretnjoki.kodiwazi.dtos.RentEstimateResponse;
import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.service.EstimateService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/areas/{areaId}/house-types/{houseType}")
public class EstimateController {

    private final EstimateService estimateService;

    public EstimateController(EstimateService estimateService) {
        this.estimateService = estimateService;
    }

    @GetMapping("/estimate")
    public RentEstimateResponse getEstimate(
            @PathVariable UUID areaId,
            @PathVariable HouseType houseType
    ) {
        return estimateService.getEstimate(areaId, houseType);
    }

    @PostMapping("/check-quote")
    public QuoteCheckResponse checkQuote(
            @PathVariable UUID areaId,
            @PathVariable HouseType houseType,
            @RequestBody QuoteCheckRequest request
    ) {
        return estimateService.checkQuote(areaId, houseType, request);
    }
}