package com.fisherman.companion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.RatingDto;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetDetailedRatingResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.RatingRepository;
import com.fisherman.companion.persistence.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public String rateUser(final HttpServletRequest request, final CreateRatingRequest ratingRequest) {
        final UserDto user = tokenService.verifyAuthentication(request);

        ratingRepository.createRating(ratingRequest, user.id());

        return ResponseStatus.USER_RATED_SUCCESSFULLY.getCode();
    }

    @Override
    public Double getUserAverageRatingByUserId(final Long userId) {
        return getAverageRating(userId);
    }

    private Double getAverageRating(final Long userId) {
        return ratingRepository.getAverageRatingForUser(userId);
    }

    @Override
    public GenericListResponse<GetDetailedRatingResponse> getDetailedRatingsByUserId(final Long userId) {
        return getDetailedRating(userId);
    }

    private GenericListResponse<GetDetailedRatingResponse> getDetailedRating(final Long userId) {
        final List<RatingDto> ratings = ratingRepository.getRatingsWithCommentsForUser(userId);

        return GenericListResponse.of(ratings.stream().map(this::populateWithUsername).toList());
    }

    private GetDetailedRatingResponse populateWithUsername(final RatingDto ratingDto) {
        final Long userId = ratingDto.ratedBy();

        final UserDto ratedByUser = userRepository.findUserById(userId);

        return GetDetailedRatingResponse.builder()
                                        .id(ratingDto.id())
                                        .userId(ratingDto.userId())
                                        .userIdRatedBy(ratingDto.ratedBy())
                                        .usernameRatedBy(ratedByUser.username())
                                        .rating(ratingDto.rating())
                                        .comment(ratingDto.comment())
                                        .build();
    }

    @Override
    public void deleteRatingById(final HttpServletRequest request, final Long ratingId) {
        tokenService.verifyAuthentication(request);

        ratingRepository.deleteRatingById(ratingId);
    }
}
