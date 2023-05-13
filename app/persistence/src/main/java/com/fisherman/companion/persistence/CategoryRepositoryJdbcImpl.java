package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.CategoryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryJdbcImpl implements CategoryRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<CategoryDto> getListCategories() {
        final String sql = """
                SELECT id, name FROM categories
                """;

        return namedParameterJdbcTemplate.query(sql, new CategoryMapper());
    }

    @Override
    public String findCategoryNameById(final Long categoryId) {
        final String sql = """
                SELECT name FROM categories WHERE id = :categoryId
                """;

        return namedParameterJdbcTemplate.queryForObject(sql, Map.of("categoryId", categoryId), String.class);
    }

    private static class CategoryMapper implements RowMapper<CategoryDto> {
        @Override
        public CategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            final CategoryDto category = new CategoryDto();

            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));

            return category;
        }
    }

}
