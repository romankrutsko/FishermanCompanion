package com.fisherman.companion.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.BoundingBoxDimensions;
import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.PostStatus;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;
import com.fisherman.companion.persistence.CategoriesRepository;
import com.fisherman.companion.persistence.PostRepository;
import com.fisherman.companion.persistence.RatingRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoriesRepository categoriesRepository;
    private final RatingRepository ratingRepository;
    private final CookieService cookieService;
    private final GeolocationService geolocationService;

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_DEGREE_LATITUDE = 111.2;


    @Override
    public PostResponse createPost(final HttpServletRequest request, final CreatePostRequest createPostRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final PostDto post = mapCreateRequestToPostDto(createPostRequest, user.getId());

        Long postId = postRepository.savePost(post);

        populatePostWithCategoryName(post);

        post.setId(postId);

        return convertToResponse(post);
    }

    private PostDto mapCreateRequestToPostDto(final CreatePostRequest createPostRequest, final Long userId) {
        final Geolocation geolocation = geolocationService.getCoordinates(createPostRequest.settlement());

        final CategoryDto category = new CategoryDto();

        category.setId(createPostRequest.categoryId());

        final PostDto post = new PostDto();

        post.setUserId(userId);
        post.setCategory(category);
        post.setTitle(createPostRequest.title());
        post.setDescription(createPostRequest.description());
        post.setStartDate(LocalDateTime.parse(createPostRequest.startDate()));
        post.setLatitude(geolocation.latitude());
        post.setLongitude(geolocation.longitude());
        post.setContactInfo(createPostRequest.contactInfo());
        post.setStatus(PostStatus.OPEN);

        return post;
    }

    private void populatePostWithCategoryName(final PostDto post) {
        final String categoryName = categoriesRepository.findCategoryNameById(post.getCategory().getId());

        post.getCategory().setName(categoryName);
    }

    private PostResponse convertToResponse(final PostDto postDto) {
        final String settlement = geolocationService.getSettlementName(postDto.getLatitude(), postDto.getLongitude());

        return PostResponse.builder()
                           .id(postDto.getId())
                           .userId(postDto.getUserId())
                           .category(postDto.getCategory())
                           .title(postDto.getTitle())
                           .description(postDto.getDescription())
                           .startDate(postDto.getStartDate())
                           .settlement(settlement)
                           .contactInfo(postDto.getContactInfo())
                           .status(postDto.getStatus())
                           .build();
    }

    @Override
    public GenericListResponse<PostResponse> findAllPosts(final int take, final int skip) {
        final LocalDateTime timeToShowPosts = LocalDateTime.now().plusHours(2);

        final List<PostDto> posts = postRepository.findAllCategoriesPosts(take, skip, timeToShowPosts);

        final List<PostResponse> response = posts.stream()
                    .map(this::convertToResponse)
                    .toList();

        return GenericListResponse.of(response);
    }

    @Override
    public void updatePostInfo(final HttpServletRequest request, final UpdatePostRequest updatePostRequest) {
        cookieService.verifyAuthentication(request);

        final PostDto post = mapUpdateRequestToPostDto(updatePostRequest);

        populatePostWithCategoryName(post);

        postRepository.updatePostById(post);
    }

    private PostDto mapUpdateRequestToPostDto(final UpdatePostRequest updatePostRequest) {
        final Geolocation geolocation = geolocationService.getCoordinates(updatePostRequest.settlement());

        final CategoryDto category = new CategoryDto();

        category.setId(updatePostRequest.categoryId());

        final PostDto post = new PostDto();

        String startDate = updatePostRequest.startDate();

        post.setId(updatePostRequest.postId());
        post.setCategory(category);
        post.setTitle(updatePostRequest.title());
        post.setDescription(updatePostRequest.description());
        post.setStartDate(Optional.ofNullable(startDate).map(LocalDateTime::parse).orElse(null));
        post.setLatitude(geolocation.latitude());
        post.setLongitude(geolocation.longitude());
        post.setContactInfo(updatePostRequest.contactInfo());

        return post;
    }

    @Override
    public GenericListResponse<PostResponse> findPostsNearLocation(final GetPostsInRadiusByCategoryRequest getPostsInRadiusRequest) {
        final Double lat = getPostsInRadiusRequest.latitude();
        final Double lng = getPostsInRadiusRequest.longitude();
        final Double radius = getPostsInRadiusRequest.radius();
        final Long categoryId = getPostsInRadiusRequest.categoryId();
        final boolean isSortedByRating = getPostsInRadiusRequest.sortByUserRating();

        final BoundingBoxDimensions boxDimensions = setBoundingBoxDimensions(lat, lng, radius);

        final LocalDateTime curTimePlusTwoHour = LocalDateTime.now().plusHours(2);
        final LocalDateTime curTimePlusTwoDays = curTimePlusTwoHour.plusDays(2);

        final List<PostDto> postsInBoundingBox = postRepository.findPostsInBoundingBoxByCategory(boxDimensions, curTimePlusTwoHour, curTimePlusTwoDays, categoryId);

        final List<PostDto> filteredByRadius = postsInBoundingBox.stream()
                                                                 .filter(post -> calcDistanceByHaversineInKm(lat, lng, post.getLatitude(), post.getLongitude()) <= radius)
                                                                 .toList();

        final List<PostResponse> postsConvertedToResponse = filteredByRadius.stream()
                                                                 .map(this::convertToResponse)
                                                                 .toList();

        return isSortedByRating ? sortByRatingAndStartTime(postsConvertedToResponse) : GenericListResponse.of(postsConvertedToResponse);
    }

    private BoundingBoxDimensions setBoundingBoxDimensions(final Double lat, final Double lng, final Double radius) {
        final double distanceInKm = radius / KM_PER_DEGREE_LATITUDE;
        final Double longitudeDegreeDistance = distanceInKm / Math.cos(Math.toRadians(lat));

        final Double minLat = lat - distanceInKm;
        final Double maxLat = lat + distanceInKm;
        final Double minLng = lng - longitudeDegreeDistance;
        final Double maxLng = lng + longitudeDegreeDistance;

        return BoundingBoxDimensions.builder()
                                    .minLat(minLat)
                                    .maxLat(maxLat)
                                    .minLng(minLng)
                                    .maxLng(maxLng)
                                    .build();
    }

    private double calcDistanceByHaversineInKm(final double latCurUsr, final double lngCurUsr, final double latPostUsr, final double lngPostUsr) {
        /* Haversine formula
            a = sin²(ΔlatDifference/2) + cos(lat1).cos(lat2).sin²(ΔlngDifference/2)
            c = 2.atan2(√a, √(1−a))
            d = R.c
        */

        final double lat1Rad = Math.toRadians(latCurUsr);
        final double lat2Rad = Math.toRadians(latPostUsr);
        final double latDiffRad = Math.toRadians(latPostUsr - latCurUsr);
        final double lngDiffRad = Math.toRadians(lngPostUsr - lngCurUsr);

        final double a = Math.sin(latDiffRad / 2) * Math.sin(latDiffRad / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(lngDiffRad / 2) * Math.sin(lngDiffRad / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public GenericListResponse<PostResponse> sortByRatingAndStartTime(final List<PostResponse> postList) {
        final List<PostResponse> response = postList.stream()
                       .sorted(Comparator.comparing(PostResponse::startDate)
                                         .thenComparingDouble(post -> Optional.ofNullable(ratingRepository.getAverageRatingForUser(post.userId()))
                                                                              .orElse(0.0)))
                       .toList();

        return GenericListResponse.of(response);
    }

    @Override
    public void deletePostById(final HttpServletRequest request, final Long postId) {
        final UserDto user = cookieService.verifyAuthentication(request);

        postRepository.deleteById(postId, user.getId());
    }

    @Override
    public GenericListResponse<PostResponse> findUserPostsWithPagination(final HttpServletRequest request, final int take, final int skip) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final List<PostDto> listOfUserPosts = postRepository.findUserPosts(user.getId(), take, skip);

        final List<PostResponse> response = listOfUserPosts.stream()
                              .map(this::convertToResponse)
                              .toList();

        return GenericListResponse.of(response);
    }
}
