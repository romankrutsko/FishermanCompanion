package com.fisherman.companion.dto;

public record BoundingBoxDimensions(
        Double minLat,
        Double maxLat,
        Double minLng,
        Double maxLng
) {
}
