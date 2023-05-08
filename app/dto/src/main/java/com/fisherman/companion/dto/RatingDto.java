package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record RatingDto(
        Long ratedBy,
        Integer rating,
        String comment
) {
}
