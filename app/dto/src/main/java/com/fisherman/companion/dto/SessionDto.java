package com.fisherman.companion.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;
}
