package com.fisherman.companion.persistence;

import java.time.LocalDateTime;
import java.util.List;

import com.fisherman.companion.dto.BoundingBoxDimensions;
import com.fisherman.companion.dto.PostDto;

public interface PostRepository {
    Long savePost(PostDto postDto);

    List<PostDto> findAllCategoriesPosts(int take, int skip, LocalDateTime timeToFilter);

    List<PostDto> findPostsInBoundingBoxByCategory(BoundingBoxDimensions boxDimensions, LocalDateTime timeFrom, LocalDateTime timeTo, Long categoryId);

    List<PostDto> findUserPosts(Long userId, int take, int skip);

    void updatePostById(PostDto postDto);

    void deleteById(Long id);
}
