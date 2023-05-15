package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.ProfileDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryJdbcImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Long saveProfile(final ProfileDto profile) {
        final String sql = """
                INSERT INTO profiles (user_id, full_name, bio, location, contacts)
                VALUES (:userId, :fullName, :bio, :location, :contacts)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", profile.getUserId())
                .addValue("fullName", profile.getFullName())
                .addValue("bio", profile.getBio())
                .addValue("location", profile.getLocation())
                .addValue("contacts", profile.getContacts());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void updateProfileAvatar(final Long userId, final String avatar) {
        final String sql = """
                UPDATE profiles
                SET avatar = :avatar
                WHERE user_id = :userId
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("avatar", avatar);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public ProfileDto findProfileById(final Long id) {
        final String sql = """
                SELECT id, user_id, full_name, avatar, bio, location, contacts
                FROM profiles
                WHERE id = :id
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), new ProfileMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public ProfileDto findProfileByUserId(final Long userId) {
        final String sql = """
                SELECT id, user_id, full_name, avatar, bio, location, contacts
                FROM profiles
                WHERE user_id = :userId
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), new ProfileMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public void updateProfile(final ProfileDto profile) {
        final String sql = """
                UPDATE profiles
                SET full_name = COALESCE(:fullName, full_name),
                    bio = COALESCE(:bio, bio),
                    location = COALESCE(:location, location),
                    contacts = COALESCE(:contacts, contacts)
                WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", profile.getId())
                .addValue("fullName", profile.getFullName())
                .addValue("bio", profile.getBio())
                .addValue("location", profile.getLocation())
                .addValue("contacts", profile.getContacts());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteProfileByUserId(final Long profileId) {
        final String sql = """
                DELETE FROM profiles WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", profileId));
    }

    private static class ProfileMapper implements RowMapper<ProfileDto> {
        @Override
        public ProfileDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return ProfileDto.builder()
                             .id(rs.getLong("id"))
                             .userId(rs.getLong("user_id"))
                             .fullName(rs.getString("full_name"))
                             .avatar(rs.getString("avatar"))
                             .bio(rs.getString("bio"))
                             .location(rs.getString("location"))
                             .contacts(rs.getString("contacts"))
                             .build();
        }
    }
}
