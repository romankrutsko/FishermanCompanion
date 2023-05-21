package com.fisherman.companion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    private final String code;
}
