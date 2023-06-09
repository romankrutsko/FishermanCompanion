package com.fisherman.companion.persistence;

import static com.fisherman.companion.dto.utils.DateTimeUtil.getCurrentUkrDateTime;
import static com.fisherman.companion.dto.utils.DateTimeUtil.getUkrDateTimePlusDays;
import static com.fisherman.companion.dto.utils.DateTimeUtil.trimSeconds;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<PostDto> findAllCategoriesPosts(final GetPostsPaginationParams paginationParams, final Long userId) {
        final String sql = """
                SELECT * FROM posts
                WHERE start_date >= :startDate AND user_id!=:userId
                ORDER BY start_date
                LIMIT :take
                OFFSET :skip
                """;

        final Map<String, Object> params = Map.of(
                "userId", userId,
                "startDate", getCurrentUkrDateTime(),
                "take", paginationParams.take(),
                "skip", paginationParams.skip()
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public Long countPostsAllCategories(final Long userId) {
        final String sql = """
            SELECT COUNT(id) FROM posts
            WHERE start_date >= :startDate AND user_id != :userId
            ORDER BY start_date
            """;

        final Map<String, Object> params = Map.of(
                "userId", userId,
                "startDate", getCurrentUkrDateTime()
        );

        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }


    @Override
    public List<PostDto> findPostsByCategory(final GetPostsPaginationParams paginationParams, final Long categoryId, final Long userId) {
        final String sql = """
                SELECT * FROM posts
                WHERE start_date >= :startDate AND category_id = :category AND user_id!=:userId
                ORDER BY start_date
                LIMIT :take
                OFFSET :skip
                """;

        final Map<String, Object> params = Map.of(
                "userId", userId,
                "startDate", getCurrentUkrDateTime(),
                "take", paginationParams.take(),
                "skip", paginationParams.skip(),
                "category", categoryId
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public Long countPostsByCategory(final Long categoryId, final Long userId) {
        final String sql = """
            SELECT COUNT(id) FROM posts
            WHERE start_date >= :startDate AND category_id = :category AND user_id != :userId
            ORDER BY start_date
            """;

        final Map<String, Object> params = Map.of(
                "userId", userId,
                "category", categoryId,
                "startDate", getCurrentUkrDateTime()
        );

        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    @Override
    public List<PostDto> findPostsInBoundingBoxByCategory(final BoundingBoxDimensions boxDimensions, final Long categoryId, final Long userId) {
        String sql = """
                SELECT * FROM posts
                WHERE start_date BETWEEN :from AND :to
                    AND latitude BETWEEN :minLat AND :maxLat
                    AND longitude BETWEEN :minLng AND :maxLng
                    AND category_id = :category AND user_id!=:userId
                ORDER BY start_date;
                """;
        final Map<String, Object> params = Map.of(
                "userId", userId,
                "minLat", boxDimensions.minLat(),
                "minLng", boxDimensions.minLng(),
                "maxLat", boxDimensions.maxLat(),
                "maxLng", boxDimensions.maxLng(),
                "from", getCurrentUkrDateTime(),
                "to", getUkrDateTimePlusDays(7),
                "category", categoryId
        );

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithPagination(final Long userId, final int take, final int skip) {
        final String sql = """
                SELECT *
                FROM posts
                WHERE user_id = :userId AND start_date > :now
                ORDER BY start_date DESC
                LIMIT :take
                OFFSET :skip
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("take", take)
                .addValue("skip", skip)
                .addValue("now", getCurrentUkrDateTime());

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithFutureStartDate(final Long userId) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE p.user_id = :userId AND p.start_date > :now
                AND r.status = 'accepted'
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("now", getCurrentUkrDateTime());

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findUserPostsWithStartDateInPast(final Long userId, final String timeFilterTo) {
        final String sql = """
                SELECT *
                FROM posts
                WHERE user_id = :userId AND start_date between :timeFilterTo AND :now
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("timeFilterTo", timeFilterTo)
                .addValue("now", getCurrentUkrDateTime());

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findPostsWithRequestsFromUserByUserId(final Long userId) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE r.user_id = :userId
                AND p.start_date > :now AND r.status = 'accepted'
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("now", getCurrentUkrDateTime());

        return namedParameterJdbcTemplate.query(sql, params, new PostMapper());
    }

    @Override
    public List<PostDto> findPostsWithRequestFromUserInPast(final Long userId, final String timeFilterTo) {
        final String sql = """
                SELECT p.*
                FROM posts p
                JOIN requests r ON r.post_id = p.id
                WHERE r.user_id = :userId
                AND r.status = 'accepted'
                AND start_date between :timeFilterTo AND :now
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("timeFilterTo", timeFilterTo)
                .addValue("now", getCurrentUkrDateTime());

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
            post.setStartDate(trimSeconds(rs.getString("start_date")));
            post.setLatitude(rs.getDouble("latitude"));
            post.setLongitude(rs.getDouble("longitude"));
            post.setContactInfo(rs.getString("contact_info"));

            return post;
        }
    }
}
