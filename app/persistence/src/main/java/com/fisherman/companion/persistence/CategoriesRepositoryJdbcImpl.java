package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.CategoryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoriesRepositoryJdbcImpl implements CategoriesRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<CategoryDto> getListCategories() {
        final String sql = """
                SELECT id, name FROM categories
                """;

        return namedParameterJdbcTemplate.query(sql, new CategoryMapper());
    }

    private static class CategoryMapper implements RowMapper<CategoryDto> {
        @Override
        public CategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CategoryDto.builder()
                              .id(rs.getLong("id"))
                              .name(rs.getString("name"))
                              .build();
        }
    }

}
