package com.fisherman.companion.persistence;

import java.time.LocalDateTime;

import com.fisherman.companion.dto.SessionDto;

public interface SessionRepository {

    void saveSession(final SessionDto sessionDto);

    Long getUserIdByToken(final String token);

    void deleteExpiredSessions(final LocalDateTime currentTime);

    void deleteSessionByToken(final String token);
}
