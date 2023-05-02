package com.fisherman.companion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String avatar;
    private String bio;
    private String location;
    private String website;
}
