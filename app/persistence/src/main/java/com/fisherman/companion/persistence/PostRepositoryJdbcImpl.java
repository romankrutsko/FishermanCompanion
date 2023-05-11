package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.PostStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJdbcImpl implements PostRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void savePost(final PostDto postDto) {
        final String sql = """
                INSERT INTO posts (user_id, category_id, title, description, start_date, latitude, longitude, contact_info, status)
                VALUES (:userId, :categoryId, :title, :description, :startDate, :latitude, :longitude, :contactInfo, :status)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", postDto.userId())
                .addValue("categoryId", postDto.category().id())
                .addValue("title", postDto.title())
                .addValue("description", postDto.description())
                .addValue("startDate", postDto.startDate())
                .addValue("latitude", postDto.latitude())
                .addValue("longitude", postDto.longitude())
                .addValue("contactInfo", postDto.contactInfo())
                .addValue("status", postDto.status().name());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<PostDto> findAllCategoriesPosts(final int take, final int skip, final LocalDateTime timeToFilter) {
        final String sql = """
                SELECT p.id, p.user_id, p.category_id, p.title, p.description, p.start_date, p.latitude, p.longitude, p.contact_info, p.status, c.name as category_name
                FROM posts p
                LEFT JOIN categories c ON p.category_id = c.id
                WHERE p.status = 'opened' AND p.start_date >= :startDate
                ORDER BY p.start_date
                LIMIT :take
                OFFSET :skip
                """;

        final Map<String, Object> params = Map.of(
                "startDate", Timestamp.valueOf(timeToFilter),
                "take", take,
                "skip", skip
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public void updatePostById(final PostDto postDto) {
        final String sql = """
                UPDATE posts
                SET user_id = COALESCE(:userId, user_id),
                    category_id = COALESCE(:categoryId, category_id),
                    title = COALESCE(:title, title),
                    description = COALESCE(:description, description),
                    start_date = COALESCE(:startDate, start_date),
                    latitude = COALESCE(:latitude, latitude),
                    longitude = COALESCE(:longitude, longitude),
                    contact_info = COALESCE(:contactInfo, contact_info),
                    status = COALESCE(:status, status)
                WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postDto.id())
                .addValue("categoryId", postDto.category().id())
                .addValue("title", postDto.title())
                .addValue("description", postDto.description())
                .addValue("startDate", postDto.startDate())
                .addValue("latitude", postDto.latitude())
                .addValue("longitude", postDto.longitude())
                .addValue("contactInfo", postDto.contactInfo())
                .addValue("status", postDto.status().name())
                .addValue("id", postDto.id());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = """
                DELETE FROM posts WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

    private static class PostMapper implements RowMapper<PostDto> {
        @Override
        public PostDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return PostDto.builder()
                          .id(rs.getLong("id"))
                          .userId(rs.getLong("user_id"))
                          .category(new CategoryDto(rs.getLong("category_id"), rs.getString("category_name")))
                          .title(rs.getString("title"))
                          .description(rs.getString("description"))
                          .startDate(rs.getTimestamp("start_date").toLocalDateTime())
                          .latitude(rs.getDouble("latitude"))
                          .longitude(rs.getDouble("longitude"))
                          .contactInfo(rs.getString("contact_info"))
                          .status(PostStatus.valueOf(rs.getString("status")))
                          .build();
        }
    }
}
