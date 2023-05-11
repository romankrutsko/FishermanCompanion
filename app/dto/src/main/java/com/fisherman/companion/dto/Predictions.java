package com.fisherman.companion.dto;

import java.util.List;

public record Predictions(
        String description,
        List<MatchedSubstring> matchedSubstrings,
        String placeId,
        List<String> types

) {
}
