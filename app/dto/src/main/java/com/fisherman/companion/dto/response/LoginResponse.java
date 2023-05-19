package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        UserResponse user,
        String token
) {
}
