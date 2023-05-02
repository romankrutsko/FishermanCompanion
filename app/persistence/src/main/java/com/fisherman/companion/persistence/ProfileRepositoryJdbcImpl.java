package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.ProfileDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryJdbcImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveProfile(final ProfileDto profile) {
        final String sql = """
                INSERT INTO profiles (user_id, full_name, avatar, bio, location, website)
                VALUES (:userId, :fullName, :avatar, :bio, :location, :website)
                """;

        final Map<String, Object> params = Map.of(
                "userId", profile.getUserId(),
                "fullName", profile.getFullName(),
                "avatar", profile.getAvatar(),
                "bio", profile.getBio(),
                "location", profile.getLocation(),
                "website", profile.getWebsite()
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public ProfileDto findProfileById(final Long id) {
        final String sql = """
                SELECT id, user_id, full_name, avatar, bio, location, website
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
                SELECT id, user_id, full_name, avatar, bio, location, website
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
                SET full_name = :fullName,
                    avatar = :avatar,
                    bio = :bio,
                    location = :location,
                    website = :website
                WHERE id = :id
                """;

        final Map<String, Object> params = Map.of(
                "id", profile.getId(),
                "fullName", profile.getFullName(),
                "avatar", profile.getAvatar(),
                "bio", profile.getBio(),
                "location", profile.getLocation(),
                "website", profile.getWebsite()
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteProfileById(final Long id) {
        final String sql = """
                DELETE FROM profiles WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
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
                             .website(rs.getString("website"))
                             .build();
        }
    }
}
