package com.fisherman.companion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String avatar;
    private String bio;
    private String location;
    private String contacts;
    private Double averageRating;
    private String role;
}
