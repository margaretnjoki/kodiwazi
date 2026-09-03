package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.QuoteCheckRequest;
import com.margaretnjoki.kodiwazi.dtos.QuoteCheckResponse;
import com.margaretnjoki.kodiwazi.dtos.RentEstimateResponse;
import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.entity.RentSubmission;
import com.margaretnjoki.kodiwazi.entity.SubmissionStatus;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstimateServiceTest {

    @Mock
    private RentSubmissionRepository rentSubmissionRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private EstimateService estimateService;

    @Test
    void medianOfOddCountReturnsExactMiddleValue() {

        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().name("Kilimani").build();
        area.setId(areaId);

        RentSubmission s1 = submission(8000, true, Instant.now());
        RentSubmission s2 = submission(8500, true, Instant.now());
        RentSubmission s3 = submission(9000, true, Instant.now());
        RentSubmission s4 = submission(9200, true, Instant.now());
        RentSubmission s5 = submission(45000, true, Instant.now());

        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(List.of(s1, s2, s3, s4, s5));

        RentEstimateResponse response = estimateService.getEstimate(areaId, HouseType.ONE_BEDROOM);

        assertThat(response.utilitiesIncluded().medianAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(9000));
        assertThat(response.utilitiesIncluded().sampleSize()).isEqualTo(5);
    }

    private RentSubmission submission(double amount, boolean utilitiesIncluded, Instant createdAt) {
        RentSubmission submission = RentSubmission.builder()
                .amount(BigDecimal.valueOf(amount))
                .utilitiesIncluded(utilitiesIncluded)
                .status(SubmissionStatus.ACTIVE)
                .build();
        submission.setCreatedAt(createdAt);
        return submission;
    }

    @Test
    void oldOutlierSubmissionsHaveLessInfluenceThanFreshOnes() {
        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().name("Kilimani").build();
        area.setId(areaId);

        RentSubmission old1 = submission(5000, true, Instant.now().minus(365, ChronoUnit.DAYS));
        RentSubmission old2 = submission(5000, true, Instant.now().minus(365, ChronoUnit.DAYS));
        RentSubmission old3 = submission(5000, true, Instant.now().minus(365, ChronoUnit.DAYS));

        RentSubmission fresh1 = submission(20000, true, Instant.now());
        RentSubmission fresh2 = submission(20000, true, Instant.now());

        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(List.of(old1, old2, old3, fresh1, fresh2));

        RentEstimateResponse response = estimateService.getEstimate(areaId, HouseType.ONE_BEDROOM);

        assertThat(response.utilitiesIncluded().medianAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(20000));
    }

    @Test
    void sameMediansButDifferentConsistencyProduceDifferentConfidence() {

        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().name("Kilimani").build();
        area.setId(areaId);

        List<RentSubmission> tight = List.of(
                submission(14800, true, Instant.now()),
                submission(14900, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15100, true, Instant.now()),
                submission(15100, true, Instant.now()),
                submission(15200, true, Instant.now())
        );

        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(tight);

        RentEstimateResponse tightResponse = estimateService.getEstimate(areaId, HouseType.ONE_BEDROOM);

        UUID areaId2 = UUID.randomUUID();
        Area area2 = Area.builder().name("Westlands").build();
        area2.setId(areaId2);

        List<RentSubmission> scattered = List.of(
                submission(5000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(30000, true, Instant.now())
        );

        when(areaRepository.findById(areaId2)).thenReturn(Optional.of(area2));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId2, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(scattered);

        RentEstimateResponse scatteredResponse = estimateService.getEstimate(areaId2, HouseType.ONE_BEDROOM);

        assertThat(tightResponse.utilitiesIncluded().medianAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(scatteredResponse.utilitiesIncluded().medianAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(15000));

        assertThat(tightResponse.utilitiesIncluded().confidenceScore())
                .isGreaterThan(scatteredResponse.utilitiesIncluded().confidenceScore());
    }

    @Test
    void quoteFarAboveMedianIsClassifiedAsSignificantlyAboveTypical() {

        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().name("Kilimani").build();
        area.setId(areaId);

        List<RentSubmission> submissions = List.of(
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now()),
                submission(15000, true, Instant.now())
        );

        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.ONE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(submissions);

        QuoteCheckRequest request = new QuoteCheckRequest(BigDecimal.valueOf(30000), true);

        QuoteCheckResponse response = estimateService.checkQuote(areaId, HouseType.ONE_BEDROOM, request);

        assertThat(response.verdict()).isEqualTo("SIGNIFICANTLY_ABOVE_TYPICAL");
        assertThat(response.medianAmount()).isEqualByComparingTo(BigDecimal.valueOf(15000));
    }

    @Test
    void segmentWithNoSubmissionsReturnsHonestNullMedian() {

        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().name("Karen").build();
        area.setId(areaId);

        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(rentSubmissionRepository.findByAreaIdAndHouseTypeAndStatus(
                areaId, HouseType.THREE_BEDROOM, SubmissionStatus.ACTIVE
        )).thenReturn(List.of());

        RentEstimateResponse response = estimateService.getEstimate(areaId, HouseType.THREE_BEDROOM);

        assertThat(response.utilitiesIncluded().medianAmount()).isNull();
        assertThat(response.utilitiesIncluded().sampleSize()).isEqualTo(0);
        assertThat(response.utilitiesExcluded().medianAmount()).isNull();
        assertThat(response.utilitiesExcluded().sampleSize()).isEqualTo(0);
    }
}