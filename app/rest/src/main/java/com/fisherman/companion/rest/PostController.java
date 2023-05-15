package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;
import com.fisherman.companion.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    PostResponse createPost(HttpServletRequest request, @RequestBody final CreatePostRequest createPostRequest) {
        return postService.createPost(request, createPostRequest);
    }

    @GetMapping("/user/get")
    GenericListResponse<PostResponse> findCurrentUserPostsWithPagination(HttpServletRequest request, @RequestParam(value = "skip", defaultValue = "0") final Integer skip,
                                                                        @RequestParam(value = "take", defaultValue = "10") final Integer take) {
        return postService.findUserPostsWithPagination(request, take, skip);
    }

    @PostMapping("/update/{postId}")
    void updatePost(HttpServletRequest request, @RequestBody final UpdatePostRequest updatePostRequest, @PathVariable(value = "postId") Long postId) {
        postService.updatePostInfo(request, updatePostRequest, postId);
    }

    @PostMapping("/get/by-location")
    GenericListResponse<PostResponse> findPostsByLocationAndCategory(@RequestBody final GetPostsInRadiusByCategoryRequest radiusByCategoryRequest) {
        return postService.findPostsNearLocation(radiusByCategoryRequest);
    }

    @GetMapping("/get/all")
    GenericListResponse<PostResponse> findAllPosts(@RequestParam(value = "skip", defaultValue = "0") final Integer skip,
                                                   @RequestParam(value = "take", defaultValue = "10") final Integer take) {
        return postService.findAllPosts(take, skip);
    }

    @DeleteMapping("/{postId}")
    void deletePostById(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        postService.deletePostById(request, postId);
    }
}
