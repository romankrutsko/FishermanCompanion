package com.fisherman.companion.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PostDto(
        Long id,
        Long userId,
        CategoryDto category,
        String title,
        String description,
        LocalDateTime startDate,
        Double latitude,
        Double longitude,
        String contactInfo,
        PostStatus status
) {}
