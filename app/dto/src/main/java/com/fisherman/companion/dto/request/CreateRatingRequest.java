package com.fisherman.companion.dto.request;

public record CreateRatingRequest(
        Long userId,
        int rating,
        String comment
) {
}
