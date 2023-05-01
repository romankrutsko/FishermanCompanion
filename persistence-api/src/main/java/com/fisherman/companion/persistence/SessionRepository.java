package com.fisherman.companion.persistence;

import java.time.LocalDateTime;

import com.fisherman.companion.dto.SessionDto;

public interface SessionRepository {

    void saveSession(SessionDto sessionDto);

    Integer getUserIdByToken(String token);

    void deleteExpiredSessions(LocalDateTime currentTime);
}
