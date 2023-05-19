package com.fisherman.companion.dto;

import java.time.LocalDateTime;

public record GetPostsPaginationParams(
        int skip,
        int take,
        LocalDateTime timeToFilter
) {
}