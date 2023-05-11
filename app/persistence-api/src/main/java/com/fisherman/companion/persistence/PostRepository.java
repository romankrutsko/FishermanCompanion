package com.fisherman.companion.persistence;

import java.time.LocalDateTime;
import java.util.List;

import com.fisherman.companion.dto.PostDto;

public interface PostRepository {
    void savePost(PostDto postDto);

    List<PostDto> findAllCategoriesPosts(int take, int skip, LocalDateTime timeToFilter);

    void updatePostById(PostDto postDto);

    void deleteById(Long id);
}
