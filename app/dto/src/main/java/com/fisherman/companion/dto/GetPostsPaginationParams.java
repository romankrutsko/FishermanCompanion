package com.fisherman.companion.dto;

public record GetPostsPaginationParams(
        int skip,
        int take
) {
}