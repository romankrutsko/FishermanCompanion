package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record RatingDto(
        Long id,
        Long userId,
        Long ratedBy,
        Integer rating,
        String comment
) {
}
