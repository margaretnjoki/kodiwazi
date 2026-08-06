package com.margaretnjoki.kodiwazi.entity;

import com.margaretnjoki.kodiwazi.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contributors")
public class Contributor extends BaseEntity {

   @Column(name = "first_name", nullable = false)
   private String firstName;

   @Column(name = "last_name", nullable = false)
   private String lastName;

   @Column(name = "hashed_password", nullable = false)
   private String password;

@Column(nullable = false, unique = true)
   private String email;

   private boolean enabled  = true;


}
