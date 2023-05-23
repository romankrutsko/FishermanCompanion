package com.fisherman.companion.dto.request;

public record CreateRatingRequest(
        Long userId,
        Long postId,
        int rating,
        String comment
) {
}
