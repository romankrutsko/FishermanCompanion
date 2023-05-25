package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record RequestFullDetailsResponse(
        RequestResponse request,
        UserResponse user,
        PostResponse post
) {
}