package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record RequestResponse(
        Long id,
        Long userId,
        Long postId,
        String comment,
        String status
) {
}
