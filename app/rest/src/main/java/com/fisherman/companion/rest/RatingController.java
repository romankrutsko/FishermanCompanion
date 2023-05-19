package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetDetailedRatingResponse;
import com.fisherman.companion.service.RatingService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    String rateUser(HttpServletRequest request, @RequestBody CreateRatingRequest ratingRequest) {
        return ratingService.rateUser(request, ratingRequest);
    }
    @GetMapping("/{userId}")
    GenericListResponse<GetDetailedRatingResponse> getUserDetailedRatings(@PathVariable(value = "userId") Long userId) {
        return ratingService.getDetailedRatingsByUserId(userId);
    }

    @DeleteMapping("/{ratingId}")
    void getUserDetailedRatings(HttpServletRequest request, @PathVariable(value = "ratingId") Long ratingId) {
        ratingService.deleteRatingById(request, ratingId);
    }
}
