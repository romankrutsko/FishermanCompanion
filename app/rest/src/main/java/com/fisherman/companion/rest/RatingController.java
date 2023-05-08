package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetAverageRatingResponse;
import com.fisherman.companion.dto.response.GetDetailedRatingResponse;
import com.fisherman.companion.service.RatingService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/rate")
    String rateUser(HttpServletRequest request, @RequestBody CreateRatingRequest ratingRequest) {
        return ratingService.rateUser(request, ratingRequest);
    }

    @GetMapping("/average/my")
    GetAverageRatingResponse getMyAverageRating(HttpServletRequest request) {
        return ratingService.getMyAverageRating(request);
    }

    @GetMapping("/average/{userId}")
    GetAverageRatingResponse getUserAverageRating(@PathVariable(value = "userId") Long userId) {
        return ratingService.getUserAverageRatingByUserId(userId);
    }
    @GetMapping("/detailed/my")
    GenericListResponse<GetDetailedRatingResponse> getMyDetailedRatings(HttpServletRequest request) {
        return ratingService.getMyDetailedRatings(request);
    }

    @GetMapping("/detailed/{userId}")
    GenericListResponse<GetDetailedRatingResponse> getUserDetailedRatings(@PathVariable(value = "userId") Long userId) {
        return ratingService.getDetailedRatingsByUserId(userId);
    }
}
