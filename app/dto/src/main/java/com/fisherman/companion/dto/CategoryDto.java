package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record CategoryDto(
        Long id,
        String name
) {
}
