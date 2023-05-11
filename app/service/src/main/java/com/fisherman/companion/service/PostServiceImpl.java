package com.fisherman.companion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.PostStatus;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.AllCategoriesPostsRequest;
import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.PostRepository;
import com.fisherman.companion.service.exception.RequestException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CookieService cookieService;

    @Override
    public void createPost(final HttpServletRequest request, final CreatePostRequest createPostRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final PostDto post = mapCreateRequestToPostDto(createPostRequest, user.getId());

        postRepository.savePost(post);
    }

    private PostDto mapCreateRequestToPostDto(final CreatePostRequest createPostRequest, final Long userId) {
        final String coordinates = createPostRequest.coordinates();

        if (coordinates != null && areCoordinatesInvalid(coordinates)) {
            throw new RequestException(ResponseStatus.INVALID_COORDINATES.getCode());
        }

        return PostDto.builder()
                      .userId(userId)
                      .category(CategoryDto.builder().id(createPostRequest.categoryId()).build())
                      .title(createPostRequest.title())
                      .description(createPostRequest.description())
                      .startDate(LocalDateTime.parse(createPostRequest.startDate()))
                      .latitude(Optional.ofNullable(coordinates)
                                        .map(c -> Double.parseDouble(c.split(",")[0]))
                                        .orElse(null))
                      .longitude(Optional.ofNullable(coordinates)
                                         .map(c -> Double.parseDouble(c.split(",")[1]))
                                         .orElse(null))
                      .contactInfo(createPostRequest.contactInfo())
                      .status(PostStatus.OPEN)
                      .build();
    }

    private boolean areCoordinatesInvalid(String coordinates) {
        if (coordinates == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)\\s*,\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
        Matcher matcher = pattern.matcher(coordinates);

        return !matcher.matches();
    }

    @Override
    public List<PostDto> findAllPosts(final AllCategoriesPostsRequest postsRequest) {
        final LocalDateTime timeToShowPosts = LocalDateTime.now().plusHours(2);

        return postRepository.findAllCategoriesPosts(postsRequest.take(), postsRequest.skip(), timeToShowPosts);
    }

    @Override
    public void updatePostInfo(final HttpServletRequest request, final UpdatePostRequest updatePostRequest) {
        cookieService.verifyAuthentication(request);

        final PostDto post = mapUpdateRequestToPostDto(updatePostRequest);

        postRepository.updatePostById(post);
    }

    private PostDto mapUpdateRequestToPostDto(final UpdatePostRequest updatePostRequest) {
        final String coordinates = updatePostRequest.coordinates();

        if (coordinates != null && areCoordinatesInvalid(coordinates)) {
            throw new RequestException(ResponseStatus.INVALID_COORDINATES.getCode());
        }

        return PostDto.builder()
                      .id(updatePostRequest.postId())
                      .category(CategoryDto.builder().id(updatePostRequest.categoryId()).build())
                      .title(updatePostRequest.title())
                      .description(updatePostRequest.description())
                      .startDate(LocalDateTime.parse(updatePostRequest.startDate()))
                      .latitude(Optional.ofNullable(coordinates)
                                        .map(c -> Double.parseDouble(c.split(",")[0]))
                                        .orElse(null))
                      .longitude(Optional.ofNullable(coordinates)
                                         .map(c -> Double.parseDouble(c.split(",")[1]))
                                         .orElse(null))
                      .contactInfo(updatePostRequest.contactInfo())
                      .status(PostStatus.OPEN)
                      .build();
    }

}
