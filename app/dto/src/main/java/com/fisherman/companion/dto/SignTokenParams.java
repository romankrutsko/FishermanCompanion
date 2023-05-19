package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record SignTokenParams(
        Long id,
        String username,
        String role
) {
}
