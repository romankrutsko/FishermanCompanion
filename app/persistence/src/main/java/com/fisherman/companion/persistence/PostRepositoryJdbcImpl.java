package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.BoundingBoxDimensions;
import com.fisherman.companion.dto.CategoryDto;
import com.fisherman.companion.dto.GetPostsPaginationParams;
import com.fisherman.companion.dto.PostDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJdbcImpl implements PostRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Long savePost(final PostDto postDto) {
        final String sql = """
                INSERT INTO posts (user_id, category_id, title, description, start_date, latitude, longitude, contact_info)
                VALUES (:userId, :categoryId, :title, :description, :startDate, :latitude, :longitude, :contactInfo)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", postDto.getUserId())
                .addValue("categoryId", postDto.getCategory().getId())
                .addValue("title", postDto.getTitle())
                .addValue("description", postDto.getDescription())
                .addValue("startDate", postDto.getStartDate())
                .addValue("latitude", postDto.getLatitude())
                .addValue("longitude", postDto.getLongitude())
                .addValue("contactInfo", postDto.getContactInfo());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public PostDto findPostById(final Long postId) {
        final String sql = """
                SELECT * FROM posts
                WHERE id = :id
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("id", postId), new PostMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public List<PostDto> findAllCategoriesPosts(final GetPostsPaginationParams paginationParams) {
        final String sql = """
                SELECT * FROM posts
                WHERE start_date >= :startDate
                ORDER BY start_date
                LIMIT :take
                OFFSET :skip
                """;

        final Map<String, Object> params = Map.of(
                "startDate", Timestamp.valueOf(paginationParams.timeToFilter()),
                "take", paginationParams.take(),
                "skip", paginationParams.skip()
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }


    @Override
    public List<PostDto> findPostsByCategory(final GetPostsPaginationParams paginationParams, final Long categoryId) {
        final String sql = """
                SELECT * FROM posts
                WHERE start_date >= :startDate AND category_id = :category
                ORDER BY start_date
                LIMIT :take
                OFFSET :skip
                """;

        final Map<String, Object> params = Map.of(
                "startDate", Timestamp.valueOf(paginationParams.timeToFilter()),
                "take", paginationParams.take(),
                "skip", paginationParams.skip(),
                "category", categoryId
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findPostsInBoundingBoxByCategory(final BoundingBoxDimensions boxDimensions, final LocalDateTime from, final LocalDateTime to, final Long categoryId) {
        String sql = """
                SELECT * FROM posts
                WHERE start_date BETWEEN :from AND :to
                    AND latitude BETWEEN :minLat AND :maxLat
                    AND longitude BETWEEN :minLng AND :maxLng
                    AND category_id = :category
                ORDER BY start_date;
                """;
        final Map<String, Object> params = Map.of(
                "minLat", boxDimensions.minLat(),
                "minLng", boxDimensions.minLng(),
                "maxLat", boxDimensions.maxLat(),
                "maxLng", boxDimensions.maxLng(),
                "from", Timestamp.valueOf(from),
                "to", Timestamp.valueOf(to),
                "category", categoryId
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithPagination(final Long userId, final int take, final int skip) {
        final String sql = """
                SELECT *
                FROM posts
                WHERE user_id = :userId AND start_date > NOW()
                ORDER BY start_date DESC
                LIMIT :take
                OFFSET :skip
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("take", take)
                .addValue("skip", skip);

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithFutureStartDate(final Long userId) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE p.user_id = :userId AND p.start_date > NOW() 
                AND r.status = 'accepted'        
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithStartDateInPast(final Long userId, final LocalDateTime timeFilterTo) {
        final String sql = """
                SELECT *
                FROM posts
                WHERE user_id = :userId AND start_date between :timeFilterTo AND NOW()         
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("timeFilterTo", Timestamp.valueOf(timeFilterTo));

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findPostsWithRequestsFromUserByUserId(final Long userId) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE r.user_id = :userId
                AND p.start_date > NOW() AND r.status = 'accepted'
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), new PostMapper());
    }

    @Override
    public List<PostDto> findPostsWithRequestFromUserInPast(final Long userId, final LocalDateTime timeFilterTo) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE r.user_id = :userId
                AND r.status = 'accepted'
                AND start_date between :timeFilterTo AND NOW()          
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("timeFilterTo", Timestamp.valueOf(timeFilterTo));

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<Long> findAllUserPostsIds(final Long userId) {
        final String sql = """
                SELECT id
                FROM posts
                WHERE user_id = :userId
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> rs.getLong("id"));
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
                    contact_info = COALESCE(:contactInfo, contact_info)
                WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", postDto.getUserId())
                .addValue("categoryId", postDto.getCategory().getId())
                .addValue("title", postDto.getTitle())
                .addValue("description", postDto.getDescription())
                .addValue("startDate", postDto.getStartDate())
                .addValue("latitude", postDto.getLatitude())
                .addValue("longitude", postDto.getLongitude())
                .addValue("contactInfo", postDto.getContactInfo())
                .addValue("id", postDto.getId());

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
            final CategoryDto category = new CategoryDto();

            category.setId(rs.getLong("category_id"));

            final PostDto post = new PostDto();

            post.setId(rs.getLong("id"));
            post.setUserId(rs.getLong("user_id"));
            post.setCategory(category);
            post.setTitle(rs.getString("title"));
            post.setDescription(rs.getString("description"));
            post.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
            post.setLatitude(rs.getDouble("latitude"));
            post.setLongitude(rs.getDouble("longitude"));
            post.setContactInfo(rs.getString("contact_info"));

            return post;
        }
    }
}
