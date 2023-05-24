package com.fisherman.companion.persistence;

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

import com.fisherman.companion.dto.RequestStatus;
import com.fisherman.companion.dto.RequestDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RequestsRepositoryImpl implements RequestsRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Long createRequest(final RequestDto request) {
        final String sql = """
            INSERT INTO requests (user_id, post_id, comment, status)
            VALUES (:userId, :postId, :comment, :status)
            """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", request.getUserId())
                .addValue("postId", request.getPostId())
                .addValue("comment", request.getComment())
                .addValue("status", request.getStatus().getCode());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public RequestDto getRequestById(final Long requestId) {
        final String sql = """
            SELECT id, user_id, post_id, comment, status
            FROM requests
            WHERE id = :id
            """;

        return namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", requestId), new RequestMapper());
    }

    @Override
    public List<RequestDto> getRequestsByUserId(final Long userId) {
        final String sql = """
            SELECT id, user_id, post_id, comment, status
            FROM requests
            WHERE user_id = :userId
            """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), new RequestMapper());
    }

    @Override
    public List<RequestDto> getRequestsByPostId(final Long postId) {
        final String sql = """
            SELECT id, user_id, post_id, comment, status
            FROM requests
            WHERE post_id = :postId AND status IN ('pending', 'accepted')
            """;

        return namedParameterJdbcTemplate.query(sql, Map.of("postId", postId), new RequestMapper());
    }

    @Override
    public List<Long> getUserIdsOfAcceptedRequestsByPostId(final Long postId, final Long userId) {
        final String sql = """
                SELECT user_id FROM requests
                WHERE post_id = :postId
                AND status = 'accepted' AND user_id != :userId
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId);

        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }

    @Override
    public void updateRequest(final RequestDto request) {
        final String sql = """
            UPDATE requests
            SET comment = :comment
            WHERE id = :id
            """;

        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("comment", request.getComment())
                .addValue("id", request.getId());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void updateRequestStatus(final RequestDto request) {
        final String sql = """
            UPDATE requests
            SET status = :status
            WHERE id = :id
            """;

        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("status", request.getStatus().getCode())
                .addValue("id", request.getId());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void deleteRequest(final Long requestId) {
        final String sql = """
            DELETE FROM requests WHERE id = :id
            """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", requestId));
    }

    private static class RequestMapper implements RowMapper<RequestDto> {
        @Override
        public RequestDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return RequestDto.builder()
                          .id(rs.getLong("id"))
                          .userId(rs.getLong("user_id"))
                          .postId(rs.getLong("post_id"))
                          .comment(rs.getString("comment"))
                          .status(RequestStatus.valueOf(rs.getString("status").toUpperCase()))
                          .build();
        }
    }
}
