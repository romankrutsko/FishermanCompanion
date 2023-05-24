package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record GetUserToRateResponse(
        UserResponse user,
        boolean canBeRated
) {
}
