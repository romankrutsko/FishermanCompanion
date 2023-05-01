package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.ProfileDto;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class ProfileRepositoryJdbcImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveProfile(ProfileDto profile) {
        final var sql = """
                INSERT INTO profiles (user_id, full_name, avatar, bio, location, website)
                VALUES (:userId, :fullName, :avatar, :bio, :location, :website)
                """;

        Map<String, Object> params = Map.of(
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
    public Optional<ProfileDto> findProfileById(Long id) {
        final var sql = """
                SELECT id, user_id, full_name, avatar, bio, location, website
                FROM profiles
                WHERE id = :id
                """;
        Map<String, Object> params = Map.of("id", id);
        final var result = namedParameterJdbcTemplate.query(sql, params, new ProfileMapper());
        return result.stream().findFirst();
    }

    @Override
    public Optional<ProfileDto> findProfileByUserId(Long userId) {
        final var sql = """
                SELECT id, user_id, full_name, avatar, bio, location, website
                FROM profiles
                WHERE user_id = :userId
                """;
        Map<String, Object> params = Map.of("userId", userId);
        final var result = namedParameterJdbcTemplate.query(sql, params, new ProfileMapper());
        return result.stream().findFirst();
    }

    @Override
    public void updateProfile(ProfileDto profile) {
        final var sql = """
                UPDATE profiles
                SET full_name = :fullName,
                    avatar = :avatar,
                    bio = :bio,
                    location = :location,
                    website = :website
                WHERE id = :id
                """;

        Map<String, Object> params = Map.of(
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
    public void deleteProfileById(Long id) {
        final var sql = """
                DELETE FROM profiles WHERE id = :id
                """;
        final var paramMap = Map.of("id", id);
        namedParameterJdbcTemplate.update(sql, paramMap);
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
