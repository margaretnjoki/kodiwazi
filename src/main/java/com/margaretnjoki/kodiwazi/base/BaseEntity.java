package com.margaretnjoki.kodiwazi.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue
   private UUID id;

    @CreationTimestamp
   private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
