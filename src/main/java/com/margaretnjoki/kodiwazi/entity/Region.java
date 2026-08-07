package com.margaretnjoki.kodiwazi.entity;

import com.margaretnjoki.kodiwazi.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region  extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
