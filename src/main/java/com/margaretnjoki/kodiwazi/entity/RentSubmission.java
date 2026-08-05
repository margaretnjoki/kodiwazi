package com.margaretnjoki.kodiwazi.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RentSubmission {
    private UUID id;
   private Contributor contributor;
   private Area area;
   private HouseType houseType;
   private BigDecimal amount;
   private Instant createdAt;
   private Instant updatedAt;
   private SubmissionStatus status;
   private boolean utilitiesIncluded;

}
