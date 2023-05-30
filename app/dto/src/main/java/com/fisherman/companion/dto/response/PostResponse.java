package com.fisherman.companion.dto.response;

import com.fisherman.companion.dto.CategoryDto;

import lombok.Builder;

@Builder
public record PostResponse(
        Long id,
        UserResponse user,
        CategoryDto category,
        String title,
        String description,
        String startDate,
        String settlement,
        String contactInfo,
        boolean canRespond
) {
}
