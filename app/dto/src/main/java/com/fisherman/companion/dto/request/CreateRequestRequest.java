package com.fisherman.companion.dto.request;

public record CreateRequestRequest(
        Long userId,
        Long postId,
        String comment
) {
}
