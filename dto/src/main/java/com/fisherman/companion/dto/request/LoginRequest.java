package com.fisherman.companion.dto.request;

import lombok.Getter;

@Getter
public record LoginRequest(
        String username,
        String password
) {
}
