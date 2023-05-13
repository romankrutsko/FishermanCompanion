package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PostService {
    PostResponse createPost(HttpServletRequest request, CreatePostRequest createPostRequest);

    GenericListResponse<PostResponse> findAllPosts(int take, int skip);

    void updatePostInfo(HttpServletRequest request, UpdatePostRequest updatePostRequest);

    GenericListResponse<PostResponse> findPostsNearLocation(GetPostsInRadiusByCategoryRequest getPostsInRadiusRequest);

    void deletePostById(HttpServletRequest request, Long postId);

    GenericListResponse<PostResponse> findUserPostsWithPagination(HttpServletRequest request, int take, int skip);
}
