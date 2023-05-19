package com.fisherman.companion.dto.request;

public record CreateUserRequest(
        String username,
        String password,
        String bio,
        String location,
        String contacts
) {
}
