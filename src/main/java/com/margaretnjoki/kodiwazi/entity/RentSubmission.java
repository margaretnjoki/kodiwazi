package com.margaretnjoki.kodiwazi.entity;

import com.margaretnjoki.kodiwazi.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rent_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentSubmission extends BaseEntity {

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "contributor_id")
   private Contributor contributor;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "area_id")
   private Area area;

   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   private HouseType houseType;

   @Column(name = "amount", nullable = false)
   private BigDecimal amount;

   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   private SubmissionStatus status;

   @Column(nullable = false)
   private boolean utilitiesIncluded;

}
