package com.fisherman.companion.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
