package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetAverageRatingResponse;
import com.fisherman.companion.dto.response.GetDetailedRatingResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RatingService {
    String rateUser(HttpServletRequest request, CreateRatingRequest ratingRequest);

    GetAverageRatingResponse getMyAverageRating(HttpServletRequest request);

    GetAverageRatingResponse getUserAverageRatingByUserId(Long userId);

    GenericListResponse<GetDetailedRatingResponse> getMyDetailedRatings(HttpServletRequest request);

    GenericListResponse<GetDetailedRatingResponse> getDetailedRatingsByUserId(Long userId);

    void deleteRatingById(HttpServletRequest request, Long ratingId);
}
