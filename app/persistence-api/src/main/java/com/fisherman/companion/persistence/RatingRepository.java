package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.RatingDto;
import com.fisherman.companion.dto.request.CreateRatingRequest;

public interface RatingRepository {

    void createRating(CreateRatingRequest request, Long ratedBy);

    Double getAverageRatingForUser(Long userId);

    List<RatingDto> getRatingsWithCommentsForUser(Long userId);
}
