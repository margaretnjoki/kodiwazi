package com.margaretnjoki.kodiwazi.dtos;

public record LoginRequest(
        String email,
        String password
) {
}