package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsByCategoryRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PostService {
    PostResponse createPost(HttpServletRequest request, CreatePostRequest createPostRequest);

    PostResponse findPostById(Long postId);

    GenericListResponse<PostResponse> findAllPosts(HttpServletRequest request, int take, int skip);

    PostResponse updatePostInfo(HttpServletRequest request, UpdatePostRequest updatePostRequest, Long postId);

    GenericListResponse<PostResponse> findPostsByCategory(HttpServletRequest request, GetPostsByCategoryRequest getPostsByCategoryRequest, int skip, int take);

    GenericListResponse<PostResponse> findPostsNearLocation(HttpServletRequest request, GetPostsInRadiusByCategoryRequest getPostsInRadiusRequest);

    void deletePostById(HttpServletRequest request, Long postId);

    GenericListResponse<PostResponse> findUserPostsWithPagination(HttpServletRequest request, Long userId, int take, int skip);

    GenericListResponse<PostResponse> findUserFutureTrips(HttpServletRequest request, Long userId);

    GenericListResponse<PostResponse> findUserFinishedTrips(HttpServletRequest request, Long userId, Long postsAfterDaysToShow);
}
