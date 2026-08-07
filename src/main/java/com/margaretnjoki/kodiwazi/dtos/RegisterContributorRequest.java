package com.margaretnjoki.kodiwazi.dtos;

public record RegisterContributorRequest(
     String firstName,
     String lastName,
     String email,
     String password
) {
}
