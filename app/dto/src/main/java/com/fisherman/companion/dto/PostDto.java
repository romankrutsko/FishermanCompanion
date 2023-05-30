package com.fisherman.companion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private Long userId;
    private CategoryDto category;
    private String title;
    private String description;
    private String startDate;
    private Double latitude;
    private Double longitude;
    private String contactInfo;
}