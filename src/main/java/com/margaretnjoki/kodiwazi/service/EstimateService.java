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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class EstimateService {

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

        List<BigDecimal> included = submissions.stream()
                .filter(RentSubmission::isUtilitiesIncluded)
                .map(RentSubmission::getAmount)
                .toList();

        List<BigDecimal> excluded = submissions.stream()
                .filter(s -> !s.isUtilitiesIncluded())
                .map(RentSubmission::getAmount)
                .toList();

        return new RentEstimateResponse(
                area.getId(),
                area.getName(),
                houseType,
                toSegment(included),
                toSegment(excluded)
        );
    }

    private RentEstimateSegment toSegment(List<BigDecimal> amounts) {
        return new RentEstimateSegment(median(amounts), amounts.size());
    }

    private BigDecimal median(List<BigDecimal> amounts) {

        if (amounts.isEmpty()) {
            return null;
        }

        List<BigDecimal> sorted = amounts.stream()
                .sorted(Comparator.naturalOrder())
                .toList();

        int size = sorted.size();
        int middle = size / 2;

        if (size % 2 == 1) {
            return sorted.get(middle);
        }

        BigDecimal lower = sorted.get(middle - 1);
        BigDecimal upper = sorted.get(middle);
        return lower.add(upper).divide(BigDecimal.valueOf(2));
    }
}