package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.request.CreateRatingRequest;
import com.fisherman.companion.dto.RatingDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryJdbcImpl implements RatingRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createRating(final CreateRatingRequest request, final Long ratedBy) {
        final String sql = """
                INSERT INTO ratings (user_id, post_id, rated_by, rating, comment)
                VALUES (:userId, :postId, :ratedBy, :rating, :comment)
                """;

        final Map<String, Object> params = Map.of(
                "userId", request.userId(),
                "postId", request.postId(),
                "ratedBy", ratedBy,
                "rating", request.rating(),
                "comment", request.comment()
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Double getAverageRatingForUser(final Long userId) {
        final String sql = """
                SELECT AVG(rating) AS avg_rating
                FROM ratings
                WHERE user_id = :userId
                """;

        return namedParameterJdbcTemplate.queryForObject(sql, Map.of("userId", userId), Double.class);
    }

    @Override
    public List<RatingDto> getRatingsWithCommentsForUser(final Long userId) {
        final String sql = """
                SELECT * FROM ratings
                WHERE user_id = :userId
                ORDER BY id DESC
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), new RatingMapper());
    }

    @Override
    public boolean canUserRate(final Long postId, final Long currentUserId, final Long userIdToCheck) {
        final String sql = """
                SELECT NOT EXISTS (
                    SELECT 1
                    FROM ratings
                    WHERE post_id = :postId
                      AND rated_by = :ratedBy
                      AND user_id = :userId
                ) AS no_results;
                """;

        final Map<String, Object> params = Map.of(
                "userId", userIdToCheck,
                "postId", postId,
                "ratedBy", currentUserId
        );

        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class));
    }

    @Override
    public void deleteRatingById(final Long ratingId) {
        final String sql = """
                DELETE FROM ratings WHERE id = :ratingId
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("ratingId", ratingId));
    }

    private static class RatingMapper implements RowMapper<RatingDto> {
        @Override
        public RatingDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return RatingDto.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getLong("user_id"))
                            .ratedBy(rs.getLong("rated_by"))
                            .rating(rs.getInt("rating"))
                            .comment(rs.getString("comment"))
                            .build();
        }
    }

}
