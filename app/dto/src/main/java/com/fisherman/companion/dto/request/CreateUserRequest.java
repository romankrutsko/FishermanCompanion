package com.fisherman.companion.dto.request;

public record CreateUserRequest(
        String username,
        String email,
        String password
) {
}
