package com.fisherman.companion.persistence;

import java.time.LocalDateTime;
import java.util.List;

import com.fisherman.companion.dto.BoundingBoxDimensions;
import com.fisherman.companion.dto.GetPostsPaginationParams;
import com.fisherman.companion.dto.PostDto;

public interface PostRepository {
    Long savePost(PostDto postDto);

    PostDto findPostById(Long postId);

    List<PostDto> findAllCategoriesPosts(GetPostsPaginationParams params);

    List<PostDto> findPostsByCategory(GetPostsPaginationParams paginationParams, Long categoryId);

    List<PostDto> findPostsInBoundingBoxByCategory(BoundingBoxDimensions boxDimensions, LocalDateTime timeFrom, LocalDateTime timeTo, Long categoryId);

    List<PostDto> findUserPostsWithPagination(Long userId, int take, int skip);

    List<PostDto> findUserPostsWithFutureStartDate(Long userId);

    List<PostDto> findUserPostsWithStartDateInPast(Long userId, LocalDateTime timeToFilter);

    List<PostDto> findPostsWithRequestsFromUserByUserId(Long userId);

    List<PostDto> findPostsWithRequestFromUserInPast(Long userId, LocalDateTime timeFilterTo);

    List<Long> findAllUserPostsIds(Long userId);

    void updatePostById(PostDto postDto);

    void deleteById(Long id);
}
