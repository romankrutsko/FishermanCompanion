package com.fisherman.companion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private Long userId;
    private Long postId;
    private String comment;
    private RequestStatus status;
}
