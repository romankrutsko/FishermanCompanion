package com.fisherman.companion.dto.request;

public record GetPostsByCategoryRequest(
        Long categoryId,
        Boolean sortByUserRating
) {
}