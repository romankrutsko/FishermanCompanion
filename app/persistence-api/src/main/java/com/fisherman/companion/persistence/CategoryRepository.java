package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.CategoryDto;

public interface CategoryRepository {
    List<CategoryDto> getListCategories();
    String findCategoryNameById(Long categoryId);
}
