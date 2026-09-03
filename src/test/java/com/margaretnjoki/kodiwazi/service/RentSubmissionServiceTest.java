package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.RentSubmissionRequest;
import com.margaretnjoki.kodiwazi.dtos.RentSubmissionResponse;
import com.margaretnjoki.kodiwazi.entity.*;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentSubmissionServiceTest {

    @Mock
    private RentSubmissionRepository rentSubmissionRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private ContributorRepository contributorRepository;

    @InjectMocks
    private RentSubmissionService rentSubmissionService;

    @Test
    void newSubmissionWildlyAboveExistingDataIsFlagged() {

        UUID areaId = UUID.randomUUID();
        UUID contributorId = UUID.randomUUID();

        Area area = Area.builder().name("Kilimani").build();
        area.setId(areaId);

        Contributor contributor = Contributor.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("hashed")
                .enabled(true)
                .build();
        contributor.setId(contributorId);

        RentSubmission e1 = existingSubmission(15000, false);
        RentSubmission e2 = existingSubmission(16000, false);
        RentSubmission e3 = existingSubmission(14500, false);

        when(contributorRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(contributor));
        when(areaRepository.findById(areaId))
                .thenReturn(Optional.of(area));
        when(rentSubmissionRepository
                .findByContributorIdAndAreaIdAndHouseTypeAndUtilitiesIncludedAndStatus(
                        any(), any(), any(), anyBoolean(), any()
                ))
                .thenReturn(List.of());
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(List.of(e1, e2, e3));
        when(rentSubmissionRepository.save(any(RentSubmission.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RentSubmissionRequest request = new RentSubmissionRequest(
                areaId, HouseType.ONE_BEDROOM, BigDecimal.valueOf(500000), false
        );

        RentSubmissionResponse response = rentSubmissionService.submit(request, "test@example.com");

        assertThat(response.status()).isEqualTo(SubmissionStatus.FLAGGED);
    }

    private RentSubmission existingSubmission(double amount, boolean utilitiesIncluded) {
        return RentSubmission.builder()
                .amount(BigDecimal.valueOf(amount))
                .utilitiesIncluded(utilitiesIncluded)
                .status(SubmissionStatus.ACTIVE)
                .build();
    }
}