package com.fisherman.companion.dto.request;

public record UpdatePostRequest(
        String categoryId,
        String title,
        String description,
        String startDate,
        String settlement,
        String contactInfo
) {
}
