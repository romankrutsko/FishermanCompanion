package com.fisherman.companion.dto.request;

public record GetPostsInRadiusByCategoryRequest(
        Double latitude,
        Double longitude,
        Double radius,
        Long categoryId,
        Boolean sortByUserRating
) {
}