package com.fisherman.companion.dto.response;

import lombok.Builder;

@Builder
public record GetProfileResponse(
        String fullName,
        String avatar,
        String bio,
        String location,
        String contacts
) {
}
