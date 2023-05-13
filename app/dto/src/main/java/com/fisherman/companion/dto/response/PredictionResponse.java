package com.fisherman.companion.dto.response;

import java.util.List;

import com.fisherman.companion.dto.Predictions;

public record PredictionResponse(
        String status,
        List<Predictions> predictions
) {

}

