package com.fisherman.companion.dto.request;

public record UpdatePostRequest(
        Long postId,
        Long categoryId,
        String title,
        String description,
        String startDate,
        String coordinates,
        String contactInfo
) {
}
