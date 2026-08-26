package com.margaretnjoki.kodiwazi.service;

import com.margaretnjoki.kodiwazi.dtos.RentEstimateResponse;
import com.margaretnjoki.kodiwazi.dtos.RentEstimateSegment;
import com.margaretnjoki.kodiwazi.entity.Area;
import com.margaretnjoki.kodiwazi.entity.HouseType;
import com.margaretnjoki.kodiwazi.entity.RentSubmission;
import com.margaretnjoki.kodiwazi.entity.SubmissionStatus;
import com.margaretnjoki.kodiwazi.repository.AreaRepository;
import com.margaretnjoki.kodiwazi.repository.RentSubmissionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class EstimateService {

    private static final double HALF_LIFE_DAYS = 90.0;
    private static final double FULL_CONFIDENCE_SAMPLE_SIZE = 10.0;

    private final RentSubmissionRepository rentSubmissionRepository;
    private final AreaRepository areaRepository;

    public EstimateService(
            RentSubmissionRepository rentSubmissionRepository,
            AreaRepository areaRepository
    ) {
        this.rentSubmissionRepository = rentSubmissionRepository;
        this.areaRepository = areaRepository;
    }

    public RentEstimateResponse getEstimate(UUID areaId, HouseType houseType) {

        Area area = areaRepository
                .findById(areaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Area not found")
                );

        List<RentSubmission> submissions = rentSubmissionRepository
                .findByAreaIdAndHouseTypeAndStatus(areaId, houseType, SubmissionStatus.ACTIVE);

        List<RentSubmission> included = submissions.stream()
                .filter(RentSubmission::isUtilitiesIncluded)
                .toList();

        List<RentSubmission> excluded = submissions.stream()
                .filter(s -> !s.isUtilitiesIncluded())
                .toList();

        return new RentEstimateResponse(
                area.getId(),
                area.getName(),
                houseType,
                toSegment(included),
                toSegment(excluded)
        );
    }

    private RentEstimateSegment toSegment(List<RentSubmission> submissions) {

        if (submissions.isEmpty()) {
            return new RentEstimateSegment(null, 0, 0.0, "LOW");
        }

        List<WeightedAmount> weighted = submissions.stream()
                .map(s -> new WeightedAmount(s.getAmount(), decayWeight(s.getCreatedAt())))
                .toList();

        BigDecimal median = weightedMedian(weighted);
        double confidenceScore = confidenceScore(weighted);
        String confidenceLabel = confidenceLabel(confidenceScore);

        return new RentEstimateSegment(median, submissions.size(), confidenceScore, confidenceLabel);
    }


    private double decayWeight(Instant submittedAt) {
        long daysOld = Duration.between(submittedAt, Instant.now()).toDays();
        return Math.pow(0.5, daysOld / HALF_LIFE_DAYS);
    }


    private BigDecimal weightedMedian(List<WeightedAmount> weighted) {

        List<WeightedAmount> sorted = weighted.stream()
                .sorted(Comparator.comparing(WeightedAmount::amount))
                .toList();

        double totalWeight = sorted.stream()
                .mapToDouble(WeightedAmount::weight)
                .sum();

        double halfway = totalWeight / 2.0;
        double cumulative = 0.0;

        for (WeightedAmount w : sorted) {
            cumulative += w.weight();
            if (cumulative >= halfway) {
                return w.amount();
            }
        }

        return sorted.get(sorted.size() - 1).amount();
    }


    private double confidenceScore(List<WeightedAmount> weighted) {

        double sampleScore = sampleSizeScore(weighted.size());
        double recencyScore = recencyScore(weighted);
        double consistencyScore = consistencyScore(weighted);

        double overall = (sampleScore + recencyScore + consistencyScore) / 3.0;

        return round(overall);
    }

    private double sampleSizeScore(int count) {
        return Math.min(count / FULL_CONFIDENCE_SAMPLE_SIZE, 1.0) * 100.0;
    }

    private double recencyScore(List<WeightedAmount> weighted) {
        double averageWeight = weighted.stream()
                .mapToDouble(WeightedAmount::weight)
                .average()
                .orElse(0.0);
        return averageWeight * 100.0;
    }

    private double consistencyScore(List<WeightedAmount> weighted) {

        if (weighted.size() == 1) {
            return 100.0;
        }

        double mean = weighted.stream()
                .mapToDouble(w -> w.amount().doubleValue())
                .average()
                .orElse(0.0);

        if (mean == 0.0) {
            return 0.0;
        }

        double variance = weighted.stream()
                .mapToDouble(w -> Math.pow(w.amount().doubleValue() - mean, 2))
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = stdDev / mean;

        double score = 100.0 - (coefficientOfVariation * 100.0);
        return Math.max(0.0, Math.min(100.0, score));
    }

    private String confidenceLabel(double score) {
        if (score >= 70.0) return "HIGH";
        if (score >= 40.0) return "MEDIUM";
        return "LOW";
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private record WeightedAmount(BigDecimal amount, double weight) {
    }
}