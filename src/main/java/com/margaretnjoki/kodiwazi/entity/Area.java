package com.margaretnjoki.kodiwazi.entity;

import com.margaretnjoki.kodiwazi.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name= "region_id")
    private Region region;
}
