package com.margaretnjoki.kodiwazi.controller;

import com.margaretnjoki.kodiwazi.dtos.RentSubmissionRequest;
import com.margaretnjoki.kodiwazi.dtos.RentSubmissionResponse;
import com.margaretnjoki.kodiwazi.service.RentSubmissionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rent-submissions")
public class RentSubmissionController {
    public final RentSubmissionService rentSubmissionService;

    public RentSubmissionController(RentSubmissionService rentSubmissionService) {
        this.rentSubmissionService = rentSubmissionService;
    }

    @PostMapping
    public RentSubmissionResponse submit(@RequestBody RentSubmissionRequest request, Authentication authentication) {
        String contributorEmail = authentication.getName();
        return rentSubmissionService.submit(request, contributorEmail);
    }


}
