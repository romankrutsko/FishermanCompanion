package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.CategoryDto;

public interface CategoriesRepository {
    List<CategoryDto> getListCategories();
    String findCategoryNameById(Long categoryId);
}
