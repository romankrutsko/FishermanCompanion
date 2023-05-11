package com.fisherman.companion.service;

import java.util.List;

import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.request.AllCategoriesPostsRequest;
import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface PostService {
    void createPost(HttpServletRequest request, CreatePostRequest createPostRequest);

    List<PostDto> findAllPosts(AllCategoriesPostsRequest postsRequest);

    void updatePostInfo(HttpServletRequest request, UpdatePostRequest updatePostRequest);
}
