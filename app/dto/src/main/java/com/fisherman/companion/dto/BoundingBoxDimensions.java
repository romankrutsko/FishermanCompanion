package com.fisherman.companion.dto;

import lombok.Builder;

@Builder
public record BoundingBoxDimensions(
        Double minLat,
        Double maxLat,
        Double minLng,
        Double maxLng
) {
}
