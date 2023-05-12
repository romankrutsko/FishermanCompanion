package com.fisherman.companion.dto.request;

public record CreatePostRequest(
        Long categoryId,
        String title,
        String description,
        String startDate,
        String latitude,
        String longitude,
        String contactInfo
) {
}
