package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String token,
        String role
) {
}
