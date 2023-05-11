package com.fisherman.companion.dto.request;

public record CreatePostRequest(
        Long categoryId,
        String title,
        String description,
        String startDate,
        String coordinates,
        String contactInfo
) {
}
