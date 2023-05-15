package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record GetDetailedRatingResponse(
        Long id,
        Long userId,
        Long userIdRatedBy,
        String usernameRatedBy,
        int rating,
        String comment
) {
}
