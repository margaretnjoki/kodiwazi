package com.margaretnjoki.kodiwazi.entity;

import com.margaretnjoki.kodiwazi.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "regions")
public class Region  extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
