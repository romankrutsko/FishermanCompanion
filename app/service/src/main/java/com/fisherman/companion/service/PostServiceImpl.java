package com.fisherman.companion.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.BoundingBoxDimensions;
import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.GetPostsPaginationParams;
import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.RequestDto;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsByCategoryRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;
import com.fisherman.companion.dto.response.UserResponse;
import com.fisherman.companion.persistence.CategoryRepository;
import com.fisherman.companion.persistence.PostRepository;
import com.fisherman.companion.persistence.RequestsRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final GeolocationService geolocationService;
    private final RequestsRepository requestsRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_DEGREE_LATITUDE = 111.2;


    @Override
    public PostResponse createPost(final HttpServletRequest request, final CreatePostRequest createPostRequest) {
        final UserDto user = tokenService.verifyAuthentication(request);

        final PostDto post = mapCreateRequestToPostDto(createPostRequest, user.id());

        final Long postId = postRepository.savePost(post);

        populatePostWithCategoryName(post);

        post.setId(postId);

        return convertToResponse(post);
    }

    private PostDto mapCreateRequestToPostDto(final CreatePostRequest createPostRequest, final Long userId) {
        final Geolocation geolocation = geolocationService.getCoordinates(createPostRequest.settlement());

        final CategoryDto category = new CategoryDto();

        final Long categoryId = Long.valueOf(createPostRequest.categoryId());

        category.setId(categoryId);

        final PostDto post = new PostDto();

        post.setUserId(userId);
        post.setCategory(category);
        post.setTitle(createPostRequest.title());
        post.setDescription(createPostRequest.description());
        post.setStartDate(LocalDateTime.parse(createPostRequest.startDate()));
        post.setLatitude(geolocation.latitude());
        post.setLongitude(geolocation.longitude());
        post.setContactInfo(createPostRequest.contactInfo());

        return post;
    }

    private void populatePostWithCategoryName(final PostDto post) {
        final String categoryName = categoryRepository.findCategoryNameById(post.getCategory().getId());

        post.getCategory().setName(categoryName);
    }

    private PostResponse convertToResponse(final PostDto postDto) {
        final UserResponse user = userService.findUserById(postDto.getUserId());

        final String settlement = geolocationService.getSettlementName(postDto.getLatitude(), postDto.getLongitude());

        return PostResponse.builder()
                           .id(postDto.getId())
                           .user(user)
                           .category(postDto.getCategory())
                           .title(postDto.getTitle())
                           .description(postDto.getDescription())
                           .startDate(postDto.getStartDate())
                           .settlement(settlement)
                           .contactInfo(postDto.getContactInfo())
                           .build();
    }

    @Override
    public GenericListResponse<PostResponse> findAllPosts(final int take, final int skip) {
        final LocalDateTime timeToShowPosts = LocalDateTime.now().plusHours(2);

        final GetPostsPaginationParams paginationParams = new GetPostsPaginationParams(skip, take, timeToShowPosts);

        final List<PostDto> posts = postRepository.findAllCategoriesPosts(paginationParams);

        final List<PostResponse> response = getPostResponses(posts);

        return GenericListResponse.of(response);
    }

    private List<PostResponse> getPostResponses(final List<PostDto> posts) {
        posts.forEach(this::populatePostWithCategoryName);

        return posts.stream()
                    .map(this::convertToResponse)
                    .toList();
    }

    @Override
    public PostResponse updatePostInfo(final HttpServletRequest request, final UpdatePostRequest updatePostRequest, final Long postId) {
        tokenService.verifyAuthentication(request);

        final PostDto post = mapUpdateRequestToPostDto(updatePostRequest, postId);

        populatePostWithCategoryName(post);

        postRepository.updatePostById(post);

        final PostDto updatedPost = postRepository.findPostById(postId);

        populatePostWithCategoryName(updatedPost);

        return convertToResponse(updatedPost);
    }

    private PostDto mapUpdateRequestToPostDto(final UpdatePostRequest updatePostRequest, final Long postId) {
        final Geolocation geolocation = geolocationService.getCoordinates(updatePostRequest.settlement());

        final CategoryDto category = new CategoryDto();

        final Long categoryId = Long.valueOf(updatePostRequest.categoryId());

        category.setId(categoryId);

        final PostDto post = new PostDto();

        final String startDate = updatePostRequest.startDate();

        post.setId(postId);
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
    public GenericListResponse<PostResponse> findPostsByCategory(final GetPostsByCategoryRequest getPostsByCategoryRequest, int skip, int take) {
        final boolean isSortedByRating = getPostsByCategoryRequest.sortByUserRating();
        final LocalDateTime timeToShowPosts = LocalDateTime.now().plusHours(2);

        final GetPostsPaginationParams paginationParams = new GetPostsPaginationParams(skip, take, timeToShowPosts);

        final List<PostDto> posts = postRepository.findPostsByCategory(paginationParams, getPostsByCategoryRequest.categoryId());

        final List<PostResponse> postResponses = getPostResponses(posts);

        return isSortedByRating ? sortByRatingAndStartTime(postResponses) : GenericListResponse.of(postResponses);
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

        final List<PostResponse> postsConvertedToResponse = getPostResponses(filteredByRadius);

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
                                         .thenComparingDouble(post -> Optional.ofNullable(post.user().getAverageRating())
                                                                              .orElse(0.0))
                                         .reversed())
                       .toList();

        return GenericListResponse.of(response);
    }

    @Override
    public void deletePostById(final HttpServletRequest request, final Long postId) {
        tokenService.verifyAuthentication(request);

        final List<RequestDto> requestsToPost = requestsRepository.getRequestsByPostId(postId);

        requestsToPost.forEach(requestDto -> requestsRepository.deleteRequest(requestDto.getId()));

        postRepository.deleteById(postId);
    }

    @Override
    public GenericListResponse<PostResponse> findUserPostsWithPagination(final HttpServletRequest request, final Long userId, final int take, final int skip) {
        tokenService.verifyAuthentication(request);

        final List<PostDto> listOfUserPosts = postRepository.findUserPostsWithPagination(userId, take, skip);

        final List<PostResponse> response = getPostResponses(listOfUserPosts);

        return GenericListResponse.of(response);
    }

    @Override
    public GenericListResponse<PostResponse> findUserFutureTravels(final HttpServletRequest request, final Long userId) {
        tokenService.verifyAuthentication(request);

        final List<PostDto> listOfUsersPosts = postRepository.findUserPostsWithFutureStartDate(userId);

        final List<PostDto> listOfPostsWithRequestsFromCurUser = postRepository.findPostsWithRequestsFromUserByUserId(userId);

        final List<PostDto> futureTravels = Stream.concat(listOfUsersPosts.stream(), listOfPostsWithRequestsFromCurUser.stream())
                     .sorted(Comparator.comparing(PostDto::getStartDate))
                     .toList();

        final List<PostResponse> converted = getPostResponses(futureTravels);

        return GenericListResponse.of(converted);
    }

    @Override
    public GenericListResponse<PostResponse> findUserFinishedTravels(final HttpServletRequest request, final Long userId, final Long postsAfterDaysToShow) {
        tokenService.verifyAuthentication(request);

        final LocalDateTime timeToFilterFinishedPosts = LocalDateTime.now().minusDays(postsAfterDaysToShow);

        final List<PostDto> listOfUsersPosts = postRepository.findUserPostsWithStartDateInPast(userId, timeToFilterFinishedPosts);

        final List<PostDto> listOfPostsWithRequestsFromCurUser = postRepository.findPostsWithRequestFromUserInPast(userId, timeToFilterFinishedPosts);

        final List<PostDto> futureTravels = Stream.concat(listOfUsersPosts.stream(), listOfPostsWithRequestsFromCurUser.stream())
                                                  .sorted(Comparator.comparing(PostDto::getStartDate)
                                                                    .reversed())
                                                  .toList();

        final List<PostResponse> converted = getPostResponses(futureTravels);

        return GenericListResponse.of(converted);
    }
}
