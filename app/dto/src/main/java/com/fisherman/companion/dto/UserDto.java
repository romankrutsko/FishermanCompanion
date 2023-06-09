package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String password,
        String avatar,
        String bio,
        String location,
        String contacts,
        String role
) {
}
