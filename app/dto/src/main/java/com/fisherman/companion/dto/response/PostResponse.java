package com.fisherman.companion.dto.response;

import java.time.LocalDateTime;

import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.PostStatus;

import lombok.Builder;

@Builder
public record PostResponse(
        Long id,
        Long userId,
        CategoryDto category,
        String title,
        String description,
        LocalDateTime startDate,
        String settlement,
        String contactInfo,
        PostStatus status
) {
}
