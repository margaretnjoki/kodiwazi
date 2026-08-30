package com.margaretnjoki.kodiwazi.controller;

import com.margaretnjoki.kodiwazi.dtos.RentSubmissionRequest;
import com.margaretnjoki.kodiwazi.dtos.RentSubmissionResponse;
import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.service.RentSubmissionService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/rent-submissions")
public class RentSubmissionController {
    public final RentSubmissionService rentSubmissionService;

    public RentSubmissionController(RentSubmissionService rentSubmissionService) {
        this.rentSubmissionService = rentSubmissionService;
    }

    @PostMapping
    public RentSubmissionResponse submit(@Valid @RequestBody RentSubmissionRequest request, Authentication authentication) {
        String contributorEmail = authentication.getName();
        return rentSubmissionService.submit(request, contributorEmail);
    }

    @GetMapping
    public List<RentSubmissionResponse> listSubmissions(
            @RequestParam(required = false) UUID areaId,
            @RequestParam(required = false) HouseType houseType
    ) {
        return rentSubmissionService.listSubmissions(areaId, houseType);
    }


}
