package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.CreatePostRequest;
import com.fisherman.companion.dto.request.GetPostsByCategoryRequest;
import com.fisherman.companion.dto.request.GetPostsInRadiusByCategoryRequest;
import com.fisherman.companion.dto.request.UpdatePostRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PostResponse;
import com.fisherman.companion.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    PostResponse createPost(HttpServletRequest request, @RequestBody final CreatePostRequest createPostRequest) {
        return postService.createPost(request, createPostRequest);
    }

    @GetMapping("/{userId}")
    GenericListResponse<PostResponse> findCurrentUserPostsWithPagination(HttpServletRequest request, @RequestParam(value = "skip", defaultValue = "0") final Integer skip,
                                                                        @RequestParam(value = "take", defaultValue = "10") final Integer take, @PathVariable(value = "userId") Long userId) {
        return postService.findUserPostsWithPagination(request, userId, take, skip);
    }

    @PatchMapping("/{postId}")
    PostResponse updatePost(HttpServletRequest request, @RequestBody final UpdatePostRequest updatePostRequest, @PathVariable(value = "postId") Long postId) {
        return postService.updatePostInfo(request, updatePostRequest, postId);
    }

    @PostMapping("/by-category")
    GenericListResponse<PostResponse> findPostsByCategory(@RequestBody final GetPostsByCategoryRequest postsByCategoryRequest,
                                                          @RequestParam(value = "skip", defaultValue = "0") final Integer skip,
                                                          @RequestParam(value = "take", defaultValue = "10") final Integer take) {
        return postService.findPostsByCategory(postsByCategoryRequest, skip, take);
    }

    @PostMapping("/by-location")
    GenericListResponse<PostResponse> findPostsByLocationAndCategory(@RequestBody final GetPostsInRadiusByCategoryRequest radiusByCategoryRequest) {
        return postService.findPostsNearLocation(radiusByCategoryRequest);
    }

    @GetMapping
    GenericListResponse<PostResponse> findAllPosts(@RequestParam(value = "skip", defaultValue = "0") final Integer skip,
                                                   @RequestParam(value = "take", defaultValue = "10") final Integer take) {
        return postService.findAllPosts(take, skip);
    }

    @GetMapping("/future/{userId}")
    GenericListResponse<PostResponse> findUserFuturePosts(HttpServletRequest request, @PathVariable(value = "userId") Long userId) {
        return postService.findUserFutureTrips(request, userId);
    }

    @GetMapping("/finished/{userId}")
    GenericListResponse<PostResponse> findUserFinishedPosts(HttpServletRequest request, @RequestParam(value = "days", defaultValue = "7") final Long days,
                                                            @PathVariable(value = "userId") Long userId) {
        return postService.findUserFinishedTrips(request, userId, days);
    }

    @DeleteMapping("/{postId}")
    void deletePostById(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        postService.deletePostById(request, postId);
    }
}
