package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetDetailedRatingResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RatingService {
    String rateUser(HttpServletRequest request, CreateRatingRequest ratingRequest);

    Double getUserAverageRatingByUserId(Long userId);

    GenericListResponse<GetDetailedRatingResponse> getDetailedRatingsByUserId(Long userId);

    void deleteRatingById(HttpServletRequest request, Long ratingId);
}
