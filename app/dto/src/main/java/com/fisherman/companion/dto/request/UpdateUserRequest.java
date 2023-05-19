package com.fisherman.companion.dto.request;

public record UpdateUserRequest(
        String password,
        String bio,
        String location,
        String contacts
) {
}
